"use strict";

class UserDialog extends EditDialog {
    selected_manager=null;
    constructor(item_callback,item) {
        super("user-popup",item_callback,item);
    }
    initTemplate() {
        let t=this.view_template;
        let password_set=t.get("password-set");

        if(this.item!==null) {
            t.get("id").value=this.item.id;
            t.get("name").value=this.item.name;
            this.selected_manager=this.item.manager;
            t.get("password").disabled=true;
        }else{
            password_set.checked=true;
            password_set.disabled=true;
        }

        password_set.onchange=()=>t.get("password").disabled=!password_set.checked;
        
        createSelectButton(t.get("manager"),()=>new UserSelectView(),s=>this.selected_manager=s,this.selected_manager,"user.null");
    }
    getItem() {
        let t=this.view_template;
        let id=t.get("id").value;
        let name=t.get("name").value;
        let password=t.get("password-set").checked?t.get("password").value:null;

        if(id==="")
            id=randomId();

        if(name.length==0)
            name=id;

        return {id:id,name:name,manager:this.selected_manager,password:password};
    }
}

const system_authorizations=[
    "view_warehouses",
    "delete_warehouses",
    "create_warehouses",
    "modify_warehouses",
    "delete_types",
    "create_types",
    "modify_types",
    "view_users",
    "delete_users",
    "create_users",
    "modify_users"];
const local_authorizations=['view','create_add_operation','create_remove_operation','handle_operation','configure'];

class UserAuthorizationDialog extends DialogView {
    path;
    code_prefix;
    granted;
    auth_list;
    constructor(path,code_prefix,granted,auth_list) {
        super("user-authorization-popup");
        this.path=path;
        this.code_prefix=code_prefix;
        this.granted=granted;
        this.auth_list=auth_list;
    }
    hasOk() {
        return false;
    }
    createRow(content,a) {
        let row=new Template("authorization-template");
        row.get("authorization-name").textContent=Localization.CURRENT.get(this.code_prefix+a);
        row.get("authorization-grant").onclick=()=>{
            row.getNode().classList.add("loading");
            request("PUT",this.path+a).then(()=>{
                row.getNode().classList.add("granted");
            }).catch(()=>{

            }).then(()=>{
                row.getNode().classList.remove("loading");
            });
        };
        row.get("authorization-revoke").onclick=()=>{
            row.getNode().classList.add("loading");
            request("DELETE",this.path+a).then(()=>{
                row.getNode().classList.remove("granted");
            }).catch(()=>{
                
            }).then(()=>{
                row.getNode().classList.remove("loading");
            });
        };
        if(this.granted.indexOf(a)!=-1)
            row.getNode().classList.add("granted");
        content.appendChild(row.getNode());
    }
    initTemplate() {
        let t=this.view_template;
        let content=t.get("content");

        for(let a of this.auth_list) {
            this.createRow(content,a);
        }
    }
}

class UserView extends ListEditView {
    initTemplate() {
       super.initTemplate();
       this.view_template.get("list-archived-outer").style.display="none";
    }
    load(q,archived,offset,length) {
        return request("GET","/users"+pageParams(q,archived,offset,length),undefined,"user.fail.list");
    }
    createItemNode(item) {
        let t=new Template("user-template");
        t.get("id").textContent=item.id;
        t.get("name").textContent=item.name;
        t.get("manager").textContent=item.manager===null?"":item.manager.name;
        t.get("authorizations").onclick=()=>{
            showDialog(request("GET","/users/"+item.id+"/authorizations/system")
                .then(res=>new UserAuthorizationDialog("/users/"+item.id+"/authorizations/system/","authorization.system.",res,system_authorizations)));
        };
        t.get("local-authorizations").onclick=()=>{
            showDialog(Promise.resolve(new SelectDialog(new WarehouseSelectView(),(s)=>{
                showDialog(request("GET","/users/"+item.id+"/authorizations/local/"+s.id)
                        .then(res=>new UserAuthorizationDialog("/users/"+item.id+"/authorizations/local/"+s.id+"/","authorization.local.",res,local_authorizations)));
                return Promise.resolve();
            })));
        };
        this.initEditDelete(t,item);
        return t.getNode();
    }
    createEditView(item,item_callback) {
        return Promise.resolve(new UserDialog(item_callback,item));
    }
    delete(item) {
        return request("DELETE","/users/"+item.id,undefined,"user.fail.delete");
    }
    save(original,modified) {
        if(original===null||original.id===modified.id)
            return request("PUT","/users/"+modified.id+putParams(original===null),{
                name:modified.name,
                manager:modified.manager==null?null:modified.manager.id,
                password:modified.password
            },"user.fail."+(original===null?"create":"modify"));
        else
            return request("POST","/users",{
                from:original.id,
                to:modified.id
            },"user.fail.move").then(this.save(null,modified));
    }
}

class UserSelectView extends SelectView {
    hasArchived() {
        return false;
    }
    load(q,archived,offset,length) {
        return request("GET","/users"+pageParams(q,archived,offset,length),undefined,"user.fail.list").then(r=>{
            r.splice(0,0,null);
            return r;
        });
    }
    createItemNode(item) {
        let t=new Template("user-select-template");
        if(item===null) {
            t.get("id").textContent="";
            t.get("name").textContent=Localization.CURRENT.get("user.null");
        }else{
            t.get("id").textContent=item.id;
            t.get("name").textContent=item.name;
        }
        return t.getNode();
    }
}
