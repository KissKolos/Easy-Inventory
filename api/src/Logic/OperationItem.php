<?php

namespace EI\Logic;

class OperationItem {
    
    public readonly Item $item;
    public readonly int $amount;
    public readonly Storage|null $storage;
    public readonly int|null $global_serial;
    public readonly string|null $manufacturer_serial;
    public readonly string|null $lot;

    public function __construct(Item $item, int $amount,Storage|null $storage,
            string|null $global_serial,string|null $manufacturer_serial, string|null $lot) {
        $this->item = $item;
        $this->amount = $amount;
        $this->storage = $storage;
        $this->global_serial = $global_serial;
        $this->manufacturer_serial = $manufacturer_serial;
        $this->lot = $lot;
    }
}