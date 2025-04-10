<?php

namespace EI\Logic;

class Storage {
    public readonly Warehouse $warehouse;
    public readonly string $id;
    public readonly string $name;
    public readonly int|null $deleted;

    public function __construct(Warehouse $warehouse, string $id, string $name, int|null $deleted) {
        $this->warehouse = $warehouse;
        $this->id = $id;
        $this->name = $name;
        $this->deleted=$deleted;
    }
}
