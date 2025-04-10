<?php

namespace EI\Database;
use EI\Logic\Unit;

trait UnitDB
{
    use BaseTrait;

    /**
     * @return Unit[]
     */
    public function getUnits(string|null $q,int $offset,int $len,bool $archived):array
    {
        $res=$this->fetchAllPreparedOrExit("select id,name,deleted from units_view where ((? is null) or locate(?,name)>0 or locate(?,id)>0) and (?=1 or (deleted is null)) limit ?,?",
        "sssiii",$q,$q,$q,$archived,$offset,$len);
        foreach($res as &$row){
            $row=new Unit($row["id"],$row["name"],$row["deleted"]);
        }
        return $res;
    }

    public function getUnit(string $unit_id):Unit|null
    {
        $res=$this->fetchPreparedOrExit("select id,name,deleted from units_view WHERE id = ?;","s",$unit_id);
        if($res===null)
            return null;
        return new Unit($res["id"], $res["name"],$res["deleted"]);
    }

    public function createUnit(string $unit_id, string $unit_name):bool
    {
        return $this->executePreparedOrExit("insert ignore into units_view (id,name) values (?,?)","ss",$unit_id,$unit_name)>0;
    }

    public function createOrUpdateUnit(string $unit_id, string $unit_name):UpsertResult
    {
        return UpsertResult::fromModified($this->executePreparedOrExit("insert ignore into units_view (id,name)
            values (?,?)
            on duplicate key update
            id = values(id),
            name = values(name)
            ","ss",$unit_id, $unit_name));//TODO
    }

    public function updateUnit(string $unit_id, string $unit_name):bool
    {
        return $this->executePreparedOrExit("update units set
            name=? where external_id=? and deleted is null","ss",
            $unit_name,
            $unit_id)>0;
    }

    public function deleteUnit(string $unit_id):DeleteResult
    {
        try{
            $this->getMysqli()->begin_transaction(MYSQLI_TRANS_START_READ_WRITE);

            $res=$this->executePrepared("select 0 from item_types_view where unit=?","s",$unit_id)->fetch_assoc();

            if($res!==null){
                $this->getMysqli()->commit();
                return DeleteResult::Failed;
            }

            $m=0;
            $this->executePrepared2("update units_view set deleted=UNIX_TIMESTAMP() where id=? and deleted is null",$m,"s",$unit_id);
            $this->getMysqli()->commit();
            return $m===0?DeleteResult::Missing:DeleteResult::Deleted;
        }catch(\mysqli_sql_exception $e) {
            $this->rollback($e);
        }
    }

    public function moveUnit(string $unit_id,string $new_id):MoveResult {
        return $this->executeMoveOrExit("update units set external_id=? where external_id=? and deleted is null","ss",$new_id,$unit_id);
    }
}
