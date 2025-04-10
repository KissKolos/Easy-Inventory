<?php

namespace EI\Database;

class DBCLI {
    private \mysqli $mysqli;

    public function __construct(DBConnection $connection) {
        mysqli_report(MYSQLI_REPORT_ERROR | MYSQLI_REPORT_STRICT);

        try{
            $this->mysqli=mysqli_connect(
                $connection->host,
                $connection->user,
                $connection->password,
                null,
                $connection->port);
        }catch(\mysqli_sql_exception $e) {
            $this->fail("Database connection failed");
        }
        
        try{
            $this->mysqli->set_charset("utf8mb4");
        }catch(\mysqli_sql_exception $e) {
            $this->fail("Setting charset failed");
        }
    }

    private function multiQuery(string $query) {
        $this->mysqli->begin_transaction();

        try{
            $this->mysqli->multi_query($query);
            do {
                if ($result = $this->mysqli->store_result()) {
                    while ($result->fetch_row()) {}
                }
            } while ($this->mysqli->next_result());

            $this->mysqli->commit();
        }catch(\Exception $e) {
            $this->mysqli->rollback();
            $this->fail("Failed to create database: ".$e);
        }
    }

    public function init(string $database):void {
        $query=file_get_contents("db.sql");
        $query=str_replace("DBNAME",$database,$query);
        foreach(explode('$',$query) as $q) {
                if(strlen(trim($q))>0)
                    $this->multiQuery($q);
        }
    }

    public function drop(string $database):void {
        $this->multiQuery(str_replace("DBNAME",$database,"drop database DBNAME;"));
    }

    private function fail(string $msg) {
        echo($msg);
        exit(1);
    }

    function __destruct() {
        $this->mysqli->close();
    }

    public static function dropDB() {
        $db=\EI\Settings\Settings::getSettings()->db_connection;
        $conn=new DBCLI($db);
        $conn->drop($db->database_name);
    }
    
    public static function initDB() {
        $db=\EI\Settings\Settings::getSettings()->db_connection;
        $conn=new DBCLI($db);
        $conn->init($db->database_name);
    }
}
