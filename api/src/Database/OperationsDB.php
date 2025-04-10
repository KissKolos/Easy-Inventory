<?php

namespace EI\Database;
use EI\Logic\ItemStack;
use EI\Logic\Operation;
use EI\Logic\OperationItem;
use EI\Logic\Item;
use EI\Logic\Unit;
use EI\Logic\Storage;
use EI\Logic\Warehouse;

trait OperationsDB {
    use BaseTrait;

    /**
     * @param string $warehouse_id
     * @return \EI\Logic\Operation[]
     */
    public function getOperations(string $warehouse_id,string|null $q,int $offset,int $len,bool $archived):array {
        $res=$this->fetchAllPreparedOrExit("select * from operations_view where warehouse=? and
        (? is null or locate(?,name)>0 or locate(?,id)>0) and (?=1 or commited is null) order by name limit ?,?","ssssiii",$warehouse_id,$q,$q,$q,$archived,$offset,$len);
        foreach($res as &$row) {
            $items=$this->fetchAllPreparedOrExit("select *
                from operation_items_view where warehouse=? and operation=?","ss",$warehouse_id,$row["id"]);
            foreach($items as &$irow) {
                $irow=new OperationItem(
                    new Item(
                        $irow["item"],
                        new Unit(
                            $irow["unit"],
                            $irow["unit_name"],
                            $irow["unit_deleted"]
                        ),
                        $irow["item_name"],
                        $irow["item_deleted"]
                    ),
                    $irow["amount"],
                    $irow["storage"]===null?null:new Storage(
                        new Warehouse(
                            $irow["warehouse"],
                            $irow["warehouse_name"],
                            $irow["warehouse_address"],
                            $irow["warehouse_deleted"]
                        ),
                        $irow["storage"],
                        $irow["storage_name"],
                        $irow["storage_deleted"]
                    ),
                    $irow["global_serial"],
                    $irow["manufacturer_serial"],
                    $irow["lot"]
                );
            }
            $row=new Operation(
                $row["id"],
                $row["name"],
                $row["type"]==="add",
		        new Warehouse(
                    $row["warehouse"],
                    $row["warehouse_name"],
                    $row["warehouse_address"],
                    $row["warehouse_deleted"]
                ),
		        $row["created"],
		        $row["commited"],
                $items
            );
        }
        return $res;
    }

    public function getOperation(string $warehouse_id,string $operation_id):Operation|null {
        $row=$this->fetchPreparedOrExit("select * from operations_view where warehouse=? and id=?","ss",$warehouse_id,$operation_id);
        if($row===null)
            return null;

        $items=$this->fetchAllPreparedOrExit("select * from operation_items_view where warehouse=? and operation=?","ss",$warehouse_id,$operation_id);
        foreach($items as &$irow) {
            $irow=new OperationItem(
                new Item(
                    $irow["item"],
                    new Unit(
                        $irow["unit"],
                        $irow["unit_name"],
                        $irow["unit_deleted"]
                    ),
                    $irow["item_name"],
                    $irow["item_deleted"]
                ),
                $irow["amount"],
                $irow["storage"]===null?null:new Storage(
                    new Warehouse(
                        $irow["warehouse"],
                        $irow["warehouse_name"],
                        $irow["warehouse_address"],
                        $irow["warehouse_deleted"]
                    ),
                    $irow["storage"],
                    $irow["storage_name"],
                    $irow["storage_deleted"]
                ),
                $irow["global_serial"],
                $irow["manufacturer_serial"],
                $irow["lot"]
            );
        }

        return new Operation(
            $row["id"],
            $row["name"],
            $row["type"]==="add",
            new Warehouse(
                $row["warehouse"],
                $row["warehouse_name"],
                $row["warehouse_address"],
                $row["warehouse_deleted"]
            ),
	        $row["created"],
	        $row["commited"],
            $items
        );
    }

    private function createOperation(string $warehouse_id,string $operation_id,string $type,string $name):int|null {
        $modified=0;
        $this->executePrepared2("insert ignore into operations (warehouse,external_id,name,type,created) values
            ((select id from warehouses where external_id=?),?,?,?,UNIX_TIMESTAMP())",$modified,"ssss",
            $warehouse_id,$operation_id,$name,$type);
        
        if($modified==0){
            return null;
        }

        return $this->getLastId();
    }

    /**
     * @param int $internal_operation_id
     * @param string $warehouse_id
     * @param \EI\Logic\OperationItem[] $data
     * @return void
     */
    private function addOperationItems(int $internal_operation_id,string $warehouse_id,array $data): void {
        foreach($data as $_=>$item) {
            $this->executePrepared("insert into operation_items
                (operation,storage,item_type,amount,lot,global_serial_inner,manufacturer_serial)
                values (?,
                (select storages.id from warehouses
                    inner join storages on warehouses.id=storages.warehouse
                    where warehouses.external_id=? and storages.external_id=?
                ),
                (select id from item_types where external_id=?),
                ?,?,?,?) on duplicate key update operation_items.amount=operation_items.amount+?",
                "isssisisi",
                $internal_operation_id,$warehouse_id,$item->storage===null?null:$item->storage->id,
                $item->item->id,$item->amount,($item->lot===null?"":$item->lot),$item->global_serial??0,$item->manufacturer_serial,$item->amount);
        }
    }

    /**
     * @param string $warehouse_id
     * @param \EI\Logic\OperationItem $item
     * @return \EI\Logic\OperationItem[]|null
     */
    private function planItemAdd(string $warehouse_id,OperationItem $item):array|null {
        $planned=[];
        $storage=$item->storage===null?null:$item->storage->id;
        $matched=$this->executePrepared("select
                slv.storage `storage`,
                (slv.amount-COALESCE(total_amount,0)) amount
            from storage_limits_view slv
                left join (select warehouse,storage,item,sum(amount) total_amount from item_stacks_view group by warehouse,storage,item) st on (
                    st.warehouse=slv.warehouse and
                    st.storage=slv.storage and
                    st.item=slv.item_type
                )
            where
                (slv.storage=? or (? is null)) and
                slv.warehouse=? and slv.item_type=?","ssss",
            $storage,$storage,
            $warehouse_id,$item->item->id)->fetch_all(MYSQLI_ASSOC);

        $need=$item->amount;
        foreach($matched as $m) {
            $a=min($need,$m["amount"]);
            if($a>0){
                $planned[]=new OperationItem(
                    new Item($item->item->id,new Unit("","",null),"",null),
                    $a,
                    new Storage(new Warehouse("","","",null),$m["storage"],"",null),
                    $item->global_serial,
                    $item->manufacturer_serial,
                    $item->lot);
                $need-=$a;
            }
        }

        if($need===0)
            return $planned;
        else
            return null;
    }

    /**
     * @param string $operation_id
     * @param string $name
     * @param string $warehouse_id
     * @param \EI\Logic\OperationItem[] $items
     * @return bool
     */
    public function createAddOperation2(string $operation_id,string $name,string $warehouse_id,array $items):bool {
        $id=$this->createOperation($warehouse_id,$operation_id,"add",$name);
        if($id===null)
            return false;

        foreach($items as $item) {
            $planned=$this->planItemAdd($warehouse_id,$item);
            if($planned===null)
                return false;
            $this->addOperationItems($id,$warehouse_id,$planned);
        }

        return true;
    }

    /**
     * @param string $operation_id
     * @param string $name
     * @param string $warehouse_id
     * @param \EI\Logic\OperationItem[] $items
     * @return bool
     */
    public function createAddOperation(string $operation_id,string $name,string $warehouse_id,array $items):bool {
        try{
            $this->getMysqli()->begin_transaction(MYSQLI_TRANS_START_READ_WRITE);

            $res=$this->createAddOperation2($operation_id,$name,$warehouse_id,$items);
            
            if($res)
                $this->getMysqli()->commit();
            else
                $this->getMysqli()->rollback();
            return $res;
        }catch(\mysqli_sql_exception $e) {
            $this->rollback($e);
        }
    }


    /**
     * @param string $warehouse_id
     * @param \EI\Logic\OperationItem $item
     * @return \EI\Logic\OperationItem[]|null
     */
    private function planItemRemoval(string $warehouse_id,OperationItem $item):array|null {
        $planned=[];
        $serial=$item->global_serial===0?$item->global_serial:null;
        $storage=$item->storage===null?null:$item->storage->id;
        $matched=$this->executePrepared("select
                storage,
                global_serial,
                manufacturer_serial,
                lot,
                available_amount
            from item_stacks_view where
                (global_serial=? or (? is null)) and
                (manufacturer_serial=? or (? is null)) and
                (lot=? or (? is null)) and
                (storage=? or (? is null)) and
                warehouse=? and item=?","iissssssss",
            $serial,$serial,
            $item->manufacturer_serial,$item->manufacturer_serial,
            $item->lot,$item->lot,
            $storage,$storage,
            $warehouse_id,$item->item->id)->fetch_all(MYSQLI_ASSOC);

        $need=$item->amount;
        foreach($matched as $m) {
            $a=min($need,$m["available_amount"]);
            if($a>0){
                $planned[]=new OperationItem(
                    new Item($item->item->id,new Unit("","",null),"",null),
                    $a,
                    new Storage(new Warehouse("","","",null),$m["storage"],"",null),
                    $m["global_serial"],
                    $m["manufacturer_serial"],
                    $m["lot"]);
                $need-=$a;
            }
        }

        \EI\Logging\Logger::log($need);
        if($need===0)
            return $planned;
        else
            return null;
    }

    /**
     * @param string $operation_id
     * @param string $name
     * @param string $warehouse_id
     * @param \EI\Logic\OperationItem[] $items
     * @return bool
     */
    private function createRemoveOperation2(string $operation_id,string $name,string $warehouse_id,array $items):bool {
        $id=$this->createOperation($warehouse_id,$operation_id,"remove",$name);
        if($id===null)
            return false;

        foreach($items as $item) {
            $planned=$this->planItemRemoval($warehouse_id,$item);
            if($planned===null)
                return false;
            $this->addOperationItems($id,$warehouse_id,$planned);
        }

        return true;
    }

    /**
     * @param string $operation_id
     * @param string $name
     * @param string $warehouse_id
     * @param \EI\Logic\OperationItem[] $items
     * @return bool
     */
    public function createRemoveOperation(string $operation_id,string $name,string $warehouse_id,array $items):bool {
        try{
            $this->getMysqli()->begin_transaction(MYSQLI_TRANS_START_READ_WRITE);

            $res=$this->createRemoveOperation2($operation_id,$name,$warehouse_id,$items);

            if($res)
                $this->getMysqli()->commit();
            else
                $this->getMysqli()->rollback();
            return $res;
        }catch(\mysqli_sql_exception $e) {
            $this->rollback($e);
        }
    }

    public function deleteOperation(string $warehouse_id,string $operation_id):DeleteResult {
        if($this->executePreparedOrExit("delete from operations where
            warehouse=(select id from warehouses where external_id=?) and
            external_id=?","ss",$warehouse_id,$operation_id)>0)
            return DeleteResult::Deleted;
        else
            return DeleteResult::Missing;
    }

    private function commitAddOperation(string $warehouse_id,string $operation_id): void {
        $this->executePrepared("insert into item_stacks (storage,item_type,amount,lot,global_serial_inner,manufacturer_serial) select
            storage,item_type,amount,lot,global_serial_inner,manufacturer_serial from
            operation_items inner join operations on operation_items.operation=operations.id
            where warehouse=(select id from warehouses where external_id=?) and external_id=?
            on duplicate key update
            item_stacks.amount=item_stacks.amount+operation_items.amount","ss",$warehouse_id,$operation_id);
    }

    private function commitRemoveOperation(string $warehouse_id,string $operation_id): void {
        $this->executePrepared("update
                operations inner join operation_items b on operations.id=b.operation
                inner join item_stacks a on (
                    a.storage=b.storage and
                    a.item_type=b.item_type and
                    a.global_serial_inner=b.global_serial_inner and
                    a.lot=b.lot
                )
            set
                a.amount=a.amount-b.amount
            where
                operations.warehouse=(select id from warehouses where external_id=?) and
		operations.external_id=?
	    ","ss",$warehouse_id,$operation_id);
        $this->executeSql("delete from item_stacks where amount=0");
    }

    public function commitOperation(string $warehouse_id,string $operation_id):DeleteResult {
        try{
            $this->getMysqli()->begin_transaction(MYSQLI_TRANS_START_READ_WRITE);

            $res=$this->executePrepared("select type,(commited is not null) commited from operations_view where warehouse=? and id=?","ss",$warehouse_id,$operation_id)->fetch_assoc();
            if($res===null){
                $this->getMysqli()->commit();
                return DeleteResult::Missing;
            }
            if($res["commited"]===1) { 
                    $this->getMysqli()->commit();
                    return DeleteResult::Failed;
            }

            if($res["type"]==="add") {
                $this->commitAddOperation($warehouse_id,$operation_id);
            }else{
                $this->commitRemoveOperation($warehouse_id,$operation_id);
            }

            $this->executePrepared("update operations set commited=UNIX_TIMESTAMP() where
                warehouse=(select id from warehouses where external_id=?) and
                external_id=?","ss",$warehouse_id,$operation_id);

            $this->getMysqli()->commit();
            return DeleteResult::Deleted;
        }catch(\mysqli_sql_exception $e) {
            $this->rollback($e);
        }
    }

    public function moveOperation(string $warehouse_id,string $operation_id,string $new_id):bool {
        return $this->executePreparedOrExit("update operations inner join warehouses on operations.warehouse=warehouses.id
            set operations.external_id=? where warehouses.external_id=? and operations.external_id=? and commited is null",
            "sss",$new_id,$warehouse_id,$operation_id)>0;
    }

}
