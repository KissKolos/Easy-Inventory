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
