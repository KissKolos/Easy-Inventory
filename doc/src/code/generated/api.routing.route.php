private static function route($method, $pattern, $function): void {
    if($method !== $_SERVER['REQUEST_METHOD'])
        return;
    $url=explode("?",$_SERVER['REQUEST_URI'])[0];
    $url = explode("/",trim($url,"/"));
    $pattern =  explode("/",trim($pattern,"/"));
    $params = [];
    if(count($pattern) !== count($url))
        return;
    
    for($i = 0; $i < count($pattern); $i++)
        if($pattern[$i]== ":")
            $params[]= urldecode($url[$i]);
        else if($url[$i] != $pattern[$i])
            return;
    $function(...$params);
}
