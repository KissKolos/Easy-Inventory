<?php

namespace EI\API;
use EI\Database\DB;
use EI\Logging\Logger;
use EI\Logic\Authorization;
use EI\Response\Response;

class StatisticsAPI {
	public static function getItemStats():never {
		if(!Authorization::getCurrentAuthorization()->canViewWarehouses())
            Response::forbidden();
		
		$item=$_GET["item"]??"";
		$from=intval($_GET["from"]??"");
		$to=intval($_GET["to"]??"");
		$operation=$_GET["operation"]??"";
        $warehouse=$_GET["warehouse"]??"";
        $storage=$_GET["storage"]??"";

		Response::json(DB::getGlobal()->getItemStatistics($warehouse,$storage,$item,$from,$to,$operation),200);
	}
    public static function getOperationStats():never {
		if(!Authorization::getCurrentAuthorization()->canViewWarehouses())
			Response::forbidden();

		$from=intval($_GET["from"]??"");
		$to=intval($_GET["to"]??"");
		$operation=$_GET["operation"]??"";
        $warehouse=$_GET["warehouse"]??"";

		Response::json(DB::getGlobal()->getOperationStatistics($warehouse,$from,$to,$operation),200);
	}
}
