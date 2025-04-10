<?php

namespace EI\Logic;

class OperationStatistics {
    /**
     * @var float[]
     */
    public readonly array $times;

    /**
     * @param float[] $times
     */
    public function __construct(array $times) {
        $this->times = $times;
    }
}
