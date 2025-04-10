<?php

namespace EI\Logic;

class ItemStatistics {
    /**
     * @var int[]
     */
    public readonly array $start_amount;
    /**
     * @var int[]
     */
    public readonly array $remove_amount;
    /**
     * @var int[]
     */
    public readonly array $add_amount;

    /**
     * @param int[] $start_amount
     * @param int[] $remove_amount
     * @param int[] $add_amount
     */
    public function __construct(array $start_amount,array $remove_amount,array $add_amount) {
        $this->start_amount = $start_amount;
        $this->remove_amount = $remove_amount;
        $this->add_amount = $add_amount;
    }
}
