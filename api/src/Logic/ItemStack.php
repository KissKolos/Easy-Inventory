<?php

namespace EI\Logic;

class ItemStack {
    public readonly Item $item;
    public readonly int $amount;
    public readonly int $available_amount;
    public readonly int|null $global_serial;
    public readonly string|null $manufacturer_serial;
    public readonly string|null $lot;

    public readonly Warehouse|null $warehouse;
    public readonly Storage|null $storage;

    public function __construct(Item $item, int $amount,int $available_amount,
            string|null $global_serial,string|null $manufacturer_serial, string|null $lot,
            Warehouse|null $warehouse=null,Storage|null $storage=null) {
        $this->item = $item;
        $this->amount = $amount;
        $this->available_amount = $available_amount;
        $this->global_serial = $global_serial;
        $this->manufacturer_serial = $manufacturer_serial;
        $this->lot = $lot;

        $this->warehouse = $warehouse;
        $this->storage = $storage;
    }
}