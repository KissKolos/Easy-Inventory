<?php

namespace EI\Logic;
use EI\Database\DB;
use EI\Logging\Logger;
use EI\Response\Response;
use EI\Settings\Settings;


class Authentication {

    public static function getToken():string|null {
        if(!isset($_SERVER["HTTP_AUTHORIZATION"]))
            return null;
        
        $token=trim($_SERVER["HTTP_AUTHORIZATION"]);
        if(str_starts_with($token,"Bearer ")) {
            return trim(substr($token,strlen("Bearer ")));
        }else{
            return null;
        }
    }

    public static function isTestUser():bool {
        return self::getToken()===Settings::getSettings()->test_token;
    }

    public static function getCurrentUserId():string|null {
        $token=self::getToken();

        if($token!==null){
            $userid=DB::getGlobal()->getUserIdFromToken($token);
            \EI\Logging\Logger::set_userid($userid);
            return $userid;
        }else
            return null;
    }

    public static function isValidPassword(string $password):bool {
        return $password!=="";
    }

    public static function verifyPassword(string $user_id,string $password):bool {
        $hash=DB::getGlobal()->getPasswordHash($user_id);
        return $hash!==null&&password_verify($password,$hash);
    }

    /**
     * @param string $user_id
     * @param string $password
     * @return string|null the authorization token or null if provided invalid credentials
     */
    public static function authenticate(string $user_id,string $password):string|null {
        if(self::verifyPassword($user_id,$password)) {
            $settings=Settings::getSettings();

            $retry=0;
            do{
                $token=base64_encode(random_bytes($settings->token_length));
                $retry+=1;
            }while(!DB::getGlobal()->createToken($user_id,$token,$settings->token_expiration)&&$retry<5);//token conflict has a very low chance
            if($retry==5)
                Response::serverError();
            return $token;
        }else{
            return null;
        }
    }

    public static function createHash(string $password): string {
        return password_hash($password,PASSWORD_BCRYPT);//TODO add hash algo setting?
    }
}