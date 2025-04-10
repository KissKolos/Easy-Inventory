<?php

namespace EI\Logic;

class Item {
    public readonly string $id;
    public readonly Unit $unit;
    public readonly string $name;
    public readonly int|null $deleted;

    public function __construct(string $id, Unit $unit, string $name, int|null $deleted) {
        $this->id = $id;
        $this->unit = $unit;
        $this->name = $name;
        $this->deleted=$deleted;
    }
}
