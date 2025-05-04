class Logger {
    function __construct($src_ip,$src_port,$useragent,$http,$method,$url,$req_body) { ... }
    function __destruct() { ... }
    public static function set_logger(self $l):void { ... }
    public static function set_userid(string|null $userid):void { ... }
    public static function set_response(int $code,string $body):void { ... }
    public static function log(string $message):void { ... }
    public static function error($e):void { ... }
}