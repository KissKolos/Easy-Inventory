<?php

namespace EI\Logic;

class UnitConversion {
    public readonly string $from_unit;
    public readonly string $to_unit;
    public readonly int $amount;

    public function __construct(string $from_unit, string $to_unit, int $amount) {
        $this->from_unit = $from_unit;
        $this->to_unit = $to_unit;
        $this->amount = $amount;
    }
}