<?php

namespace EI\API;
use EI\Database\DB;
use EI\Database\UpsertResult;
use EI\Logic\Authentication;
use EI\Logic\Authorization;
use EI\Response\Response;

class UsersAPI {

    /*private static function canModifyUser(string $target_user_id):bool {
        $user_id=Authentication::getCurrentUserId();

        if($user_id===null)
            return false;

        $auth=Authorization::getAuthorization($user_id);

        return $auth->canViewUsers()||
            $user_id===$target_user_id||
            DB::getGlobal()->isUserManagedBy($target_user_id,$user_id);
    }*/

    public static function handleGetAll():never {
        $user_id=Authentication::getCurrentUserId();

        if($user_id===null)
            Response::unathorized();

        $auth=Authorization::getAuthorization($user_id);

        $data=null;
        if($auth->canViewUsers()) {
            $data=DB::getGlobal()->getUsers($_GET["q"]??"",
                intval($_GET["offset"]??""),
                intval($_GET["len"]??APIUtils::default_page_size));
        }else{
            $data=DB::getGlobal()->getUsersManagedBy($user_id,
                $_GET["q"]??"",
                intval($_GET["offset"]??""),
                intval($_GET["len"]??APIUtils::default_page_size));
        }

        /*foreach($data as &$user) {
            $user=[
                "id"=>$user->id,
                "name"=> $user->name,
                "manager"=>[
                    "id"=>$user->manager->id,
                    "id"=>$user->manager->name
                ]
            ];
        }*/
        Response::json($data,200);
    }

    public static function handleGetSpecific(string $target_user_id) {
        if(Authorization::getCurrentAuthorization()->canViewUser($target_user_id)) {

            $user=DB::getGlobal()->getUser($target_user_id);
            if($user===null)
                Response::notFound();
            else{
                Response::json($user/*[
                    "name"=> $user->name,
                    "manager"=> $user->manager_id
                ]*/,200);
            }
        }else
            Response::unathorized();
    }

    public static function handleDelete(string $target_user_id):never {
        if(Authorization::getCurrentAuthorization()->canDeleteUser($target_user_id)) {
            Response::deleteResult(DB::getGlobal()->deleteUser($target_user_id));
        }else
            Response::forbidden();
    }

    public static function handlePut(string $target_user_id):never {
        APIUtils::checkId($target_user_id);
        $user_id=Authentication::getCurrentUserId();

        if($user_id===null)
            Response::unathorized();

        $auth=Authorization::getAuthorization($user_id);
        
        $update=$auth->canManageUser($target_user_id)&&($_GET["update"]??"")==="true";
        $create=$auth->canCreateUsers()&&($_GET["create"]??"")==="true";

        $data=APIUtils::getValidatedBody([
            "name"=>"string",
            "password?"=>"string",
            "manager?"=>"string"
        ]);

        if(!isset($data["manager"]))
            $data["manager"]=$user_id;
        
        if($data["manager"]!==$user_id&&!DB::getGlobal()->isUserManagedBy($data["manager"],$user_id)&&!$auth->isAdmin())
            Response::forbidden();
        
        if(!isset($data["password"])){
            if($update)
                Response::updateResult2(DB::getGlobal()->updateUserNoPassword(
                    $target_user_id, $data["name"],$data["manager"]));
            elseif($create)
                Response::badRequest();
            else
                Response::forbidden();
        }else{
            if(!Authentication::isValidPassword($data["password"]))
                Response::badRequest();
            $pass_hash=Authentication::createHash($data["password"]);
            if($create&&$update)
                Response::upsertResult(DB::getGlobal()->createOrUpdateUser($target_user_id, $data["name"],$pass_hash,$data["manager"]));
            elseif($update)
                Response::updateResult2(DB::getGlobal()->updateUser($target_user_id, $data["name"],$pass_hash,$data["manager"]));
            elseif($create)
                Response::createResult(DB::getGlobal()->createUser($target_user_id, $data["name"],$pass_hash,$data["manager"]));
            else
                Response::forbidden();
        }
/*
        if($auth->canReassign()) {
            if(!isset($data["password"])){
                Response::updateResult2(DB::getGlobal()->updateUserNoPassword(
                    $target_user_id, $data["name"],$data["manager"]));
            }else{
                $pass_hash=Authentication::createHash($data["password"]);
                Response::upsertResult(DB::getGlobal()->createOrUpdateUser(
                    $target_user_id, $data["name"],$pass_hash,$data["manager"]));
            }
        }else{
            if(!isset($data["password"]))
                Response::badRequest();
            $pass_hash=Authentication::createHash($data["password"]);
            Response::createResult(DB::getGlobal()->createUser(
                $target_user_id, $data["name"],$pass_hash,$data["manager"]));
        }
*/

    }

    public static function handleMove(): never {
        $data=APIUtils::getValidatedBody([
            "from"=>"string",
            "to"=>"string"
        ]);
        APIUtils::checkId($data["from"]);
        APIUtils::checkId($data["to"]);

        if(!Authorization::getCurrentAuthorization()->canManageUser($data["from"]))
            Response::forbidden();

        Response::moveResult(DB::getGlobal()->moveUser($data["from"],$data["to"]));
    }
}
