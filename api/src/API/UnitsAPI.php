<?php

namespace EI\API;
use EI\Database\DB;
use EI\Logic\Authorization;
use EI\Response\Response;

class UnitsAPI {

    public static function handleGetAll() {
        if(Authorization::getCurrentAuthorization()->canViewTypes()){
            $data=DB::getGlobal()->getUnits(
                $_GET["q"]??"",
                intval($_GET["offset"]??""),
                intval($_GET["len"]??APIUtils::default_page_size),
                ($_GET["archived"]??"")==="true");
            /*foreach($data as $_=>&$item) {
                $item=[
                    "id"=>$item->id,
                    "name"=> $item->name,
                    "deleted"=>$item->deleted
                ];
            }*/
            Response::json($data,200);
        }else
            Response::forbidden();
    }

    public static function handleGetSpecific(string $unit_id) {
        if(Authorization::getCurrentAuthorization()->canViewTypes()){
            $unit=DB::getGlobal()->getUnit($unit_id);
            if($unit===null)
                Response::notFound();
            else
                Response::json($unit/*[
                    "name"=> $unit->name,
                    "deleted"=>$unit->deleted
                ]*/,200);
        }else
            Response::forbidden();
    }

    public static function handleDelete(string $unit_id) {
        if(Authorization::getCurrentAuthorization()->canDeleteType())
            Response::deleteResult(DB::getGlobal()->deleteUnit($unit_id));
        else
            Response::forbidden();
    }

    public static function handlePut(string $unit_id): never {
        APIUtils::checkId($unit_id);
        $auth=Authorization::getCurrentAuthorization();

        $data=APIUtils::getValidatedBody([
            "name"=>"string"
        ]);

        $update=$auth->canModifyType()&&($_GET["update"]??"")==="true";
        $create=$auth->canCreateType()&&($_GET["create"]??"")==="true";

        if($update&&$create)
            Response::upsertResult(DB::getGlobal()->createOrUpdateUnit($unit_id,$data["name"]));
        elseif($update)
            Response::updateResult(DB::getGlobal()->updateUnit($unit_id,$data["name"]));
        elseif($create)
            Response::createResult(DB::getGlobal()->createUnit($unit_id,$data["name"]));
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

        Response::moveResult(DB::getGlobal()->moveUnit($data["from"],$data["to"]));
    }

}