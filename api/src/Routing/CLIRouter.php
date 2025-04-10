<?php

namespace EI\Routing;

use \EI\Logging\LogCLI;
use \EI\Database\DBCLI;
use \EI\Database\DB;

class CLIRouter {

    private static function createUser(string $userid,string $name,string $pass) {
        DB::getGlobal()->createUser($userid,$name,\EI\Logic\Authentication::createHash($pass),null);
    }

    private static function passwordUser(string $userid,string $pass) {
        $user=DB::getGlobal()->getUser($userid);
        DB::getGlobal()->updateUser($userid,$user->name,\EI\Logic\Authentication::createHash($pass),$user->manager===null?null:$user->manager->id);
    }

    private static function deleteUser(string $userid) {
        DB::getGlobal()->deleteUser($userid);
    }

    public static function handleUser(array $argv):never {
        self::route($argv,["create",null,null,null],"",[],fn(...$a) => self::createUser(...$a));
        self::route($argv,["password",null,null],"",[],fn(...$a) => self::passwordUser(...$a));
        self::route($argv,["delete",null],"",[],fn(...$a) => self::deleteUser(...$a));

        echo("usage:
            php user create <userid> <name> <password>
                creates a new admin
            php user password <userid> <password>
                sets a user's password
            php user delete <userid>
                deletes a user");
        exit(0);
    }

    private static function parseTime(string $time):int {
        if($time==="all")
            return 0;
        if(str_ends_with($time,"y")) {
            $t=intval(substr($time,0,strlen($time)-1));
            return time()-$t*60*60*24*365;
        }
        if(str_ends_with($time,"w")) {
            $t=intval(substr($time,0,strlen($time)-1));
            return time()-$t*60*60*24*7;
        }
        if(str_ends_with($time,"d")) {
            $t=intval(substr($time,0,strlen($time)-1));
            return time()-$t*60*60*24;
        }
        return time();
    }

    private static function deleteArchived(string $time) {
        DB::getGlobal()->deleteArchived(self::parseTime($time));
    }

    public static function handleArchive(array $argv):never {
        self::route($argv,["delete",null],"",[],fn(...$a) => self::deleteArchived(...$a));

        echo("usage:
            php archive delete <time>
                deletes archived data older than time
                if time is 'all' then it will delete all archived data
                time can be specified in years (5y), weeks (5w) or days (5d)");
        exit(0);
    }

    private static function saveBackup($file) {
        $data=DB::getGlobal()->export();
        file_put_contents($file,json_encode($data));
    }

    private static function loadBackup($file) {
        $data=file_get_contents($file);
        DB::getGlobal()->import(json_decode($data,true));
    }

    public static function handleBackup(array $argv):never {
        self::route($argv,["save",null],"",[],fn(...$a) => self::saveBackup(...$a));
        self::route($argv,["load",null],"",[],fn(...$a) => self::loadBackup(...$a));

        echo("usage: php backup <command> <file>
        commands:
            save    saves backup to file
            load    loads backup from file (WARNING!!! This command wipes the database)");
        exit(0);
    }

    public static function handleLog(array $argv):never {
        self::route($argv,["clear"],"",[],fn(...$a) => LogCLI::clear(...$a));
        self::route($argv,["show"],"vsbreu:",["verbose","success","bad","rejected","error","user:"],fn(...$a) => LogCLI::show(...$a));
        self::route($argv,["show",null],"",[],fn(...$a) => LogCLI::showSingle(...$a));
        
        echo("usage:
            php log clear              clears logs
            php log [OPTION] show      shows logs
                -v, --verbose
                    show more info
                -s, --success
                    show successful requests
                -b, --bad
                    show bad requests
                -r, --rejected
                    show rejected requests
                -e, --error
                    show failed requests
                -u, --user=USERID
                    show requests made by a given user id
            php log show [INDEX]       shows a single log entry");
        exit(0);
    }

    public static function handleDB(array $argv):never {
        self::route($argv,["drop"],"",[],fn(...$a) => DBCLI::dropDB(...$a));
        self::route($argv,["init"],"",[],fn(...$a) => DBCLI::initDB(...$a));

        echo("usage: php db [command]
        commands:
            drop     drops the database specified in the settings
            init     creates the database specified in the settings");
        exit(0);
    }

    private static function route(array $argv,array $pattern,string $short,array $long, $function): void {
        $params = [];
    
        $start_index=0;
        $opt=getopt($short,$long,$start_index);
    
        if(count($pattern) !== count($argv)-$start_index)
            return;
        
        for($i = 0; $i < count($pattern); $i++)
            if($pattern[$i]===null)
                $params[]=$argv[$start_index+$i];
            else if($argv[$start_index+$i] !== $pattern[$i])
                return;
    
        $params[]=$opt;
        $function(...$params);
        exit(0);
    }
}