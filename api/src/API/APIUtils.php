<?php

namespace EI\API;
use EI\Response\Response;

class APIUtils {
    public const default_page_size=500;

    private static function isCompatible(string $target,string $type):bool {
        return $target==$type||($type=="integer"&&$target=="double");
    }

    static function validate(array|string $template,mixed $data) {
        $datatype=gettype($data);

        if(gettype($template)==="string") {
            if(!self::isCompatible($template,$datatype))
                Response::badRequest();
        }else{
            if($datatype!=="array")
                Response::badRequest();

            if(isset($template[0])) {
                foreach($data as $d) {
                    self::validate($template[0],$d);
                }
            }else{
                foreach($template as $key=>$templ) {
                    if(str_ends_with($key,"?")){
                        $d=$data[substr($key,0,strlen($key)-1)]??null;
                        if($d!==null)
                            self::validate($templ,$d);
                    }else
                        self::validate($templ,$data[$key]??null);
                }
            }
        }
    }

    public static function getValidatedBody(array $template):mixed {
        \EI\Logging\Logger::log(file_get_contents('php://input'));
        $data = json_decode(file_get_contents('php://input'), true);
        \EI\Logging\Logger::log(json_encode($data));

        self::validate($template,$data);
        return $data;
    }

    public static function getIntFromBody():int {
        $data = json_decode(file_get_contents('php://input'), true);

        if(gettype($data)==="integer")
            return $data;
        else
            Response::badRequest();
    }

    public static function checkId(string $s):void {
        if(strlen($s)==0)
            Response::badRequest();
    }
}
