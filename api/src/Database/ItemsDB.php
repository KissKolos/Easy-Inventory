<?php

namespace EI\Database;
use EI\Logic\Item;
use EI\Logic\Unit;
use EI\Response\Response;

trait ItemsDB
{
    use BaseTrait;   

    /**
      *@return Item[]
      */
    public function getItems(string|null $q,int $offset,int $len,bool $archived):array
    {
        $res=$this->fetchAllPreparedOrExit("select id,unit,unit_name,unit_deleted,name,deleted from item_types_view where (? is null or locate(?,name)>0 or locate(?,id)>0) and (?=1 or deleted is null) order by name limit ?,?",
            "sssiii",$q,$q,$q,$archived,$offset,$len);
        foreach($res as &$row){
            $row=new Item($row["id"],new Unit($row["unit"],$row["unit_name"],$row["unit_deleted"]),$row["name"],$row["deleted"]);
        }
        return $res;
    }

    public function deleteItem(string $item_id):DeleteResult
    {
        try{
            $this->getMysqli()->begin_transaction(MYSQLI_TRANS_START_READ_WRITE);

            $res=$this->executePrepared("
            select 0 from item_stacks
                where item_type=(select id from item_types where external_id=?)
            union
            select 0 from operation_items
                inner join operations on operations.id=operation_items.operation
                where item_type=(select id from item_types where external_id=?) and commited is null","ss",$item_id,$item_id)->fetch_assoc();

            if($res!==null){
                $this->getMysqli()->commit();
                return DeleteResult::Failed;
            }

            $m=0;
            $this->executePrepared2("update item_types set deleted=UNIX_TIMESTAMP() where external_id=?",$m,"s",$item_id);
            $this->getMysqli()->commit();
            return $m===0?DeleteResult::Missing:DeleteResult::Deleted;
        }catch(\mysqli_sql_exception $e) {
            $this->rollback($e);
        }
    }

    public function getItem(string $item_id):Item|null
    {
        $row=$this->fetchPreparedOrExit("select unit,unit_name,name,unit_deleted,deleted from item_types_view where id=?","s",$item_id);
        if($row!=null)
            $row=new Item($item_id,new Unit($row["unit"],$row["unit_name"],$row["unit_deleted"]),$row["name"],$row["deleted"]);
        return $row;
    }

    public function createItem(string $item_id, string $item_unit, string $item_name):bool
    {
        return $this->executePreparedOrExit("insert ignore into item_types (external_id,unit,name)
            values (?,(select id from units where external_id=?),?)","sss",$item_id, $item_unit, $item_name)>0;
    }

    public function createOrUpdateItem(string $item_id, string $item_unit, string $item_name):UpsertResult
    {
        return UpsertResult::fromModified($this->executePreparedOrExit("insert ignore into item_types (external_id,unit,name)
            values (?,(select id from units where external_id=?),?)
            on duplicate key update
            external_id = values(external_id),
            unit = values(unit),
            name = values(name)","sss",$item_id, $item_unit, $item_name));//TODO
    }

    public function updateItem(string $item_id, string $item_unit, string $item_name):bool
    {
        return $this->executePreparedOrExit("update ignore item_types set unit=(select id from units where external_id=?),name=? where external_id=? and deleted is null","sss",
            $item_unit, $item_name,$item_id)>0;
    }

    public function moveItem(string $item_id,string $new_id):MoveResult {
        return $this->executeMoveOrExit("update item_types set external_id=? where external_id=? and deleted is null","ss",$new_id,$item_id);
    }
}
