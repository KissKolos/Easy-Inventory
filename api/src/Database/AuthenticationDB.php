<?php

namespace EI\Database;

use EI\Settings\Settings;

trait AuthenticationDB {
    use BaseTrait;

    public function getPasswordHash(string $user_id):string|null {
        $user=$this->fetchPreparedOrExit("SELECT passhash FROM users_view WHERE id=?","s",$user_id);
        return $user===null?null:$user["passhash"];
    }

    public function updatePassword(string $user_id,string $new_password_hash):bool {
        return $this->executePreparedOrExit("update users set passhash=? where external_id=?","ss",$new_password_hash,$user_id)>0;
    }

    public function createToken(string $user_id,string $token,int $expiration):bool {
        return $this->executePreparedOrExit("insert into authentication_token (token,user,expiration)
            select ?,id,(UNIX_TIMESTAMP()+?) from users where external_id=?","sis",$token,$expiration,$user_id)>0;
    }

    public function setTokenExpiration(string $token,int $expiration):bool {
        return $this->executePreparedOrExit("update authentication_token_view set expiration=UNIX_TIMESTAMP()+? where token=?","is",$expiration,$token)>0;
    }

    public function deleteToken(string $token):bool {
        return $this->executePreparedOrExit("delete from authentication_token where token=?","s",$token)>0;
    }

    public function getUserIdFromToken(string $token):string|null {
        $res=$this->fetchPreparedOrExit("SELECT user FROM authentication_token_view WHERE token=?","s",$token);
        if($res!==null)
            $res=$res["user"];
        return $res;
    }
}