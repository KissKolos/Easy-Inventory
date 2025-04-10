<?php

namespace EI\API;
use EI\Database\DB;
use EI\Logging\Logger;
use EI\Logic\Authorization;
use EI\Response\Response;

class ItemsAPI {

    public static function handleGetAll() {
        if(Authorization::getCurrentAuthorization()->canViewTypes()){
            $data=DB::getGlobal()->getItems(
                $_GET["q"]??"",
                intval($_GET["offset"]??""),
                intval($_GET["len"]??APIUtils::default_page_size),
                ($_GET["archived"]??"")==="true"
            );
            /*foreach($data as $_=>&$item) {
                $item=[
                    "id"=>$item->id,
                    "name"=> $item->name,
                    "unit"=>$item->unit,
                    "deleted"=>$item->deleted
                ];
            }*/
            Response::json($data,200);
        }
        else
            Response::forbidden();
    }

    public static function handleGetSpecific(string $item_id) {
        if(Authorization::getCurrentAuthorization()->canViewTypes()){
            $item=DB::getGlobal()->getItem($item_id);
            if($item===null)
                Response::notFound();
            else
                Response::json($item/*[
                    "name"=> $item->name,
                    "unit"=>$item->unit,
                    "deleted"=>$item->deleted
                ]*/,200);
        }else
            Response::forbidden();
    }

    public static function handleDelete(string $item_id) {
        if(Authorization::getCurrentAuthorization()->canDeleteType())
            Response::deleteResult(DB::getGlobal()->deleteItem($item_id));
        else
            Response::forbidden();
    }

    public static function handlePut(string $item_id): never {
        APIUtils::checkId($item_id);
        $auth=Authorization::getCurrentAuthorization();

        $data=APIUtils::getValidatedBody([
            "name"=>"string",
            "unit"=>"string"
        ]);

        $update=$auth->canModifyType()&&($_GET["update"]??"")==="true";
        $create=$auth->canCreateType()&&($_GET["create"]??"")==="true";
        
        if($update&&$create)
            Response::upsertResult(DB::getGlobal()->createOrUpdateItem($item_id,$data["unit"],$data["name"]));
        elseif($update)
            Response::updateResult(DB::getGlobal()->updateItem($item_id,$data["unit"],$data["name"]));
        elseif($create)
            Response::createResult(DB::getGlobal()->createItem($item_id,$data["unit"],$data["name"]));
        else
            Response::forbidden();
    }

    public static function handleMove(): never {
        $data=APIUtils::getValidatedBody([
            "from"=>"string",
            "to"=>"string"
        ]);
        APIUtils::checkId($data["from"]);
        APIUtils::checkId($data["to"]);

        if(!Authorization::getCurrentAuthorization()->canModifyType())
            Response::forbidden();

        Response::moveResult(DB::getGlobal()->moveItem($data["from"],$data["to"]));
    }

}