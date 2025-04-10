<?php

namespace EI\API;
use EI\Database\DB;
use EI\Logic\Authorization;
use EI\Response\Response;

class StorageLimitsAPI {

    public static function handleGetAll(string $warehouse_id,string $storage_id): never {
        if(Authorization::getCurrentAuthorization()->canViewWarehouse($warehouse_id)){
            $data=DB::getGlobal()->getStorageLimits($warehouse_id,$storage_id,
                $_GET["q"]??"",
                intval($_GET["offset"]??""),
                intval($_GET["len"]??APIUtils::default_page_size));
            if($data===null)
                Response::notFound();

            /*$ret=[];
            foreach($data as $_=>$d) {
                $ret[$d->item_type]=$d->amount;
            }*/
            Response::json($data,200);
        }else
            Response::forbidden();
    }

    public static function handlePut(string $warehouse_id,string $storage_id,string $item_type): never {
        $auth=Authorization::getCurrentAuthorization();
        if(!$auth->canModifyWarehouse($warehouse_id))
            Response::forbidden();

        $data=APIUtils::getValidatedBody([
            "amount"=>"integer"
        ]);

        Response::updateResult(DB::getGlobal()->setStorageLimit(
            $warehouse_id,$storage_id,$item_type,$data["amount"]));
    }

    public static function handleGetCapacity(string $warehouse_id,string $storage_id): never {
        if(Authorization::getCurrentAuthorization()->canViewWarehouse($warehouse_id)){
            $data=DB::getGlobal()->getStorageCapacity($warehouse_id,$storage_id,
                $_GET["q"]??"",
                intval($_GET["offset"]??""),
                intval($_GET["len"]??APIUtils::default_page_size));
            if($data===null)
                Response::notFound();
            /*$ret=[];
            foreach($data as $_=>$d) {
                $ret[$d->item_type]=$d->amount;
            }*/
            Response::json($data,200);
        }else
            Response::forbidden();
    }

}