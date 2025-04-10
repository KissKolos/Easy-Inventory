<?php

namespace EI\Routing;

use EI\API\AuthenticationAPI;
use EI\API\AuthorizationAPI;
use EI\API\DatabaseAPI;
use EI\API\ItemsAPI;
use EI\API\OperationAPI;
use EI\API\SearchAPI;
use EI\API\StorageLimitsAPI;
use EI\API\StoragesAPI;
use EI\API\UnitsAPI;
use EI\API\UsersAPI;
use EI\API\WarehousesAPI;
use EI\API\StatisticsAPI;
use EI\Logging\Logger;
use EI\Response\Response;

class ApiRouter {
    public static function handle() {
        Logger::set_logger(new Logger(
            $_SERVER["REMOTE_ADDR"],
            $_SERVER["REMOTE_PORT"],
            $_SERVER["HTTP_USER_AGENT"],
            $_SERVER["SERVER_PROTOCOL"],
            $_SERVER["REQUEST_METHOD"],
            $_SERVER["REQUEST_URI"],
            file_get_contents('php://input')
        ));

        $uri=explode("?",$_SERVER['REQUEST_URI']);
        $uri=explode("/",$uri[0]);
        array_shift($uri);
        array_shift($uri);
        
        self::route("GET", "api/users", fn(...$a) =>UsersAPI::handleGetAll());
        self::route("POST", "api/users", fn(...$a) =>UsersAPI::handleMove());
        self::route("DELETE", "api/users/:", fn(...$a) =>UsersAPI::handleDelete(...$a));
        self::route("PUT", "api/users/:", fn(...$a) => UsersAPI::handlePut(...$a));
        self::route("GET", "api/users/:", fn(...$a) => UsersAPI::handleGetSpecific(...$a));
        self::route("POST", "api/users/:/auth", fn(...$a) =>AuthenticationAPI::handleLogin(...$a));
        self::route("GET","api/users/:/authorizations/system", fn(...$a) =>AuthorizationAPI::handleGetSystem(...$a));
        self::route("GET","api/users/:/authorizations/local/:", fn(...$a) =>AuthorizationAPI::handleGetLocal(...$a));
        self::route("PUT","api/users/:/authorizations/local/:/:", fn(...$a) =>AuthorizationAPI::handleLocalPut(...$a));
        self::route("DELETE","api/users/:/authorizations/local/:/:", fn(...$a) =>AuthorizationAPI::handleLocalDelete(...$a));
        self::route("PUT","api/users/:/authorizations/system/:", fn(...$a) =>AuthorizationAPI::handleSystemPut(...$a));
        self::route("DELETE","api/users/:/authorizations/system/:", fn(...$a) =>AuthorizationAPI::handleSystemDelete(...$a));

        self::route("GET", "api/userinfo", fn(...$a) => AuthenticationAPI::handleUserinfo(...$a));
        self::route("PUT", "api/userinfo", fn(...$a) => AuthenticationAPI::handleUserinfoPut(...$a));
        self::route("POST", "api/token", fn(...$a) => AuthenticationAPI::handleTokenRenew(...$a));
        self::route("DELETE", "api/token", fn(...$a) => AuthenticationAPI::handleTokenDelete(...$a));

        self::route("PUT", "api/db", fn(...$a) => DatabaseAPI::handlePut());
        self::route("GET", "api/db", fn(...$a) => DatabaseAPI::handleGet());

        self::route("GET", "api/items", fn(...$a) => ItemsAPI::handleGetAll());
        self::route("POST", "api/items", fn(...$a) => ItemsAPI::handleMove());
        self::route("GET", "api/items/:", fn(...$a) => ItemsAPI::handleGetSpecific(...$a));
        self::route("DELETE", "api/items/:", fn(...$a) => ItemsAPI::handleDelete(...$a));
        self::route("PUT", "api/items/:", fn(...$a) => ItemsAPI::handlePut(...$a));

        self::route("GET", "api/units", fn(...$a) => UnitsAPI::handleGetAll());
        self::route("POST", "api/units", fn(...$a) => UnitsAPI::handleMove());
        self::route("GET", "api/units/:", fn(...$a) => UnitsAPI::handleGetSpecific(...$a));
        self::route("DELETE", "api/units/:", fn(...$a) => UnitsAPI::handleDelete(...$a));
        self::route("PUT", "api/units/:", fn(...$a) => UnitsAPI::handlePut(...$a));

        self::route("GET", "api/search", fn() => SearchAPI::handleSearch());

        self::route("GET", "api/warehouses", fn(...$a) => WarehousesAPI::handleGetAll());
        self::route("POST", "api/warehouses", fn(...$a) => WarehousesAPI::handleMove());
        self::route("GET", "api/warehouses/:", fn(...$a) => WarehousesAPI::handleGetSpecific(...$a));
        self::route("DELETE", "api/warehouses/:", fn(...$a) => WarehousesAPI::handleDelete(...$a));
        self::route("PUT", "api/warehouses/:", fn(...$a) => WarehousesAPI::handlePut(...$a));
        self::route("PATCH", "api/warehouses/:", fn(...$a) => WarehousesAPI::handlePatch(...$a));
        self::route("GET", "api/warehouses/:/search", fn(...$a) => WarehousesAPI::handleSearch(...$a));

        self::route("GET", "api/warehouses/:/operations", fn(...$a) => OperationAPI::handleGetAll(...$a));
        self::route("POST", "api/warehouses/:/operations", fn(...$a) => OperationAPI::handleMove(...$a));
        self::route("GET", "api/warehouses/:/operations/:", fn(...$a) => OperationAPI::handleGetSpecific(...$a));
        self::route("DELETE", "api/warehouses/:/operations/:", fn(...$a) => OperationAPI::handleDelete(...$a));
        self::route("PUT", "api/warehouses/:/operations/:", fn(...$a) => OperationAPI::handlePut(...$a));

        self::route("GET", "api/warehouses/:/storages", fn(...$a) => StoragesAPI::handleGetAll(...$a));
        self::route("POST", "api/warehouses/:/storages", fn(...$a) => StoragesAPI::handleMove(...$a));
        self::route("GET", "api/warehouses/:/storages/:", fn(...$a) => StoragesAPI::handleGetSpecific(...$a));
        self::route("DELETE", "api/warehouses/:/storages/:", fn(...$a) => StoragesAPI::handleDelete(...$a));
        self::route("PUT", "api/warehouses/:/storages/:", fn(...$a) => StoragesAPI::handlePut(...$a));
        self::route("GET", "api/warehouses/:/storages/:/search", fn(...$a) => StoragesAPI::handleSearch(...$a));

        self::route("GET", "api/warehouses/:/storages/:/limits", fn(...$a) => StorageLimitsAPI::handleGetAll(...$a));
        self::route("PUT", "api/warehouses/:/storages/:/limits/:", fn(...$a) => StorageLimitsAPI::handlePut(...$a));
        self::route("GET", "api/warehouses/:/storages/:/capacity", fn(...$a) => StorageLimitsAPI::handleGetCapacity(...$a));

        self::route("GET", "api/statistics/items", fn(...$a) => StatisticsAPI::getItemStats(...$a));
        self::route("GET", "api/statistics/operations", fn(...$a) => StatisticsAPI::getOperationStats(...$a));

        Response::notFound();
    }

    private static function route($method, $pattern, $function): void {
        if($method !== $_SERVER['REQUEST_METHOD'])
            return;

        $url=explode("?",$_SERVER['REQUEST_URI'])[0];
        $url = explode("/",trim($url,"/"));
        $pattern =  explode("/",trim($pattern,"/"));

        $params = [];

        if(count($pattern) !== count($url))
            return;
        
        for($i = 0; $i < count($pattern); $i++)
            if($pattern[$i]== ":")
                $params[]= urldecode($url[$i]);
            else if($url[$i] != $pattern[$i])
                return;

        $function(...$params);
    }
}
