<?php

namespace EI\Database;

trait ArchiveDB {
    use BaseTrait;

    public function deleteArchived(int $time):void {
        $this->executePreparedOrExit("delete from operation_items inner join operations on operations.id=operation_items.operation
            where commited is not null and commited>?","i",$time);
        $this->executePreparedOrExit("delete from operations where commited is not null and commited>?","i",$time);
        $this->executePreparedOrExit("delete from items where deleted is not null and deleted>?","i",$time);
        $this->executePreparedOrExit("delete from units where deleted is not null and deleted>?","i",$time);
        $this->executePreparedOrExit("delete from storages where deleted is not null and deleted>?","i",$time);
        $this->executePreparedOrExit("delete from warehouses where deleted is not null and deleted>?","i",$time);
    }
}