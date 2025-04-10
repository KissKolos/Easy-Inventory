"use strict";

const API_ROOT="/api";

function set_token(token) {
    if(token===null)
        sessionStorage.removeItem('token');
    else
        sessionStorage.setItem('token',token);
}

function get_token() {
    return sessionStorage.getItem('token');
}

function request(method,url,body,err_lang_code) {
    return fetch(API_ROOT+url,{
        method:method,
        body:JSON.stringify(body),
        headers:{
            "Authorization": "Bearer "+get_token()
        }
    }).then(r=>{
        if(!r.ok) {
	    let err=Localization.CURRENT.get(err_lang_code+"."+r.status);
            if(err===undefined)
		err=Localization.CURRENT.get(err_lang_code);
            throw err;
        }else if(r.status==204||r.status==201){
            return [];
        }else{
            return r.json().catch(e=>{throw Localization.CURRENT.get(err_lang_code);});
        }
    });
}

function putParams(create) {
    return "?"+new URLSearchParams({create:create,update:!create}).toString();
}

function pageParams(q,archived,offset,length) {
    return "?"+new URLSearchParams({q:q,offset:offset,len:length,archived:archived}).toString();
}

function randomId() {
    return ""+Math.floor(Math.random() * 10000000);
}

setInterval(()=>{
    let token=get_token();
    if(token!==null) {
        request("POST","/token",undefined,null);
    }
},60000);
