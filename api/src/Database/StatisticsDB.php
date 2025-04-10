<?php

namespace EI\Database;
use EI\Logic\ItemStatistics;
use EI\Logic\OperationStatistics;
use EI\Response\Response;

trait StatisticsDB
{
	use BaseTrait;
	
	public function getItemStatistics(string $warehouse,string $storage,string $item,int $from,int $to,string $operation_filter):ItemStatistics {
		$res=$this->fetchAllPreparedOrExit("
            select
                (COALESCE(sum(amount * if(type='add',-1,1)),0)+(
                    select
                        sum(amount)
                    from
                        item_stacks_view
                    where
                        item=? and
                        (? is null or locate(?,storage)>0) and
                        (? is null or locate(?,warehouse)>0)
                )) ch,
                0 `date`
            from operation_items_view
            where
                item=? and
                (? is null or locate(?,storage)>0) and
                (? is null or locate(?,warehouse)>0) and
                commited is not null and
                datediff(from_unixtime(commited),from_unixtime(?))>=0
        union
            (
            select
                sum(amount * if(type='add',-1,1)) ch,
                datediff(from_unixtime(commited),from_unixtime(?)) `date`
            from operation_items_view
            where
                item=? and
                (? is null or locate(?,storage)>0) and
                (? is null or locate(?,warehouse)>0) and
                commited is not null and
                datediff(from_unixtime(commited),from_unixtime(?))<0 and
                datediff(from_unixtime(commited),from_unixtime(?))>=0
            group by `date`
            )
        union
            (
            select
                0 ch,
                datediff(from_unixtime(?),from_unixtime(?)) `date`
            )
        ORDER by `date` desc
        ","ssssssssssiisssssiiii",
            $item,
            $storage,$storage,
            $warehouse,$warehouse,
            $item,
            $storage,$storage,
            $warehouse,$warehouse,
            $to,
            $to,
            $item,
            $storage,$storage,
            $warehouse,$warehouse,
            $to,$from,
            $from,$to
        );

        $start_amount=[];
        $curr=null;
        $currdate=-1;
        foreach($res as $row) {
            while($currdate-1>$row["date"]){
                $currdate-=1;
                array_push($start_amount,$curr);
            }
            if($curr===null)
                $curr=0+$row["ch"];
            else
                $curr+=$row["ch"];
            
            if($currdate==$row["date"])
                $start_amount[count($start_amount)]=$curr;
            else{
                array_push($start_amount,$curr);
                $currdate=$row["date"];
            }
        }

        $res=$this->fetchAllPreparedOrExit("
            select
                sum(amount) ch,
                datediff(from_unixtime(commited),from_unixtime(?)) `date`
            from operation_items_view
            where
                item=? and
                type='add' and
                (? is null or locate(?,storage)>0) and
                (? is null or locate(?,warehouse)>0) and
                (? is null or locate(?,operation)>0) and
                commited is not null and
                datediff(from_unixtime(commited),from_unixtime(?))<=0 and
                datediff(from_unixtime(commited),from_unixtime(?))>=0
            group by `date`
            ORDER by commited desc
        ","isssssssii",
            $to,
            $item,
            $storage,$storage,
            $warehouse,$warehouse,
            $operation_filter,$operation_filter,
            $to,$from
        );

        $add_amount=[];
        $currdate=0;
        foreach($res as $row) {
            while($currdate-1>$row["date"]){
                $currdate-=1;
                array_push($add_amount,0);
            }
            $currdate=$row["date"];
            array_push($add_amount,0+$row["ch"]);
        }
        while(count($add_amount)<count($start_amount))
            array_push($add_amount,0);

        $res=$this->fetchAllPreparedOrExit("
            select
                sum(amount) ch,
                datediff(from_unixtime(commited),from_unixtime(?)) `date`
            from operation_items_view
            where
                item=? and
                type='remove' and
                (? is null or locate(?,storage)>0) and
                (? is null or locate(?,warehouse)>0) and
                (? is null or locate(?,operation)>0) and
                commited is not null and
                datediff(from_unixtime(commited),from_unixtime(?))<=0 and
                datediff(from_unixtime(commited),from_unixtime(?))>=0
            group by `date`
            ORDER by commited desc
        ","isssssssii",
            $to,
            $item,
            $storage,$storage,
            $warehouse,$warehouse,
            $operation_filter,$operation_filter,
            $to,$from
        );

        $remove_amount=[];
        $currdate=0;
        foreach($res as $row) {
            while($currdate-1>$row["date"]){
                $currdate-=1;
                array_push($remove_amount,0);
            }
            $currdate=$row["date"];
            array_push($remove_amount,0+$row["ch"]);
        }
        while(count($remove_amount)<count($start_amount))
            array_push($remove_amount,0);

        return new ItemStatistics($start_amount,$remove_amount,$add_amount);
	}

	public function getOperationStatistics(string $warehouse,int $from,int $to,string $operation_filter):OperationStatistics {
		$res=$this->fetchAllPreparedOrExit("
            select
                avg(commited-created) a,
                datediff(from_unixtime(commited),from_unixtime(?)) `date`
            from operations_view
            where
                (? is null or locate(?,warehouse)>0) and
                (? is null or locate(?,id)>0) and
                commited is not null and
                datediff(from_unixtime(commited),from_unixtime(?))<=0 and
                datediff(from_unixtime(commited),from_unixtime(?))>=0
            group by `date`
        union
            (
            select
                0 a,
                datediff(from_unixtime(?),from_unixtime(?)) `date`
            )
        ORDER by `date` desc","issssiiii",$to,$warehouse,$warehouse,$operation_filter,$operation_filter,$to,$from,$from,$to);

        $times=[];
        $currdate=-1;
        foreach($res as $row) {
            while($currdate>$row["date"]){
                $currdate-=1;
                array_push($times,0);
            }
            
            if($currdate==$row["date"]) {
                array_push($times,0+$row["a"]);
                $currdate-=1;
            }
        }
        return new OperationStatistics($times);
	}
}
