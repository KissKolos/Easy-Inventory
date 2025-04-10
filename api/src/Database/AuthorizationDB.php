<?php

namespace EI\Database;
use EI\Logic\Authorization;
use EI\Logic\LocalAuthorization;

trait AuthorizationDB {
    use BaseTrait;

    public function getAuthorization(string $user_id):Authorization {
        $user_res=$this->fetchPreparedOrExit("select manager from users_view where id=?","s",$user_id);
        if($user_res===null)
            //user doesn't exists
            return Authorization::createDefault();
        
        $res=$this->fetchAllPreparedOrExit("select authorization,warehouse from authorization_view where id=?","s",$user_id);

        $global_authorizations=[
            "view_warehouses"=> false,
            "delete_warehouses"=> false,
            "create_warehouses"=> false,
            "modify_warehouses"=> false,
            "delete_types"=> false,
            "create_types"=> false,
            "modify_types"=> false,
            "view_users"=> false,
            "delete_users"=> false,
            "create_users"=> false,
            "modify_users"=> false
        ];
        $local_authorizations=[];

        foreach($res as $row) {
            $wh=$row["warehouse"];

            if($wh===null) {
                $global_authorizations[$row["authorization"]]=true;
            }else{
                if(!isset($local_authorizations[$wh])) {
                    $local_authorizations[$wh]=[
                        "view"=> false,
                        "create_remove_operation"=> false,
                        "create_add_operation"=> false,
                        "handle_operation"=> false,
                        "configure"=> false
                    ];
                }
                $local_authorizations[$wh][$row["authorization"]]=true;
            }
        }
        
        foreach($local_authorizations as $_i=>&$la) {
            $la=new LocalAuthorization($la["view"],$la["create_add_operation"],$la["create_remove_operation"],
                $la["handle_operation"],$la["configure"]);
        }
        
        return new Authorization($user_res["manager"]===null,
            $global_authorizations["view_warehouses"],
            $global_authorizations["delete_warehouses"],
            $global_authorizations["create_warehouses"],
            $global_authorizations["modify_warehouses"],
            $global_authorizations["delete_types"],
            $global_authorizations["create_types"],
            $global_authorizations["modify_types"],
            $global_authorizations["view_users"],
            $global_authorizations["delete_users"],
            $global_authorizations["create_users"],
            $global_authorizations["modify_users"],
            $local_authorizations
        );
    }

    public function createLocalAuthorization(string $user_id,string $warehouse_id,string $authorization):DeleteResult {
        if($this->executePreparedOrExit("insert ignore into authorization (user,warehouse,authorization) values
            ((select id from users where external_id=?),
            (select id from warehouses where external_id=?),
            ?)","sss",$user_id,$warehouse_id,$authorization)>0)
            return DeleteResult::Deleted;
        else
            return DeleteResult::Missing;
    }

    public function deleteLocalAuthorization(string $user_id,string $warehouse_id,string $authorization):DeleteResult {
        if($this->executePreparedOrExit("delete from authorization where
            user=(select id from users where external_id=?) and
            warehouse=(select id from warehouses where external_id=?) and
            authorization=?","sss",$user_id,$warehouse_id,$authorization)>0)
            return DeleteResult::Deleted;
        else
            return DeleteResult::Missing;
    }

    public function createSystemAuthorization(string $user_id,string $authorization):DeleteResult {
        if($this->executePreparedOrExit("insert ignore into global_authorization (user,authorization) values
            ((select id from users where external_id=?),
            ?)","ss",$user_id,$authorization)>0)
            return DeleteResult::Deleted;
        else
            return DeleteResult::Missing;
    }

    public function deleteSystemAuthorization(string $user_id,string $authorization):DeleteResult {
        if($this->executePreparedOrExit("delete from global_authorization where
            user=(select id from users where external_id=?) and
            authorization=?","ss",$user_id,$authorization)>0)
            return DeleteResult::Deleted;
        else
            return DeleteResult::Missing;
    }
}