<?php

namespace EI\Database;
use EI\Logic\ItemStack;
use EI\Logic\Storage;
use EI\Logic\Warehouse;
use EI\Logic\Item;
use EI\Logic\Unit;

trait StorageDB {
    use BaseTrait;

    /**
     * @return Storage[]
     */
    public function getStorages(string $warehouse_id,string|null $q,int $offset,int $len,bool $archived):array {
        $res=$this->fetchAllPreparedOrExit("select warehouse_name,warehouse_address,warehouse_deleted, id, name,deleted from storages_view where warehouse = ?  and
        (? is null or locate(?,name)>0 or locate(?,id)>0) and (?=1 or deleted is null) order by name limit ?,?",'ssssiii',$warehouse_id,$q,$q,$q,$archived,$offset,$len);
        foreach($res as &$row){
            $row=new Storage(
                new Warehouse(
                    $warehouse_id,
                    $row["warehouse_name"],
                    $row["warehouse_address"],
                    $row["warehouse_deleted"]
                ),
                $row["id"],
                $row["name"],
                $row["deleted"]
            );
        }
        return $res;
    }

    public function getStorage(string $warehouse_id,string $storage_id):Storage|null {
        $row=$this->fetchPreparedOrExit("select warehouse_name,warehouse_address,warehouse_deleted, id, name,deleted from storages_view where warehouse = ? and id=?;",'ss',$warehouse_id,$storage_id);
        if($row!==null){
            $row=new Storage(
                new Warehouse(
                    $warehouse_id,
                    $row["warehouse_name"],
                    $row["warehouse_address"],
                    $row["warehouse_deleted"]
                ),
                $row["id"],
                $row["name"],
                $row["deleted"]
            );
        }
        return $row;
    }

    private function tryCreateStorage(int $internal_warehouse_id,string $storage_id,string $name):int {
        $modified=0;
        $this->executePrepared2("insert ignore into storages (warehouse,external_id,name) values (?,?,?)",$modified,"iss",
            $internal_warehouse_id,
            $storage_id,
            $name);
        return $modified;
    }

    private function tryCreateOrUpdateStorage(int $internal_warehouse_id,string $storage_id,string $name):int {
        $modified=0;
        $this->executePrepared2("insert ignore into storages (warehouse,external_id,name) values (?,?,?) on duplicate key update
            warehouse = values(warehouse),
            external_id = values(external_id),
            name = values(name)",$modified,"iss",
            $internal_warehouse_id,
            $storage_id,
            $name);
        return $modified;
    }

    private function getWarehouseInternalId(string $warehouse_id):int {
        return $this->executePrepared("select id from warehouses where external_id=?","s",$warehouse_id)->fetch_assoc()["id"];
    }

    public function createStorage(string $warehouse_id,string $storage_id,string $name):bool {//TODO simplify
        try{
            $this->getMysqli()->begin_transaction(MYSQLI_TRANS_START_READ_WRITE );

            $internal_warehouse_id=$this->getWarehouseInternalId($warehouse_id);

            if($this->tryCreateStorage($internal_warehouse_id,$storage_id,$name)==0){
                $this->getMysqli()->commit();
                return false;
            }

            $this->getMysqli()->commit();
            return true;
        }catch(\mysqli_sql_exception $e) {
            $this->rollback($e);
        }
    }

    public function createOrUpdateStorage(string $warehouse_id,string $storage_id,string $name):UpsertResult {
        try{
            $this->getMysqli()->begin_transaction(MYSQLI_TRANS_START_READ_WRITE );

            $internal_warehouse_id=$this->getWarehouseInternalId($warehouse_id);
            $modified=$this->tryCreateOrUpdateStorage($internal_warehouse_id,$storage_id,$name);

            if($modified==0){
                $this->getMysqli()->commit();
                return UpsertResult::Failed;
            }else if($modified==1) {
                
            }else{
                //TODO automated => non automated & non automated => automated
            }
            
            $this->getMysqli()->commit();
            return UpsertResult::fromModified($modified);
        }catch(\mysqli_sql_exception $e) {
            $this->rollback($e);
        }
    }

    public function updateStorage(string $warehouse_id,string $storage_id,string $name):bool {
        return $this->executePreparedOrExit("update ignore storages set
            warehouse=(select id from warehouses where external_id=?),
            name=? where external_id=?","sss",
            $warehouse_id,
            $name,
            $storage_id)>0;
    }

    public function deleteStorage(string $warehouse_id,string $storage_id):DeleteResult {
        try{
            $this->getMysqli()->begin_transaction(MYSQLI_TRANS_START_READ_WRITE);

            $res=$this->executePrepared("
            select 0 from item_stacks
                where storage=(select storages.id from storages inner join warehouses on warehouses.external_id=storages.warehouse where warehouses.external_id=? and storages.external_id=?)
            union
            select 0 from operation_items
                inner join operations on operations.id=operation_items.operation
                where storage=(select storages.id from storages inner join warehouses on warehouses.external_id=storages.warehouse where warehouses.external_id=? and storages.external_id=?) and commited is null",
                "ssss",$warehouse_id,$storage_id,$warehouse_id,$storage_id)->fetch_assoc();

            if($res!==null){
                $this->getMysqli()->commit();
                return DeleteResult::Failed;
            }

            $m=0;
            $this->executePrepared2("update storages set deleted=UNIX_TIMESTAMP() where warehouse = (select id from warehouses where external_id=?) and external_id=? and deleted is null",$m,
                "ss",$warehouse_id,$storage_id);
            $this->getMysqli()->commit();
            return $m===0?DeleteResult::Missing:DeleteResult::Deleted;
        }catch(\mysqli_sql_exception $e) {
            $this->rollback($e);
        }
    }

    /*public function renameStorage(string $from, string $to): RenameResult
    {
        $modified =$this->executePreparedOrExit("update storages_view SET id=? where id=?;",'ss',$from,$to);
        if($modified == 0)
        {
            return RenameResult::Failed;
        }
        else
        {
            return RenameResult::Renamed;
        }
    }*/

    /**
     * @param string|null $warehouse_id
     * @param string|null $storage_id
     * @param string|null $search
     * @param bool $warehouse
     * @param bool $storage
     * @param bool $lot
     * @param bool $serial
     * @return ItemStack[]|null
     */
    public function getStorageItems(string|null $warehouse_id,string|null $storage_id,
        string|null $search,bool $warehouse,bool $storage,bool $lot,bool $serial,int $offset,int $len):array|null {

        $items=$this->fetchAllPreparedOrExit("select
            item,
            item_name,
            warehouse,
            warehouse_name,
            warehouse_address,
            storage,
            storage_name,
            unit,
            unit_name,
            lot,
            sum(available_amount) available_amount,
            sum(amount) amount,
            global_serial,
            manufacturer_serial
            from item_stacks_view where
                (warehouse=? or ? is null) and
                (storage=? or ? is null) and
                (
                    locate(?,item_name)>0 or
                    locate(?,lot)>0 or
                    locate(?,manufacturer_serial)>0 or
                    locate(?,global_serial)>0 or
                    ? is null
                )
            group by
                item,
                if(?=1,item_stacks_view.warehouse,null),
                if(?=1,item_stacks_view.storage,null),
                if(?=1,item_stacks_view.lot,null),
                if(?=1,item_stacks_view.global_serial,null)
            order by warehouse_name,storage_name,item_name,lot,global_serial
            limit ?,?",
                "sssssssssiiiiii",
                $warehouse_id,$warehouse_id,
                $storage_id,$storage_id,

                $search,$search,$search,$search,$search,

                $warehouse?1:0,
                $storage?1:0,
                $lot?1:0,
                $serial?1:0,
                $offset,$len);
        foreach($items as &$row) {
            $row=new ItemStack(
                new Item(
                    $row["item"],
                    new Unit(
                        $row["unit"],
                        $row["unit_name"],
                        null
                    ),
                    $row["item_name"],
                    null
                ),
                $row["amount"],
                $row["available_amount"],
                $serial?$row["global_serial"]:null,
                $serial?$row["manufacturer_serial"]:null,
                $lot?$row["lot"]:null,
                $warehouse?new Warehouse(
                    $row["warehouse"],
                    $row["warehouse_name"],
                    $row["warehouse_address"],
                    null
                ):null,
                $storage?new Storage(
                    new Warehouse(
                        $row["warehouse"],
                        $row["warehouse_name"],
                        $row["warehouse_address"],
                        null
                    ),
                    $row["storage"],
                    $row["storage_name"],
                    null
                ):null
            );
        }
        return $items;
    }

    public function moveStorage(string $warehouse_id,string $storage_id,string $new_id):MoveResult {
        return $this->executeMoveOrExit("update storages inner join warehouses on storages.warehouse=warehouses.id
            set storages.external_id=? where warehouses.external_id=? and storages.external_id=? and storages.deleted is null",
            "sss",$new_id,$warehouse_id,$storage_id);
    }
}
