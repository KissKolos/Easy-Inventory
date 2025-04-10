"use strict";

class PasswordDialog extends EditDialog {
    constructor(item_callback) {
        super("password-popup",item_callback,null);
    }
    initTemplate() {}
    getItem() {
        let t=this.view_template;
        let new_password=t.get("new_password").value;
        let new_password_again=t.get("new_password_again").value;
        let old_password=t.get("old_password").value;
        
        return {new_password:new_password,new_password_again:new_password_again,old_password:old_password};
    }
}

class ProfileView extends View {
    constructor() {
        super("profile-template");
    }
    initTemplate() {
        this.view_template.get("password-change").onclick=()=>{
            showDialog(Promise.resolve(new PasswordDialog((_,modified)=>{
                if(modified.new_password!==modified.new_password_again)
                    return Promise.reject(Localization.CURRENT.get("profile.password.fail.mismatch"));
                return request("PUT","/userinfo",{
                    old_password:modified.old_password,
                    new_password:modified.new_password
                });
            })));
        };
    }
}
