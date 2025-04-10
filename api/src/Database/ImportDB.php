<?php

namespace EI\Database;
use EI\Logging\Logger;
use EI\Response\Response;

trait ImportDB
{
    use BaseTrait;   

    private function purgeDB(): void {
        $this->executeSql("delete from storage_limits;");
        $this->executeSql("delete from authentication_token;");
        $this->executeSql("delete from global_authorization;");
        $this->executeSql("delete from authorization;");
        $this->executeSql("update users set manager=null;");
        $this->executeSql("delete from users;");
        $this->executeSql("delete from operation_items;");
        $this->executeSql("delete from operations;");
        $this->executeSql("delete from item_stacks;");
        $this->executeSql("delete from storages;");
        $this->executeSql("delete from warehouses;");
        $this->executeSql("delete from item_types;");
        $this->executeSql("delete from units;");
    }

    public function import(array $data): void {
        try{
            $this->getMysqli()->begin_transaction(MYSQLI_TRANS_START_READ_WRITE);
            
            $this->purgeDB();

            foreach($data["units"] as $v)
                $this->executePrepared("insert into units (external_id,name,deleted) values (?,?,?)","ssi",$v["id"],$v["name"],$v["deleted"]);

            foreach($data["items"] as $v){
                $this->executePrepared("insert into item_types (external_id,name,unit,deleted) values (?,?,(select id from units where external_id=?),?)",
                    "sssi",$v["id"],$v["name"],$v["unit_id"],$v["deleted"]);
            }

            foreach($data["warehouses"] as $wh) {
                $this->executePrepared("insert into warehouses (external_id,name,address,deleted) values (?,?,?,?)","sssi",
                    $wh["id"],$wh["name"],$wh["address"],$wh["deleted"]);

                foreach($wh["storages"] as $st) {
                    $this->executePrepared("insert into storages (warehouse,external_id,name,deleted) values ((select id from warehouses where external_id=?),?,?,?)","sssi",
                        $wh["id"],$st["id"],$st["name"],$st["deleted"]);
                    
                    foreach($st["limits"] as $l) {
                        $this->executePrepared("insert into storage_limits (storage,item_type,amount) values (
                            (select storages.id from warehouses inner join storages on storages.warehouse=warehouses.id where warehouses.external_id=? and storages.external_id=?),
                            (select id from item_types where external_id=?),
                            ?)",
                            "sssi",$wh["id"],$st["id"],$l["item_id"],$l["amount"]);
                    }

                    foreach($st["item_stacks"] as $l) {
                        $this->executePrepared("insert into item_stacks (storage,item_type,amount,lot,global_serial_inner,manufacturer_serial) values (
                            (select storages.id from warehouses inner join storages on storages.warehouse=warehouses.id where warehouses.external_id=? and storages.external_id=?),
                            (select id from item_types where external_id=?),
                            ?,?,?,?)",
                            "sssisis",$wh["id"],$st["id"],$l["item_id"],$l["amount"],$l["lot"],$l["global_serial"],$l["manufacturer_serial"]);
                    }
                }

                foreach($wh["operations"] as $op) {
                    $this->executePrepared("insert into operations (warehouse,external_id,name,type,created,commited) values (
                        (select id from warehouses where external_id=?),
                        ?,?,?,?,?)","ssssii",
                        $wh["id"],$op["id"],$op["name"],($op["is_add"]?"add":"remove"),$op["created"],$op["commited"]);
                    
                    foreach($op["items"] as $it) {
                        $this->executePrepared("insert into operation_items (operation,storage,item_type,amount,lot,global_serial_inner,manufacturer_serial) values (
                            (select operations.id from warehouses inner join operations on operations.warehouse=warehouses.id where warehouses.external_id=? and operations.external_id=?),
                            (select storages.id from warehouses inner join storages on storages.warehouse=warehouses.id where warehouses.external_id=? and storages.external_id=?),
                            (select id from item_types where external_id=?),
                            ?,?,?,?)","sssssisis",
                            $wh["id"],$op["id"],$wh["id"],$it["storage_id"],$it["item_id"],$it["amount"],$it["lot"],$it["global_serial"],$it["manufacturer_serial"]);
                    }
                }
            }

            foreach($data["users"] as $u) {
                if(($u["pass_hash"]??null)===null)
                    $u["pass_hash"]=\EI\Logic\Authentication::createHash($u["password"]);

                $this->executePrepared("insert into users (external_id,name,manager,passhash) values (?,?,null,?)","sss",
                    $u["id"],$u["name"],$u["pass_hash"]);

                foreach($u["system_authorizations"] as $a) {
                    $this->executePrepared("insert into global_authorization (user,authorization) values ((select id from users where external_id=?),?)","ss",$u["id"],$a);
                }
                foreach($u["local_authorizations"] as $la) {
                    foreach($la["authorizations"] as $a) {
                        $this->executePrepared("insert into authorization (user,warehouse,authorization) values (
                            (select id from warehouses where external_id=?),
                            (select id from users where external_id=?),
                            ?)","sss",$u["id"],$la["warehouse_id"],$a);
                    }
                }
            }

            foreach($data["users"] as $v) {
                $this->executePrepared("update users set manager=(select id from users WHERE external_id=?) where users.external_id=?","ss",
                    $v["manager_id"],$v["id"]);
            }

            $this->getMysqli()->commit();
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
