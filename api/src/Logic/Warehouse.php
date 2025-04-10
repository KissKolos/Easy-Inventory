<?php

namespace EI\Logic;

class Warehouse {
    public readonly string $id;
    public readonly string $name;
    public readonly string $address;
    public readonly int|null $deleted;

    public function __construct(string $id, string $name, string $address, int|null $deleted) {
        $this->id = $id;
        $this->name = $name;
        $this->address = $address;
        $this->deleted=$deleted;
    }
}
