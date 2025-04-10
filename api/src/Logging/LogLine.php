<?php

namespace EI\Logging;

use EI\Settings\Settings;

class LogLine {

    public readonly int $start_time;
    public readonly int $end_time;
    public readonly string $src_ip;
    public readonly int $src_port;
    public readonly string $useragent;
    public readonly string $http;
    public readonly string $userid;
    public readonly string $method;
    public readonly string $url;
    public readonly string $req_body;
    public readonly int $resp_code;
    public readonly string $resp_body;
    public readonly string $debug_log;
    public readonly string $trace;

    function __construct(array $line) {
        $this->start_time=intval($line[0]);
        $this->end_time=intval($line[1]);
        $this->src_ip=$line[2];
        $this->src_port=intval($line[3]);
        $this->useragent=$line[4];
        $this->http=$line[5];
        $this->userid=$line[6];
        $this->method=$line[7];
        $this->url=$line[8];
        $this->req_body=$line[9];
        $this->resp_code=intval($line[10]);
        $this->resp_body=$line[11];
        $this->debug_log=$line[12];
        $this->trace=$line[13];
    }

}