<?php

namespace EI\API;
use EI\Database\DB;
use EI\Logic\Authorization;
use EI\Response\Response;

class StoragesAPI {

    public static function handleGetAll(string $warehouse_id) {
        if(Authorization::getCurrentAuthorization()->canViewWarehouse($warehouse_id)){
            $data=DB::getGlobal()->getStorages(
                $warehouse_id,
                $_GET["q"]??"",
                intval($_GET["offset"]??""),
                intval($_GET["len"]??APIUtils::default_page_size),
                ($_GET["archived"]??"")==="true");
            Response::json($data,200);
        }else
            Response::forbidden();
    }

    public static function handleGetSpecific(string $warehouse_id,string $storage_id) {
        if(Authorization::getCurrentAuthorization()->canViewWarehouse($warehouse_id)){
            $storage=DB::getGlobal()->getStorage($warehouse_id,$storage_id);
            if($storage===null)
                Response::notFound();
            else
                Response::json($storage,200);
        }else
            Response::forbidden();
    }

    public static function handleDelete(string $warehouse_id,string $storage_id) {
        if(Authorization::getCurrentAuthorization()->canDeleteStorage($warehouse_id))
            Response::deleteResult(DB::getGlobal()->deleteStorage($warehouse_id,$storage_id));
        else
            Response::forbidden();
    }

    public static function handlePut(string $warehouse_id,string $storage_id) {
        APIUtils::checkId($storage_id);
        $auth=Authorization::getCurrentAuthorization();

        $data=APIUtils::getValidatedBody([
            "name"=>"string"
        ]);

        $update=$auth->canModifyWarehouse($warehouse_id)&&($_GET["update"]??"")==="true";
        $create=$auth->canCreateStorage($warehouse_id)&&($_GET["create"]??"")==="true";

        if($update&&$create)
            Response::upsertResult(DB::getGlobal()->createOrUpdateStorage($warehouse_id,$storage_id,
                $data["name"]));
        elseif($update)
            Response::updateResult(DB::getGlobal()->updateStorage($warehouse_id,
                $storage_id,$data["name"]));
        elseif($create)
            Response::createResult(DB::getGlobal()->createStorage($warehouse_id,
                $storage_id,$data["name"]));
        else
            Response::forbidden();
    }

    public static function handleSearch(string $warehouse_id,string $storage_id): never {
        if(Authorization::getCurrentAuthorization()->canViewWarehouse($warehouse_id)){
            $items=DB::getGlobal()->getStorageItems(
                $warehouse_id,
                $storage_id,
                $_GET["q"]??"",
                ($_GET["warehouse"]??"")==="true",
                ($_GET["storage"]??"")==="true",
                ($_GET["lot"]??"")==="true",
                ($_GET["serial"]??"")==="true",
                intval($_GET["offset"]??""),
                intval($_GET["len"]??APIUtils::default_page_size)
            );
            if($items===null)
                Response::notFound();
            else{
                Response::json($items,200);
            }
        }else
            Response::forbidden();
    }

    public static function handleMove(string $warehouse_id): never {
        $data=APIUtils::getValidatedBody([
            "from"=>"string",
            "to"=>"string"
        ]);
        APIUtils::checkId($data["from"]);
        APIUtils::checkId($data["to"]);

        if(!Authorization::getCurrentAuthorization()->canModifyWarehouse($warehouse_id))
            Response::forbidden();

        Response::moveResult(DB::getGlobal()->moveStorage($warehouse_id,$data["from"],$data["to"]));
    }

}