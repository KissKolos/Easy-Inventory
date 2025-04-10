<?php

namespace EI\Database;
use EI\Logging\Logger;
use EI\Response\Response;

trait ExportDB
{
    use BaseTrait;

    public function export(): array {
        try{
            $this->getMysqli()->begin_transaction(MYSQLI_TRANS_START_READ_WRITE);

            $units=[];
            $res=$this->executeSql("select * from units_view")->fetch_all(MYSQLI_ASSOC);
            foreach($res as $v) {
                array_push($units,[
                    "id"=>$v["id"],
                    "name"=>$v["name"],
                    "deleted"=>$v["deleted"]
                ]);
            }

            $items=[];
            $res=$this->executeSql("select * from item_types_view")->fetch_all(MYSQLI_ASSOC);
            foreach($res as $v) {
                array_push($items,[
                    "id"=>$v["id"],
                    "name"=>$v["name"],
                    "unit_id"=>$v["unit"],
                    "deleted"=>$v["deleted"]
                ]);
            }

            $warehouses=[];
            $res=$this->executeSql("select * from warehouses_view")->fetch_all(MYSQLI_ASSOC);
            foreach($res as $wh) {
                $storages=[];
                $res2=$this->executePrepared("select * from storages_view where warehouse=?","s",$wh["id"])->fetch_all(MYSQLI_ASSOC);
                foreach($res2 as $st) {
                    $limits=[];
                    $res3=$this->executePrepared("select * from storage_limits_view where warehouse=? and storage=?","ss",$wh["id"],$st["id"])->fetch_all(MYSQLI_ASSOC);
                    foreach($res3 as $l) {
                        array_push($limits,[
                            "item_id"=>$l["item_type"],
                            "amount"=>$l["amount"]
                        ]);
                    }

                    $item_stacks=[];
                    $res3=$this->executePrepared("select * from item_stacks_view where warehouse=? and storage=?","ss",$wh["id"],$st["id"])->fetch_all(MYSQLI_ASSOC);
                    foreach($res3 as $l) {
                        array_push($item_stacks,[
                            "item_id"=>$l["item"],
                            "amount"=>$l["amount"],
                            "lot"=>$l["lot"],
                            "global_serial"=>$l["global_serial"]??0,
                            "manufacturer_serial"=>$l["manufacturer_serial"]
                        ]);
                    }

                    array_push($storages,[
                        "id"=>$st["id"],
                        "name"=>$st["name"],
                        "limits"=>$limits,
                        "item_stacks"=>$item_stacks,
                        "deleted"=>$st["deleted"]
                    ]);
                }

                $operations=[];
                $res2=$this->executePrepared("select * from operations_view where warehouse=?","s",$wh["id"])->fetch_all(MYSQLI_ASSOC);
                foreach($res2 as $op) {
                    $op_items=[];
                    $res3=$this->executePrepared("select * from operation_items_view where warehouse=? and operation=?","ss",$wh["id"],$op["id"])->fetch_all(MYSQLI_ASSOC);
                    foreach($res3 as $l) {
                        array_push($op_items,[
                            "item_id"=>$l["item"],
                            "amount"=>$l["amount"],
                            "lot"=>$l["lot"],
                            "global_serial"=>$l["global_serial"]??0,
                            "manufacturer_serial"=>$l["manufacturer_serial"]
                        ]);
                    }

                    array_push($operations,[
                        "id"=>$op["id"],
                        "name"=>$op["name"],
                        "is_add"=>$op["type"]==="add",
                        "items"=>$op_items,
                        "created"=>$op["created"],
                        "commited"=>$op["commited"]
                    ]);
                }

                array_push($warehouses,[
                    "id"=>$wh["id"],
                    "name"=>$wh["name"],
                    "address"=>$wh["address"],
                    "storages"=>$storages,
                    "operations"=>$operations,
                    "deleted"=>$wh["deleted"]
                ]);
            }

            $users=[];
            $res=$this->executeSql("select * from users_view")->fetch_all(MYSQLI_ASSOC);
            foreach($res as $u) {
                $system_authorizations=[];
                $local_authorizations=[];
                $res2=$this->executePrepared("select warehouse,authorization from authorization_view where id=?","s",$u["id"])->fetch_all(MYSQLI_ASSOC);
                foreach($res2 as $a) {
                    if($a["warehouse"]===null)
                        array_push($system_authorizations,$a["authorization"]);
                    else
                        array_push($local_authorizations,[
                            "warehouse_id"=>$a["warehouse"],
                            "authorization"=>$a["authorization"]
                        ]);
                }

                array_push($users,[
                    "id"=>$u["id"],
                    "name"=>$u["name"],
                    "manager_id"=>$u["manager"],
                    "pass_hash"=>$u["passhash"],
                    "system_authorizations"=>$system_authorizations,
                    "local_authorizations"=>$local_authorizations,
                ]);
            }

            $this->getMysqli()->commit();
            return [
                "units"=>$units,
                "items"=>$items,
                "warehouses"=>$warehouses,
                "users"=>$users
            ];
        }catch(\mysqli_sql_exception $e) {
            try{
                $this->getMysqli()->rollback();
            }catch(\mysqli_sql_exception $e2) {
                Logger::log($e2->getMessage());
            }
            Logger::log($e->getMessage()."\n".$e->getTraceAsString());
            Response::serverError();
        }
    }
}