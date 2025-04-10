<?php

namespace EI\Logging;

use EI\Settings\Settings;

class LogCLI {

    const ANSI_OK="\033[92m";
    const ANSI_ERR="\033[91m";
    const ANSI_WARN="\033[93m";
    const ANSI_CLEAR="\033[0m";

    /**
      *@return LogLine[]
      */
    private static function readLog():array {
        $lines=[];
        $file=null;
        try{
            $file = fopen("log.txt", "r");
            $line=null;
            while(($line=fgetcsv($file))!==false) {
                array_push($lines,new LogLine($line));
            }
        }catch(\Exception $e) {}
        if($file!==null) {
            try{
                fclose($file);
            }catch(\Exception) {}
        }
        return $lines;
    }

    public static function clear(array $opt):void {
        file_put_contents("log.txt","");
    }

    private static function codeColor(int $code):string {
        if($code<200)
            return "";
        elseif($code<300)
            return self::ANSI_OK;
        elseif($code<400)
            return "";
        elseif($code<500)
            return self::ANSI_WARN;
        else
            return self::ANSI_ERR;
    }

    private static function printVerbose(LogLine $l) {
        echo(self::codeColor($l->resp_code)."[".date("Y-m-d\\TH:i:sO",$l->start_time/1000)."][".$l->userid."][".$l->resp_code."] ".$l->method." ".$l->url." ".
            ($l->end_time-$l->start_time)."ms\n");
        echo("[".$l->src_ip.":".$l->src_port."][".$l->useragent."][".$l->http."]\n".self::ANSI_CLEAR);
        self::printPrefixed("[REQUEST]",$l->req_body);
        self::printPrefixed("[RESPONSE]",$l->resp_body);
        self::printPrefixed("[DEBUG]",$l->debug_log);
        echo(self::ANSI_ERR);
        self::printPrefixed("[ERROR]",$l->trace);
        echo(self::ANSI_CLEAR);
    }

    private static function printNormal(LogLine $l) {
        echo(self::codeColor($l->resp_code)."[".date("Y-m-d\\TH:i:sO",$l->start_time/1000)."][".$l->userid."][".$l->resp_code."] ".$l->method." ".$l->url." ".
            ($l->end_time-$l->start_time)."ms\n".self::ANSI_CLEAR);
    }

    public static function show(array $opt):void {
        $user=$opt["u"]??($opt["user"]??null);
        $success=($opt["s"]??($opt["success"]??null))===false;
        $bad=($opt["b"]??($opt["bad"]??null))===false;
        $rejected=($opt["r"]??($opt["rejected"]??null))===false;
        $error=($opt["e"]??($opt["error"]??null))===false;
        $verbose=($opt["v"]??($opt["verbose"]??null))===false;

        if(!($success||$bad||$rejected||$error)) {
            $success=true;
            $bad=true;
            $rejected=true;
            $error=true;
        }

        foreach(self::readLog() as $i=>$l) {
            if(!($user===null||$l->userid===$user))
                continue;

            if((200<=$l->resp_code&&$l->resp_code<300)&&!$success)
                continue;

            if(($l->resp_code==400)&&!$bad)
                continue;

            if((400<$l->resp_code&&$l->resp_code<500)&&!$rejected)
                continue;

            if((500<=$l->resp_code)&&!$error)
                continue;

            echo($i." ");
            if($verbose)
                self::printVerbose($l);
            else
                self::printNormal($l);
        }
    }

    private static function printPrefixed(string $prefix,string $msg):void {
        if($msg==="")
            return;
        foreach(explode("\n",$msg) as $l) {
            echo($prefix.$l."\n");
        }
    }

    public static function showSingle(string $indexv,array $opt):void {
        $index=intval($indexv);
        if($index<0)
            return;
        $lines=self::readLog();
        if($index>=count($lines))
            return;
        
        $l=$lines[$index];
        self::printVerbose($l);
    }
}