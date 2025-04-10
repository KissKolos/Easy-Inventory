<?php

namespace EI\Database;
use EI\Logic\Warehouse;

trait WarehouseDB {
    use BaseTrait;

    /**
     * @return Warehouse[]
     */
    public function getWarehouses(string|null $q,int $offset,int $len,bool $archived):array {
        $res=$this->fetchAllPreparedOrExit("select external_id id,name,address,deleted from warehouses where
        (? is null or locate(?,name)>0 or locate(?,external_id)>0) and (?=1 or deleted is null) order by name limit ?,?",
        "sssiii",$q,$q,$q,$archived,$offset,$len);
        
        foreach($res as &$row){
            $row=new Warehouse(
                $row["id"],
                $row["name"],
                $row["address"],
                $row["deleted"]
            );
        }
        return $res;
    }

    public function getWarehouse(string $warehouse_id):Warehouse|null {
        $row=$this->fetchPreparedOrExit("select name,address,deleted from warehouses where external_id=?","s",$warehouse_id);
        if($row!==null){
            $row=new Warehouse(
                $warehouse_id,
                $row["name"],
                $row["address"],
                $row["deleted"]
            );
        }
        return $row;
    }

    public function createWarehouse(string $warehouse_id,string $name,string $address):bool {
        $m=$this->executePreparedOrExit("insert ignore into warehouses (external_id,name,address) values (?,?,?)",
            "sss",
            $warehouse_id,
            $name,
            $address)>0;
        return $m;
    }

    public function updateWarehouse(string $warehouse_id,string $name,string $address):bool {
        return $this->executePreparedOrExit("update ignore warehouses set name=?,address=? where external_id=? and deleted is null","sss",
            $name,
            $address,
            $warehouse_id)>0;
    }

    public function createOrUpdateWarehouse(string $warehouse_id,string $name,string $address):UpsertResult {
        return UpsertResult::fromModified($this->executePreparedOrExit("insert ignore into warehouses (external_id,name,address)
            values (?,?,?)
            on duplicate key update
            external_id = values(external_id),
            name = values(name),
            address = values(address)
            ","sss",
            $warehouse_id,
            $name,
            $address));//TODO
    }

    public function deleteWarehouse(string $warehouse_id):DeleteResult {
        try{
            $this->getMysqli()->begin_transaction(MYSQLI_TRANS_START_READ_WRITE);

            $res=$this->executePrepared("select 0 from storages_view where warehouse=? and deleted is null","s",$warehouse_id)->fetch_assoc();

            if($res!==null){
                $this->getMysqli()->commit();
                return DeleteResult::Failed;
            }

            $m=0;
            $this->executePrepared2("update warehouses set deleted=UNIX_TIMESTAMP() where external_id=? and deleted is null",$m,"s",$warehouse_id);
            $this->getMysqli()->commit();
            return $m===0?DeleteResult::Missing:DeleteResult::Deleted;
        }catch(\mysqli_sql_exception $e) {
            $this->rollback($e);
        }
    }

    public function moveWarehouse(string $warehouse_id,string $new_id):MoveResult {
        return $this->executeMoveOrExit("update warehouses set external_id=? where external_id=? and deleted is null", "ss",$new_id,$warehouse_id);
    }
}
