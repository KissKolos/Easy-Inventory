<?php

namespace EI\Logging;

use EI\Settings\Settings;

class Logger {

    private static self|null $logger = null;

    private readonly int $start_time;

    private readonly string $src_ip;
    private readonly int $src_port;

    private readonly string $useragent;
    private readonly string $http;
    private string $userid="";

    private readonly string $method;
    private readonly string $url;
    private readonly string $req_body;
    
    private int $resp_code=0;
    private string $resp_body="";

    private string $debug_log="";
    private string $trace="";
    
    private static function militime():int {
        return intval(microtime(true)*1000);
    }

    function __construct($src_ip,$src_port,$useragent,$http,$method,$url,$req_body) {
        $this->start_time=self::militime();
        $this->src_ip=$src_ip;
        $this->src_port=$src_port;
        $this->useragent=$useragent;
        $this->http=$http;
        $this->method=$method;
        $this->url=$url;
        $this->req_body=$req_body;
    }

    function __destruct() {
        $settings=Settings::getSettings();
        $lsettings=null;
        if(200<=$this->resp_code&&$this->resp_code<300)
            $lsettings=$settings->success_log;
        elseif(400===$this->resp_code)
            $lsettings=$settings->bad_log;
        elseif(400<$this->resp_code&&$this->resp_code<500)
            $lsettings=$settings->rejected_log;
        else
            $lsettings=$settings->error_log;
        
        if(!$lsettings->enabled)
            return;

        $file=null;
        try{
            $file = fopen(dirname(__FILE__)."/../../log.txt", "a");
            fputcsv($file,[
                $this->start_time,
                self::militime(),
                $this->src_ip,
                $this->src_port,
                $this->useragent,
                $this->http,
                $this->userid,
                $this->method,
                $this->url,
                $lsettings->request_body?$this->req_body:"",
                $this->resp_code,
                $lsettings->response_body?$this->resp_body:"",
                $lsettings->debug?$this->debug_log:"",
                $lsettings->error?$this->trace:""
            ]);
        }catch(\Exception $e) {}
        if($file!==null) {
            try{
                fclose($file);
            }catch(\Exception) {}
        }
    }

    public static function set_logger(self $l):void {
        self::$logger=$l;
    }

    public static function set_userid(string|null $userid):void {
        if(self::$logger!==null)
            self::$logger->userid=$userid??"";
    }

    public static function set_response(int $code,string $body):void {
        if(self::$logger!==null){
            self::$logger->resp_code=$code;
            self::$logger->resp_body=$body;
        }
    }

    public static function log(string $message):void {
        //if(Settings::getSettings()->logging)
        //   self::force_log($message);
        if(self::$logger!==null)
            self::$logger->debug_log.=$message."\n";
    }

    public static function error($e):void {
        if(self::$logger!==null){
            $res=$e->getMessage()."\n";
            $trace=$e->getTrace();
            foreach($trace as $i=>$t) {
                $args="";
                foreach($t["args"] as $ii=>$a) {
                    $args=$args." ".var_export($a,true);
                }
                $res.=$t["file"].":".$t["line"]." ".$t["function"]."(".$args.")\n";
            }
            
            self::$logger->trace=$res;
        }
    }
}