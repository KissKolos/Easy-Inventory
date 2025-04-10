<?php

namespace EI\Logic;

class StorageCapacity {
    public readonly Item $item;
    public readonly int $stored_amount;
    public readonly int $limit;

    public function __construct(Item $item, int $stored_amount, int $limit) {
        $this->item = $item;
        $this->stored_amount = $stored_amount;
        $this->limit = $limit;
    }
}