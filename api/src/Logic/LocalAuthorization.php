<?php

namespace EI\Logic;

class LocalAuthorization {
    public readonly bool $view;
    public readonly bool $create_add_operation;
    public readonly bool $create_remove_operation;
    public readonly bool $delete_operation;
    public readonly bool $modify_operation;
    public readonly bool $configure;

    public function __construct(
        bool $view,
        bool $create_add_operation,
        bool $create_remove_operation,
        bool $handle_operation,
        bool $configure) {
        
        $this->view = $view;
        $this->create_add_operation = $create_add_operation;
        $this->create_remove_operation = $create_remove_operation;
        $this->handle_operation = $handle_operation;
        $this->configure = $configure;
    }

    /**
     * @return array Local Authorization in the API format
     */
    public function getAuthorization():array {
        $ret=[];
        if($this->view)
            $ret[]="view";
        if($this->create_add_operation)
            $ret[]="create_add_operation";
        if($this->create_remove_operation)
            $ret[]="create_remove_operation";
        if($this->handle_operation)
            $ret[]="handle_operation";
        if($this->configure)
            $ret[]="configure";
        return $ret;
    }
}