/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package easyinventorydesktop.user;

import easyinventoryapi.API;
import easyinventoryapi.Authorization;
import easyinventorydesktop.EditDialog;
import easyinventorydesktop.FormattedException;
import easyinventorydesktop.Localization;
import easyinventorydesktop.user.UserAuthorizationDialog.AuthorizationEntry;
import javafx.scene.control.CheckBox;

/**
 *
 * @author 3041TAN-08
 */
public class UserAuthorizationDialog extends EditDialog<AuthorizationEntry[]> {
    
    private final String warehouse_id;
    private final String user_id;

    private static AuthorizationEntry[] convert(Authorization a) {
        String[] codes=a.getAll();
        AuthorizationEntry[] entries=new AuthorizationEntry[codes.length];
        for(int i=0;i<codes.length;i++)
            entries[i]=new AuthorizationEntry(codes[i],a.isGranted(codes[i]));
        return entries;
    }
    
    public UserAuthorizationDialog(API api, Authorization original,String user_id,String warehouse_id) {
        super(api, convert(original),"user.authorizations");
        this.user_id=user_id;
        this.warehouse_id=warehouse_id;
    }

    @Override
    protected EditField[] createEditFields(AuthorizationEntry[] t) {
        EditField[] fields=new EditField[t.length];
        
        for(int i=0;i<t.length;i++) {
            CheckBox cb=new CheckBox();
            cb.setSelected(t[i].granted);
            fields[i]=new EditField("authorization."+(warehouse_id==null?"system":"local")+"."+t[i].authorization,cb);
        }
        return fields;
    }

    @Override
    protected AuthorizationEntry[] applyEdits(EditField[] f) {
        AuthorizationEntry[] auth=new AuthorizationEntry[f.length];
        for(int i=0;i<f.length;i++)
            auth[i]=new AuthorizationEntry(original[i].authorization,((CheckBox)f[i].node).isSelected());
        
        return auth;
    }

    @Override
    protected void save(AuthorizationEntry[] original, AuthorizationEntry[] modified) throws FormattedException {
        Localization.CURRENT.formatException("user.fail.auth", ()->{
            for(int i=0;i<original.length;i++)
                if(original[i].granted!=modified[i].granted){
                    if(warehouse_id==null) {
                        if(modified[i].granted)
                            api.grantSystemAuthorization(user_id, modified[i].authorization);
                        else
                            api.revokeSystemAuthorization(user_id, modified[i].authorization);
                    }else{
                        if(modified[i].granted)
                            api.grantLocalAuthorization(user_id,warehouse_id, modified[i].authorization);
                        else
                            api.revokeLocalAuthorization(user_id,warehouse_id, modified[i].authorization);
                    }
                }
            return true;
        });
    }
    
    public static class AuthorizationEntry {
        private final String authorization;
        private final boolean granted;

        public AuthorizationEntry(String authorization, boolean granted) {
            this.authorization = authorization;
            this.granted = granted;
        }
    }
    
}
