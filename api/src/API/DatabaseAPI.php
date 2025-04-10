<?php

namespace EI\API;
use EI\Database\DB;
use EI\Response\Response;
use EI\Logic\Authentication;
use EI\Logic\Authorization;

class DatabaseAPI {

    public static function handleGet(): never {
        if(!Authorization::getCurrentAuthorization()->isAdmin())
            Response::forbidden();

        $data=DB::getGlobal()->export();
        Response::json($data,200);
    }

    public static function handlePut(): never {
        if(!(Authentication::isTestUser()||Authorization::getCurrentAuthorization()->isAdmin()))
            Response::forbidden();
        
        $data = json_decode(file_get_contents('php://input'), true);
        DB::getGlobal()->import($data);
        Response::noContent();
    }

}