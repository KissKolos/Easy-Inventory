<?php

namespace EI\Database;
use EI\Logic\ItemStack;
use EI\Logic\Item;
use EI\Logic\Unit;
use EI\Logic\Storage;
use EI\Logic\StorageLimit;
use EI\Logic\StorageCapacity;


trait StorageLimitsDB {
    use BaseTrait;

    /**
     * @return \EI\Logic\StorageLimit[]|null
     */
    public function getStorageLimits(string $warehouse_id,string $storage_id,string|null $q,int $offset,int $len):array|null {
        $res=$this->fetchAllPreparedOrExit("
        select null item,null item_name,null unit,null unit_name,null amount from storages_view where warehouse = ? and id=? and deleted is null
        union
        (select
            item_types.external_id item,
            item_types.name item_name,
            units.external_id unit,
            units.name unit_name,
            COALESCE(amount,0) amount
        from item_types
            inner join units on units.id=item_types.unit
            left join (
                select * from storage_limits
                where
                    storage=(
                        select storages.id from storages inner join warehouses on storages.warehouse=warehouses.id
                        where warehouses.external_id = ? and storages.external_id=? and storages.deleted is null
                    )
            ) storage_limits on storage_limits.item_type=item_types.id
        where
            item_types.deleted is null and
            (? is null or locate(?,item_types.name)>0 or locate(?,item_types.external_id)>0) limit ?,?);",'sssssssii',
        $warehouse_id,$storage_id,$warehouse_id,$storage_id,$q,$q,$q,$offset,$len);

        $first=array_shift($res);
        if($first===null||$first["item"]!==null)
            return null;

        foreach($res as &$row){
            $row=new StorageLimit(
                new Item(
                    $row["item"],
                    new Unit(
                        $row["unit"],
                        $row["unit_name"],
                        null
                    ),
                    $row["item_name"],
                    null
                ),
                $row["amount"]);
        }
        return $res;
    }

    public function setStorageLimit(string $warehouse_id,string $storage_id,string $item_type,int $amount):bool {
        return $this->executePreparedOrExit("insert ignore into storage_limits (storage,item_type,amount)
            values (
                (select storages.id from storages inner join warehouses on storages.warehouse=warehouses.id where warehouses.external_id=? and storages.external_id=?),
                (select id from item_types where external_id=?),
                ?
            )
            on duplicate key update
            amount = values(amount)",'sssi',$warehouse_id,$storage_id,$item_type,$amount)>0;
    }

    /**
     * @return \EI\Logic\StorageCapacity[]|null
     */
    public function getStorageCapacity(string $warehouse_id,string $storage_id,string|null $q,int $offset,int $len):array|null {
        $res=$this->fetchAllPreparedOrExit("
            select
                null item,
                null item_name,
                null unit,
                null unit_name,
                null stored_amount,
                null `limit`
                from storages_view where warehouse = ? and id=?
            union
            (select
                item_types.external_id item,
                item_types.name item_name,
                units.external_id unit,
                units.name unit_name,
                COALESCE(sum(st.total_amount),0) stored_amount,
                COALESCE(storage_limits.amount,0) `limit`
            from item_types
                inner join units on units.id=item_types.unit
                left join storage_limits on (
                    storage_limits.item_type=item_types.id and
                    storage_limits.storage=(
                        select storages.id from storages inner join warehouses on storages.warehouse=warehouses.id
                        where warehouses.external_id = ? and storages.external_id=? and storages.deleted is null
                    )
                )
                left join (select warehouse,storage,item,sum(amount) total_amount from item_stacks_view group by warehouse,storage,item) st on (
                    st.warehouse=? and
                    st.storage=? and
                    st.item=item_types.external_id
                )
            where
                item_types.deleted is null
            group by item_types.id
            having
                (? is null or locate(?,item_types.name)>0 or locate(?,item_types.external_id)>0) limit ?,?);",'sssssssssii',
            $warehouse_id,$storage_id,$warehouse_id,$storage_id,$warehouse_id,$storage_id,$q,$q,$q,$offset,$len);
        
        $first=array_shift($res);
        if($first===null||$first["item"]!==null)
            return null;

        foreach($res as &$row){
            $row=new StorageCapacity(
                new Item(
                    $row["item"],
                    new Unit(
                        $row["unit"],
                        $row["unit_name"],
                        null
                    ),
                    $row["item_name"],
                    null
                ),
                $row["stored_amount"],
                $row["limit"]
            );
        }
        return $res;
    }

}