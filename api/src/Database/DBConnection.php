<?php

namespace EI\Database;

class DBConnection {
    public readonly string $host;
    public readonly string $user;
    public readonly string $password;
    public readonly string $database_name;

    public readonly int $port;

    public function __construct(string $host,string $user,string $password,string $database_name,int $port) {
        $this->host = $host;
        $this->user = $user;
        $this->password = $password;
        $this->database_name = $database_name;
        $this->port = $port;
    }
}