<?php

namespace EI\Logic;

class Operation {
    
    public readonly string $id;
    public readonly string|null $name;
    public readonly bool $is_add;
    public readonly Warehouse $warehouse;
    public readonly int $created;
    public readonly int|null $commited;
    /**
     * @var OperationItem[]
     */
    public readonly array $items;

    /**
     * @param string $id
     * @param string|null $name
     * @param bool $is_add
     * @param Warehouse $warehouse
     * @param int created
     * @param int|null commited
     * @param OperationItem[] $items
     */
    public function __construct(string $id, string|null $name,bool $is_add,
        Warehouse $warehouse,int $created,int|null $commited,array $items) {
        $this->id = $id;
        $this->name = $name;
        $this->is_add = $is_add;
        $this->warehouse = $warehouse;
        $this->created=$created;
        $this->commited=$commited;
        $this->items = $items;
    }
}
