<?php

namespace EI\API;
use EI\Database\DB;
use EI\Database\UpsertResult;
use EI\Logic\Authentication;
use EI\Logic\Authorization;
use EI\Response\Response;
use LDAP\Result;

class SearchAPI {

    public static function handleSearch(): never {
        if(!Authorization::getCurrentAuthorization()->canViewWarehouses())
            Response::forbidden();
        
        $items=DB::getGlobal()->getStorageItems(null,
            null,
            $_GET["q"]??"",
            ($_GET["warehouse"]??"")==="true",
            ($_GET["storage"]??"")==="true",
            ($_GET["lot"]??"")==="true",
            ($_GET["serial"]??"")==="true",
            intval($_GET["offset"]??""),
            intval($_GET["len"]??APIUtils::default_page_size)
        );
        Response::json($items,200);
    }

}