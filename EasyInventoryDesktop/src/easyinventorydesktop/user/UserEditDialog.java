/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package easyinventorydesktop.user;

import easyinventoryapi.API;
import easyinventoryapi.User;
import easyinventorydesktop.EditDialog;
import easyinventorydesktop.FormattedException;
import easyinventorydesktop.Localization;
import easyinventorydesktop.SelectButton;
import easyinventorydesktop.UIUtils;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 *
 * @author 3041TAN-08
 */
public class UserEditDialog extends EditDialog<User> {

    public UserEditDialog(API api, User original) {
        super(api, original,"user.edit");
    }
    
    @Override
    protected EditField[] createEditFields(User t) {
        t=t==null?new User("","",null,null):t;        
        
        return new EditField[]{
            new EditField("user.id",new TextField(t.id)),
            new EditField("user.name",new TextField(t.name)),
            new EditField("user.password",new PasswordField()),
            new EditField("user.manager",new SelectButton<User>("user.null",l->new UserSelectView(api,l),u->u.name))
        };
    }

    @Override
    protected User applyEdits(EditField[] f) {
        String id=((TextField)f[0].node).getText();
        String name=((TextField)f[1].node).getText();
        
        if(id.isEmpty())
            id=UIUtils.randomId();
        if(name.isEmpty())
            name=id;
        
        return new User(
            id,name,
            ((PasswordField)f[2].node).getText(),
            ((SelectButton<User>)f[3].node).getSelected()
        );
    }

    @Override
    protected void save(User original, User modified) throws FormattedException {
        if(original!=null&&!original.id.equals(modified.id))
            Localization.CURRENT.formatException("user.fail.move", ()->api.moveUser(original.id, modified.id));
        Localization.CURRENT.formatException(original==null?"user.fail.create":"user.fail.modify",
                ()->api.putUser(modified.id, modified.name,modified.password, modified.manager==null?null:modified.manager.id,original==null,original!=null));
    }
    
}
