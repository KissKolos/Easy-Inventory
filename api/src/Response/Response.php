<?php

namespace EI\Response;
use EI\Database\MoveResult;
use EI\Database\DeleteResult;
use EI\Database\UpsertResult;
use EI\Database\UpdateResult;


class Response {
    public static function notFound():never {
        self::empty(404);
    }

    public static function json($data,int $code):never {
        $jsondata=json_encode($data);
        \EI\Logging\Logger::set_response($code,$jsondata);
        http_response_code($code);
        header(header: "Content-Type: application/json; charset=utf-8");
        echo $jsondata;
        echo "\n";
        echo "\n";
        exit;
    }

    public static function empty(int $code):never {
        http_response_code($code);
        \EI\Logging\Logger::set_response($code,"");
        header(header: "Content-Type: none");
        exit;
    }

    public static function unathorized():never {
        self::empty(401);
    }

    public static function badRequest():never {
        self::empty(400);
    }

    public static function forbidden():never {
        self::empty(403);
    }

    public static function conflict():never {
        self::empty(409);
    }

    public static function created():never {
        self::empty(201);
    }

    public static function noContent():never {
        self::empty(204);
    }

    public static function notImplemented():never {
        self::empty(501);
    }

    public static function passwordUpdateResult(bool $success):never {
        if($success)
            self::noContent();
        else
            self::conflict();
    }

    public static function deleteResult(DeleteResult $result):never {
        switch($result) {
            case DeleteResult::Deleted:
                self::noContent();
            case DeleteResult::Missing:
                self::notFound();
            default:
                self::conflict();
        }
    }

    public static function deleteResult2(bool $success):never {
        if($success)
            self::noContent();
        else
            self::notFound();
    }

    public static function createResult(bool $success):never {
        if($success)
            self::created();
        else
            self::conflict();
    }

    public static function upsertResult(UpsertResult $result):never {
        switch($result) {
            case UpsertResult::Created:
                self::created();
            case UpsertResult::Updated:
                self::noContent();
            default:
                self::conflict();
        }
    }

    public static function updateResult(bool $success):never {
        if($success)
            self::noContent();
        else
            self::notFound();
    }

    public static function updateResult2(UpdateResult $result):never {
        switch($result) {
            case UpdateResult::Updated:
                self::noContent();
            case UpdateResult::NotFound:
                self::notFound();
            default:
                self::conflict();
        }
    }

    public static function moveResult(MoveResult $result):never {
        switch($result) {
            case MoveResult::Moved:
                self::noContent();
            case MoveResult::Missing:
                self::notFound();
            default:
                self::conflict();
        }
    }

    public static function serverError():never {
        self::empty(500);
    } 
}