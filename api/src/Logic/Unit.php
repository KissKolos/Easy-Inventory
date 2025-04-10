<?php

namespace EI\Logic;

class Unit {
    public readonly string $id;
    public readonly string $name;
    public readonly int|null $deleted;

    public function __construct(string $id, string $name, int|null $deleted) {
        $this->id = $id;
        $this->name = $name;
        $this->deleted=$deleted;
    }
}
