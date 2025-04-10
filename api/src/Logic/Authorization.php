<?php

namespace EI\Logic;
use EI\Database\DB;
use EI\Response\Response;

class Authorization {

    private readonly bool $admin;
    private readonly bool $view_warehouses;
    private readonly bool $delete_warehouses;
    private readonly bool $create_warehouses;
    private readonly bool $modify_warehouses;
    private readonly bool $delete_types;
    private readonly bool $create_types;
    private readonly bool $modify_types;
    private readonly bool $view_users;
    private readonly bool $delete_users;
    private readonly bool $create_users;
    private readonly bool $modify_users;
    
    /**
     * @var LocalAuthorization[]
     */
    private readonly array $local_authorizations;

    public function __construct(
        bool $admin=false,
        bool $view_warehouses=false,
        bool $delete_warehouses=false,
        bool $create_warehouses=false,
        bool $modify_warehouses=false,
        bool $delete_types=false,
        bool $create_types=false,
        bool $modify_types=false,
        bool $view_users=false,
        bool $delete_users=false,
        bool $create_users=false,
        bool $modify_users=false,
        array $local_authorizations=[]) {
        
        $this->admin = $admin;
        $this->view_warehouses = $view_warehouses;
        $this->delete_warehouses = $delete_warehouses;
        $this->create_warehouses = $create_warehouses;
        $this->modify_warehouses = $modify_warehouses;
        $this->delete_types = $delete_types;
        $this->create_types = $create_types;
        $this->modify_types = $modify_types;
        $this->view_users = $view_users;
        $this->delete_users = $delete_users;
        $this->create_users = $create_users;
        $this->modify_users = $modify_users;
        $this->local_authorizations = $local_authorizations;
    }

    public static function createDefault():self {
        return new self();
    }

    public static function createAdmin():self {
        return new self(true);
    }

    public static function getAuthorization(string|null $user_id):Authorization {
        if($user_id===null) {
            return Authorization::createDefault();
        }else{
            return DB::getGlobal()->getAuthorization($user_id);
        }
    }

    public static function getCurrentAuthorization():Authorization {
        $user=Authentication::getCurrentUserId();
        if($user===null) {
            Response::unathorized();
        }else{
            return DB::getGlobal()->getAuthorization($user);
        }
    }

    private function getLocalAuth(string $warehouse_id):LocalAuthorization {
        return $this->local_authorizations[$warehouse_id]??new LocalAuthorization(false,false,false,false,false,false);
    }

    public function isAdmin():bool {
        return $this->admin;
    }



    public function canViewWarehouse(string $warehouse_id):bool {
        return $this->admin||$this->view_warehouses||$this->getLocalAuth($warehouse_id)->view;
    }

    public function canModifyWarehouse(string $warehouse_id):bool {
        return $this->admin||$this->modify_warehouses||$this->getLocalAuth($warehouse_id)->configure;
    }

    public function canCreateStorage(string $warehouse_id):bool {
        return $this->admin||$this->modify_warehouses||$this->getLocalAuth($warehouse_id)->configure;
    }

    public function canDeleteStorage(string $warehouse_id):bool {
        return $this->admin||$this->modify_warehouses||$this->getLocalAuth($warehouse_id)->configure;
    }

    public function canCreateWarehouse():bool {
        return $this->admin||$this->create_warehouses;
    }

    public function canDeleteWarehouse():bool {
        return $this->admin||$this->delete_warehouses;
    }

    public function canViewWarehouses():bool {
        return $this->admin||$this->view_warehouses;
    }

    public function canViewOperations(string $warehouse_id):bool {
        return $this->admin||$this->canViewWarehouse($warehouse_id);
    }

    public function canCreateAddOperations(string $warehouse_id):bool {
        return $this->admin||$this->getLocalAuth($warehouse_id)->create_add_operation;
    }

    public function canCreateRemoveOperations(string $warehouse_id):bool {
        return $this->admin||$this->getLocalAuth($warehouse_id)->create_remove_operation;
    }

    public function canModifyOperations(string $warehouse_id):bool {
        return $this->admin||$this->getLocalAuth($warehouse_id)->handle_operation;
    }

    public function canDeleteOperations(string $warehouse_id):bool {
        return $this->admin||$this->getLocalAuth($warehouse_id)->handle_operation;
    }


    public function canCreateUsers():bool {
        return $this->admin||$this->create_users;
    }

    public function canDeleteUser($user_id):bool {
        return $this->admin||($this->delete_users&&DB::getGlobal()->isUserManagedBy($user_id,Authentication::getCurrentUserId()));
    }

    public function canViewUsers():bool {
        return $this->admin||$this->view_users;
    }

    public function canViewUser(string $user_id):bool {
        return $this->canViewUsers()||
            $user_id===Authentication::getCurrentUserId()||
            DB::getGlobal()->isUserManagedBy($user_id,Authentication::getCurrentUserId());
    }

    public function canManageUser(string $user_id):bool {
        return $this->admin||($this->modify_users&&DB::getGlobal()->isUserManagedBy($user_id,Authentication::getCurrentUserId()));
    }

    public function canModifySystemAuthorization(string $user_id,string $authorization):bool {
        return $this->admin||(
            array_search($authorization,$this->getSystemAuthorizations())!==false&&
            $this->canManageUser($user_id));
    }

    public function canModifyLocalAuthorization(string $user_id,string $warehouse_id,string $authorization):bool {
        return $this->admin||(
            array_search($authorization,$this->getLocalAuth($warehouse_id)->getAuthorization())!==false&&
            $this->canManageUser($user_id));
    }

    public function canViewTypes():bool {
        return true;
    }

    public function canCreateType():bool {
        return $this->admin||$this->create_types;
    }

    public function canDeleteType():bool {
        return $this->admin||$this->delete_types;
    }

    public function canModifyType():bool {
        return $this->admin||$this->modify_types;
    }

    /**
     * @return string[]
     */
    public function getViewableWarehouses():array {
        $ret=[];
        foreach($this->local_authorizations as $key=>$value)
            if($value->view)
                $ret[]=$key;
        return $ret;
    }

    /**
     * @return array Local Authorizations in the API format
     */
    public function getLocalAuthorizations(string $warehouse_id):array {
        /*$ret=[];
        foreach($this->local_authorizations as $wh=>$auth) {
            $ret[$wh]=$auth->getAuthorization();
        }*/
        return $this->getLocalAuth($warehouse_id)->getAuthorization();
    }

    /**
     * @return array System Authorizations in the API format
     */
    public function getSystemAuthorizations():array {
        $ret=[];
        if($this->view_warehouses)
            $ret[]="view_warehouses";
        if($this->delete_warehouses)
            $ret[]="delete_warehouses";
        if($this->create_warehouses)
            $ret[]="create_warehouses";
        if($this->modify_warehouses)
            $ret[]="modify_warehouses";
        
        if($this->delete_types)
            $ret[]="delete_types";
        if($this->create_types)
            $ret[]="create_types";
        if($this->modify_types)
            $ret[]="modify_types";

        if($this->view_users)
            $ret[]="view_users";
        if($this->delete_users)
            $ret[]="delete_users";
        if($this->create_users)
            $ret[]="create_users";
        if($this->modify_users)
            $ret[]="modify_users";
        return $ret;
    }
}