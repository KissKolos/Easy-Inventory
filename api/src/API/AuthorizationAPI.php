<?php

namespace EI\API;
use EI\Database\DB;
use EI\Database\UpsertResult;
use EI\Logic\Authentication;
use EI\Logic\Authorization;
use EI\Response\Response;

class AuthorizationAPI {

    public static function handleGetSystem(string $user_id):never {
        if(Authorization::getCurrentAuthorization()->canViewUser($user_id)){
            $auth=DB::getGlobal()->getAuthorization($user_id);
            Response::json($auth->getSystemAuthorizations(),200);
        }
        else
            Response::forbidden();
    }

    public static function handleGetLocal(string $user_id,string $warehouse_id):never {
        if(Authorization::getCurrentAuthorization()->canViewUser($user_id)){
            $auth=DB::getGlobal()->getAuthorization($user_id);
            Response::json($auth->getLocalAuthorizations($warehouse_id),200);
        }
        else
            Response::forbidden();
    }

    public static function handleLocalDelete(string $user_id,string $warehouse_id,string $authorization):never {
        if(Authorization::getCurrentAuthorization()->canModifyLocalAuthorization(
                $user_id,$warehouse_id,$authorization))
            Response::deleteResult(DB::getGlobal()->deleteLocalAuthorization($user_id,$warehouse_id,$authorization));
        else
            Response::unathorized();
    }

    public static function handleLocalPut(string $user_id,string $warehouse_id,string $authorization):never {
        if(Authorization::getCurrentAuthorization()->canModifyLocalAuthorization(
            $user_id,$warehouse_id,$authorization))
            Response::deleteResult(DB::getGlobal()->createLocalAuthorization($user_id,$warehouse_id,$authorization));
        else
            Response::unathorized();
    }

    public static function handleSystemDelete(string $user_id,string $authorization):never {
        if(Authorization::getCurrentAuthorization()->canModifySystemAuthorization(
                $user_id,$authorization))
            Response::deleteResult(DB::getGlobal()->deleteSystemAuthorization($user_id,$authorization));
        else
            Response::unathorized();
    }

    public static function handleSystemPut(string $user_id,string $authorization):never {
        if(Authorization::getCurrentAuthorization()->canModifySystemAuthorization(
            $user_id,$authorization))
            Response::deleteResult(DB::getGlobal()->createSystemAuthorization($user_id,$authorization));
        else
            Response::unathorized();
    }
}