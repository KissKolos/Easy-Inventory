<?php

namespace EI\Logic;

class StorageLimit {
    public readonly Item $item;
    public readonly int $amount;

    public function __construct(Item $item, int $amount) {
        $this->item = $item;
        $this->amount = $amount;
    }
}