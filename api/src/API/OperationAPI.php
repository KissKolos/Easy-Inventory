<?php

namespace EI\API;
use EI\Database\DB;
use EI\Database\UpsertResult;
use EI\Logic\Authentication;
use EI\Logic\Authorization;
use EI\Logic\OperationItem;
use EI\Logic\Item;
use EI\Logic\Unit;
use EI\Logic\Storage;
use EI\Logic\Warehouse;
use EI\Response\Response;

class OperationAPI {

    public static function handleGetAll(string $warehouse_id): never {
        if(Authorization::getCurrentAuthorization()->canViewOperations($warehouse_id)){
            $data=DB::getGlobal()->getOperations(
                $warehouse_id,
                $_GET["q"]??"",
                intval($_GET["offset"]??""),
                intval($_GET["len"]??APIUtils::default_page_size),
                ($_GET["archived"]??"")==="true"
            );
            /*foreach($data as &$d) {
                $d=[
                    "id"=>$d->id,
                    "is_add"=>$d->is_add,
        		    "name"=>$d->name,
		            "created"=>$d->created,
	        	    "commited"=>$d->commited,
                    "items"=>$d->items
                ];
            }*/
            Response::json($data,200);
        }else
            Response::forbidden();
    }

    public static function handleGetSpecific(string $warehouse_id,string $operation_id): never {
        if(!Authorization::getCurrentAuthorization()->canViewOperations($warehouse_id))
            Response::forbidden();
        
        $v=DB::getGlobal()->getOperation($warehouse_id,$operation_id);
        if($v===null)
            Response::notFound();
        
        Response::json($v/*[
            "is_add"=>$v->is_add,
            "name"=>$v->name,
            "created"=>$d->created,
	        "commited"=>$d->commited,
            "items"=>$v->items
        ]*/,200);
    }

    public static function handleDelete(string $warehouse_id,string $operation_id): never {
        if(!Authorization::getCurrentAuthorization()->canDeleteOperations($warehouse_id))
            Response::forbidden();
        
        $data=APIUtils::getValidatedBody([
            "cancel"=>"boolean"
        ]);

        if($data["cancel"])
            Response::deleteResult(DB::getGlobal()->deleteOperation($warehouse_id,$operation_id));
        else
            Response::deleteResult(DB::getGlobal()->commitOperation($warehouse_id,$operation_id));
    }

    public static function handlePut(string $warehouse_id,string $operation_id) {
        APIUtils::checkId($operation_id);
        $auth=Authorization::getCurrentAuthorization();
        if(!($auth->canCreateAddOperations($warehouse_id)||$auth->canCreateRemoveOperations($warehouse_id)))
            Response::forbidden();
        
        $data=APIUtils::getValidatedBody([
            "is_add"=>"boolean",
            "name"=>"string",
            "items"=>[[
                "type"=>"string",
                "amount"=>"integer",
                "storage?"=>"string",
                "global_serial?"=>"integer",
                "manufacturer_serial?"=>"string",
                "lot?"=>"string"
            ]]
        ]);

        if($data["is_add"]){
            if((!$auth->canCreateAddOperations($warehouse_id)))
                Response::forbidden();
        }
        else{
            if(!$auth->canCreateRemoveOperations($warehouse_id))
                Response::forbidden();
        }

        $items=[];

        foreach($data["items"] as $item) {
            $gs=$item["global_serial"]??null;
            if($gs===null&&($item["manufacturer_serial"]??null)!==null)
                Response::badRequest();
            
            if($gs!==null&&$gs<0)
                Response::badRequest();

            $items[]=new OperationItem(
                new Item($item["type"],new Unit("","",null),"",null),
                $item["amount"],
                ($item["storage"]??null)===null?null:new Storage(new Warehouse("","","",null),$item["storage"],"",null),
                $item["global_serial"]??null,
                $item["manufacturer_serial"]??null,
                $item["lot"]??null
            );
        }

        if($data["is_add"])
            Response::createResult(DB::getGlobal()->createAddOperation($operation_id,$data["name"],$warehouse_id,$items));
        else
            Response::createResult(DB::getGlobal()->createRemoveOperation($operation_id,$data["name"],$warehouse_id,$items));
    }

    public static function handleMove(string $warehouse_id): never {
        $data=APIUtils::getValidatedBody([
            "from"=>"string",
            "to"=>"string"
        ]);
        APIUtils::checkId($data["from"]);
        APIUtils::checkId($data["to"]);

        if(!Authorization::getCurrentAuthorization()->canModifyOperations($warehouse_id))
            Response::forbidden();

        Response::createResult(DB::getGlobal()->moveOperation($warehouse_id,$data["from"],$data["to"]));
    }

}
