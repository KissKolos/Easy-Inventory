<?php

namespace EI\Logic;

class User {
    public readonly string $id;
    public readonly string $name;
    public readonly User|null $manager;

    public function __construct(string $id, string $name, User|null $manager) {
        $this->id = $id;
        $this->name = $name;
        $this->manager = $manager;
    }

    public function getAuthorization():Authorization {
        return Authorization::getAuthorization($this->id);
    }
}