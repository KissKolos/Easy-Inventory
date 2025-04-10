<?php

namespace EI\Database;

use EI\Response\Response;
use EI\Logging\Logger;
use EI\Settings\Settings;

class DB {
    private static self|null $db = null;

    private \mysqli $mysqli;

    public function __construct(DBConnection $connection) {
        mysqli_report(MYSQLI_REPORT_ERROR | MYSQLI_REPORT_STRICT);

        try{
            $this->mysqli=mysqli_connect(
                $connection->host,
                $connection->user,
                $connection->password,
                $connection->database_name,
                $connection->port);
        }catch(\mysqli_sql_exception $e) {
            Logger::log("Database connection failed");
            Logger::error($e);
            Response::serverError();
        }
        
        try{
            $this->mysqli->set_charset("utf8mb4");
        }catch(\mysqli_sql_exception $e) {
            Logger::error($e);
            $this->fail("Setting charset failed");
        }
    }

    private function fail(string $message):never {
        Logger::log($message.": ".mysqli_error($this->mysqli));
        Response::serverError();
    }

    protected function getMysqli():\mysqli {
        return $this->mysqli;
    }

    protected function executePrepared2(string $sql,int &$modified,string $params,...$args):\mysqli_result|bool {
        $stmt=$this->getMysqli()->prepare($sql);
        $stmt->bind_param($params, ...$args);
        $stmt->execute();
        $modified=$stmt->affected_rows;
        return $stmt->get_result();
    }

    protected function executeSql(string $sql):\mysqli_result|bool {
        return $this->getMysqli()->query($sql);
    }

    function __destruct() {
        $this->mysqli->close();
    }

    public static function getGlobal():self {
        if(self::$db===null) {
            self::$db=new DB(Settings::getSettings()->db_connection);
        }
        return self::$db;
    }

    use AuthenticationDB;
    use AuthorizationDB;
    use OperationsDB;
    use UsersDB;
    use WarehouseDB;
    use ItemsDB;
    use UnitDB;
    use StorageDB;
    use ExportDB;
    use ImportDB;
    use StorageLimitsDB;
    use StatisticsDB;
}
