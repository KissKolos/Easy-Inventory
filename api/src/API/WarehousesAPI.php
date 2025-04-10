<?php

namespace EI\API;
use EI\Database\DB;
use EI\Database\UpsertResult;
use EI\Logic\Authentication;
use EI\Logic\Authorization;
use EI\Response\Response;
use LDAP\Result;

class WarehousesAPI {

    public static function handleGetAll(): never {
        $auth=Authorization::getCurrentAuthorization();

        $data=null;
        if($auth->canViewWarehouses()) {
            $data=DB::getGlobal()->getWarehouses(
                $_GET["q"]??"",
                intval($_GET["offset"]??""),
                intval($_GET["len"]??APIUtils::default_page_size),
                ($_GET["archived"]??"")==="true");
        }else{
            $data=[];
            foreach($auth->getViewableWarehouses() as $id)
                $data[]=DB::getGlobal()->getWarehouse($id);
        }

        foreach($data as $_=>&$v) {
		$v=[
		"id"=>$v->id,
                "address"=>$v->address,
                "name"=>$v->name,
                "deleted"=>$v->deleted
            ];
        }

        Response::json($data,200);
    }

    public static function handleGetSpecific(string $warehouse_id): never {
        $auth=Authorization::getCurrentAuthorization();

        if(!$auth->canViewWarehouse($warehouse_id))
            Response::forbidden();
        
        $v=DB::getGlobal()->getWarehouse($warehouse_id);
        if($v===null)
            Response::notFound();

        Response::json([
            "address"=>$v->address,
            "name"=>$v->name,
            "deleted"=>$v->deleted
        ],200);
    }

    public static function handleDelete(string $warehouse_id): never {
        $auth=Authorization::getCurrentAuthorization();

        if(!$auth->canDeleteWarehouse())
            Response::forbidden();
        
        Response::deleteResult(DB::getGlobal()->deleteWarehouse($warehouse_id));
    }

    public static function handlePut(string $warehouse_id): never {
        APIUtils::checkId($warehouse_id);
        $auth=Authorization::getCurrentAuthorization();

        $data=APIUtils::getValidatedBody([
            "name"=>"string",
            "address"=>"string"
        ]);

        $update=$auth->canModifyWarehouse($warehouse_id)&&($_GET["update"]??"")==="true";
        $create=$auth->canCreateWarehouse()&&($_GET["create"]??"")==="true";

        \EI\Logging\Logger::log($update." ".$create);

        if($update&&$create)
            Response::upsertResult(DB::getGlobal()->createOrUpdateWarehouse($warehouse_id,
                $data["name"],$data["address"]));
        elseif($update)
            Response::updateResult(DB::getGlobal()->updateWarehouse($warehouse_id,
                $data["name"],$data["address"]));
        elseif($create)
            Response::createResult(DB::getGlobal()->createWarehouse($warehouse_id,
                $data["name"],$data["address"]));
        else
            Response::forbidden();
    }

    public static function handleSearch(string $warehouse_id): never {
        if(!Authorization::getCurrentAuthorization()->canViewWarehouse($warehouse_id))
            Response::forbidden();
        
        $items=DB::getGlobal()->getStorageItems($warehouse_id,
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

    public static function handleMove(): never {
        $data=APIUtils::getValidatedBody([
            "from"=>"string",
            "to"=>"string"
        ]);
        APIUtils::checkId($data["from"]);
        APIUtils::checkId($data["to"]);

        if(!Authorization::getCurrentAuthorization()->canModifyWarehouse($data["from"]))
            Response::forbidden();

        Response::moveResult(DB::getGlobal()->moveWarehouse($data["from"],$data["to"]));
    }

}
