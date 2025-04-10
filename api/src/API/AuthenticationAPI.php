<?php

namespace EI\API;
use EI\Database\DB;
use EI\Logic\Authentication;
use EI\Logic\Authorization;
use EI\Response\Response;
use EI\Settings\Settings;

class AuthenticationAPI {

    public static function handleLogin(string $user_id) {
        $pass=APIUtils::getValidatedBody(["password"=>"string"])["password"];

        $token=Authentication::authenticate($user_id,$pass);

        if($token===null)
            Response::notFound();
        else
            Response::json(["token"=>$token],200);
    }

    public static function handleTokenRenew() {
        $token=Authentication::getToken();
        if($token==null)
            Response::unathorized();
        
        $exp=Settings::getSettings()->token_expiration;
        if(DB::getGlobal()->setTokenExpiration($token,$exp))
            Response::json(["expiration"=>time()+$exp],200);
        else
            Response::notFound();
    }

    public static function handleTokenDelete() {
        $token=Authentication::getToken();
        if($token==null)
            Response::unathorized();
        
        Response::deleteResult2(DB::getGlobal()->deleteToken($token));
    }

    public static function handleUserinfo():never {
        $userid=Authentication::getCurrentUserId();
        if($userid===null)
            Response::unathorized();
        
        $auth=Authorization::getAuthorization($userid);

        $user=DB::getGlobal()->getUser($userid);

        Response::json([
            "user"=>$userid,
            "username"=>$user->name
        ],200);
    }

    public static function handleUserinfoPut():never {
        $userid=Authentication::getCurrentUserId();
        if($userid===null)
            Response::unathorized();
        
        $data=APIUtils::getValidatedBody([
            "old_password"=>"string",
            "new_password"=>"string"
        ]);
        if(!Authentication::isValidPassword($data["new_password"]))
            Response::badRequest();

        if(!Authentication::verifyPassword($userid,$data["old_password"]))
            Response::badRequest();

        Response::passwordUpdateResult(DB::getGlobal()->updatePassword($userid,Authentication::createHash($data["new_password"])));
    }
}