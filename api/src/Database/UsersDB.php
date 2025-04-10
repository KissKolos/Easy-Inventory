<?php

namespace EI\Database;
use EI\Logging\Logger;
use EI\Logic\User;
use EI\Response\Response;

trait UsersDB {
    use BaseTrait;

    /**
     * @return User[]
     */
    public function getUsers(string|null $q,int $offset,int $len):array {
        $res=$this->fetchAllPreparedOrExit("select id,name,manager,manager_name from users_view where
        (? is null or locate(?,name)>0 or locate(?,id)>0) order by name limit ?,?",
        "sssii",$q,$q,$q,$offset,$len);
        foreach($res as &$row){
            $row=new User($row["id"],$row["name"],$row["manager"]==null?null:new User($row["manager"],$row["manager_name"],null));
        }
        return $res;
    }

    /**
     * @return User[]
     */
    public function getUsersManagedBy(string $manager_id,string|null $q,int $offset,int $len):array {
        $res=$this->fetchAllPreparedOrExit("with recursive users_tree as (
            select id,name,manager,manager_name from users_view where id=?
            union all
            select u.id,u.name,u.manager,u.manager_name from users_view u inner join users_tree on u.manager=users_tree.id
        )
        select * from users_tree where
        (? is null or locate(?,name)>0 or locate(?,id)>0) order by name limit ?,?","ssssii",$manager_id,$q,$q,$q,$offset,$len);
        foreach($res as &$row){
            $row=new User($row["id"],$row["name"],$row["manager"]==null?null:new User($row["manager"],$row["manager_name"],null));
        }
        return $res;
    }

    public function isUserManagedBy(string $user_id,string $manager_id):bool {
        return $this->fetchPreparedOrExit("with recursive users_tree as (
            select manager from users_view where id=?
            union all
            select u.manager from users_view u inner join users_tree on u.id=users_tree.manager where u.id!=?
        ) select 1 from users_tree where manager is null","ss",$user_id,$manager_id)===null;
    }

    public function getUser(string $user_id):User|null {
        $row=$this->fetchPreparedOrExit("select name,manager,manager_name from users_view where id=?","s",$user_id);
        if($row!=null)
            $row=new User($user_id,$row["name"],$row["manager"]==null?null:new User($row["manager"],$row["manager_name"],null));
        return $row;
    }

    public function reassignUser(string $user_id,string|null $manager_id):UpdateResult {
        return $this->executeFailOrExit("45000",UpdateResult::Updated,UpdateResult::NotFound,UpdateResult::Failed,
            "update users_view set manager=? where id=?","ss",$manager_id,$user_id)>0;
    }

    public function deleteUser(string $user_id):DeleteResult {
        try{
            $this->getMysqli()->begin_transaction(MYSQLI_TRANS_START_READ_WRITE);

            if($this->executePrepared("select * from users_view where manager=?","s",$user_id)->fetch_assoc()!==null){
                $this->getMysqli()->commit();
                return DeleteResult::Failed;
            }

            $this->executePrepared("delete a,g from
                users left join authorization a on a.user=id
                left join global_authorization g on g.user=id
                where external_id=?
            ","s",$user_id);
            $modified=0;
            $this->executePrepared2("delete from users where external_id=?",$modified,"s",$user_id);

            $this->getMysqli()->commit();
            if($modified>0)
                return DeleteResult::Deleted;
            else
                return DeleteResult::Missing;
        }catch(\mysqli_sql_exception $e) {
            $this->rollback($e);
        }
    }

    public function createUser(string $user_id,string $name,string $pass_hash,string $manager_id):bool {
        return $this->executePreparedOrExit("insert ignore into users (external_id,name,manager,passhash) values
                (?,?,(select u.id from users u where u.external_id=?),?)","ssss",$user_id,$name,$manager_id,$pass_hash)>0;
    }

    public function createOrUpdateUser(string $user_id,string $name,string $pass_hash,string $manager_id):UpsertResult {
        return UpsertResult::fromModified($this->executePreparedOrExit("insert ignore into users (external_id,name,manager,passhash) values
                (?,?,(select u.id from users u where u.external_id=?),?)
                on duplicate key update
                external_id = values(external_id),
                name = values(name),
                manager = values(manager),
                passhash = values(passhash)
                ","ssss",$user_id,$name,$manager_id,$pass_hash));
    }

    public function updateUser(string $user_id,string $name,string $pass_hash,string $manager_id):UpdateResult {
        return $this->executeFailOrExit("45000",UpdateResult::Updated,UpdateResult::NotFound,UpdateResult::Failed,"update users set
            name=?,passhash=?,manager=(select u.id from users u where u.external_id=?)
            where external_id=?","ssss",$name,$pass_hash,$manager_id,$user_id);
    }

    public function updateUserNoPassword(string $user_id,string $name,string $manager_id):UpdateResult {
        return $this->executeFailOrExit("45000",UpdateResult::Updated,UpdateResult::NotFound,UpdateResult::Failed,"update users set
            name=?,manager=(select u.id from users u where u.external_id=?)
            where external_id=?","sss",$name,$manager_id,$user_id);
    }

    public function moveUser(string $user_id,string $new_id):MoveResult {
        return $this->executeMoveOrExit("update users set external_id=? where external_id=?","ss",$new_id,$user_id);
    }
}
