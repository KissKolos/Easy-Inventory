"use strict";

function createSelectButton(node,select_view_factory,callback,current,nullcode) {
    let nulltext=Localization.CURRENT.get(nullcode);
    if(current!==null)
        node.textContent=current.name;
    else
        node.textContent=nulltext;
        
    node.onclick=()=>{
        showDialog(Promise.resolve(new SelectDialog(select_view_factory(),(s)=>{
            if(s!==null)
                node.textContent=s.name;
            else
                node.textContent=nulltext;
            callback(s);
            return Promise.resolve();
        })));
    };
}