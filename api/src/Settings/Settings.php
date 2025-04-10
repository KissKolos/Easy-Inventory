<?php

namespace EI\Settings;

use EI\Database\DBConnection;
use EI\Logging\Logger;
use EI\Response\Response;


class Settings {

    private static Settings|null $settings=null;

    public readonly DBConnection $db_connection;
    
    public readonly LogSettings $success_log;
    public readonly LogSettings $bad_log;
    public readonly LogSettings $rejected_log;
    public readonly LogSettings $error_log;

    public readonly string|null $test_token;
    public readonly int $token_length;
    public readonly int $token_expiration;

    private function __construct(DBConnection $db_connection,LogSettings $success_log,LogSettings $bad_log,LogSettings $rejected_log,LogSettings $error_log,
        string|null $test_token,int $token_length,int $token_expiration) {
        $this->db_connection = $db_connection;
        $this->success_log = $success_log;
        $this->bad_log = $bad_log;
        $this->rejected_log = $rejected_log;
        $this->error_log = $error_log;
        $this->test_token = $test_token;
        $this->token_length = $token_length;
        $this->token_expiration=$token_expiration;
    }
    
    private static function readSettings():void {
        try{
            $settings = json_decode(file_get_contents(dirname(__FILE__)."/../../config.json"),true);
            $dbsettings = $settings["database"];

            self::$settings=new Settings(
                new DBConnection(
                    $dbsettings["host"],
                    $dbsettings["user"],
                    $dbsettings["password"],
                    $dbsettings["database_name"],
                    $dbsettings["port"]
                ),
                LogSettings::parse($settings["logging"]["success"]??[]),
                LogSettings::parse($settings["logging"]["bad"]??[]),
                LogSettings::parse($settings["logging"]["rejected"]??[]),
                LogSettings::parse($settings["logging"]["error"]??[]),
                $settings["test_token"]??null,
                $settings["token_length"]??64,
                $settings["token_expiration"]
            );
        }catch(\Exception $e) {
            Logger::force_log("Failed to load settings: ". $e->getMessage());
            Response::serverError();
        }
    }

    public static function getSettings():Settings {
        if(self::$settings===null) {
            self::readSettings();
        }
        return self::$settings;
    }

}