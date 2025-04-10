<?php

namespace EI\Settings;

use EI\Database\DBConnection;
use EI\Logging\Logger;
use EI\Response\Response;


class LogSettings {

    public readonly bool $enabled;
    public readonly bool $request_body;
    public readonly bool $response_body;
    public readonly bool $debug;
    public readonly bool $error;

    public function __construct(bool $enabled,bool $request_body,bool $response_body,bool $debug,bool $error) {
        $this->enabled = $enabled;
        $this->request_body = $request_body;
        $this->response_body = $response_body;
        $this->debug = $debug;
        $this->error = $error;
    }

    public static function parse(array $settings):self {
        return new self(
            $settings!==[],
            $settings["request_body"]??false,
            $settings["response_body"]??false,
            $settings["debug"]??false,
            $settings["error"]??false
        );
    }
    
}