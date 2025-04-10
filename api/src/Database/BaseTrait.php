<?php

namespace EI\Database;
use EI\Logging\Logger;
use EI\Response\Response;

trait BaseTrait {

    protected const ER_ROW_IS_REFERENCED="23000";
    protected const ER_DUP_UNIQUE="23000";

    protected abstract function getMysqli():\mysqli;

    /**
     * 
     * @param string $sql
     * @param int $modified sets this to the number of affected rows
     * @param string $params
     * @param array $args
     * @return \mysqli_result|bool false for insert/delete..., mysqli result for select/show...
     * @throws \mysqli_sql_exception
     */
    protected abstract function executePrepared2(string $sql,int &$modified,string $params,...$args):\mysqli_result|bool;

    /**
     * 
     * @param string $sql
     * @param string $params
     * @param array $args
     * @return \mysqli_result|bool false for insert/delete..., mysqli result for select/show...
     * @throws \mysqli_sql_exception
     */
    protected function executePrepared(string $sql,string $params,...$args):\mysqli_result|bool {
        $v=0;
        return $this->executePrepared2($sql,$v,$params,...$args);
    }

    /**
     * 
     * @param string $sql
     * @return \mysqli_result|bool false for insert/delete..., mysqli result for select/show...
     * @throws \mysqli_sql_exception
     */
    protected abstract function executeSql(string $sql):\mysqli_result|bool;

    protected function fetchAllPreparedOrExit(string $sql,string $params,...$args):array {
        try {
            return $this->executePrepared($sql, $params,...$args)->fetch_all(MYSQLI_ASSOC);
        } catch (\mysqli_sql_exception $e) {
            Logger::error($e);
            Response::serverError();
        }
    }

    protected function fetchPreparedOrExit(string $sql,string $params,...$args):array|null {
        try {
            return $this->executePrepared($sql, $params,...$args)->fetch_assoc();
        } catch (\mysqli_sql_exception $e) {
            Logger::error($e);
            Response::serverError();
        }
    }

    protected function executePreparedOrExit(string $sql,string $params,...$args):int {
        try {
            $modified=0;
            $this->executePrepared2($sql,$modified, $params,...$args);
            return $modified;
        } catch (\mysqli_sql_exception $e) {
            Logger::error($e);
            Response::serverError();
        }
    }

    protected function fetchAllSqlOrExit(string $sql):array {
        try {
            return $this->executeSql($sql)->fetch_all(MYSQLI_ASSOC);
        } catch (\mysqli_sql_exception $e) {
            Logger::error($e);
            Response::serverError();
        }
    }

    protected function fetchSqlOrExit(string $sql):array|null {
        try {
            return $this->executeSql($sql)->fetch_assoc();
        } catch (\mysqli_sql_exception $e) {
            Logger::error($e);
            Response::serverError();
        }
    }

    protected function executeSqlOrExit(string $sql):int {
        try {
            $this->executeSql($sql);
            return $this->getMysqli()->affected_rows;
        } catch (\mysqli_sql_exception $e) {
            Logger::error($e);
            Response::serverError();
        }
    }

    protected function rollback(\mysqli_sql_exception $e): never {
        try{
            $this->getMysqli()->rollback();
        }catch(\mysqli_sql_exception $e2) {
            Logger::error($e2);
        }
        Logger::error($e);
        Response::serverError();
    }

    protected function getLastId():int {
        return $this->executeSql("select LAST_INSERT_ID() id")->fetch_assoc()["id"];
    }

    protected function executeFailOrExit(string $fail_state,$success,$not_modified,$failed,string $sql,string $params,...$args) {
        try {
            $modified=0;
            $this->executePrepared2($sql,$modified, $params,...$args);
            if($modified>0)
                return $success;
            else
                return $not_modified;
        } catch (\mysqli_sql_exception $e) {
            if($e->getSqlState()===$fail_state)
                return $failed;
            
            Logger::error($e);
            Response::serverError();
        }
    }

    /*protected function executeDeleteOrExit(string $sql,string $params,...$args):DeleteResult {
        return $this->executeFailOrExit(self::ER_ROW_IS_REFERENCED,DeleteResult::Deleted,DeleteResult::Missing,DeleteResult::Failed,$sql,$params,...$args);
    }*/

    protected function executeMoveOrExit(string $sql,string $params,...$args):MoveResult {
        return $this->executeFailOrExit(self::ER_DUP_UNIQUE,MoveResult::Moved,MoveResult::Missing,MoveResult::Failed,$sql,$params,...$args);
    }

}