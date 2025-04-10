/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package easyinventorydesktop;

import easyinventoryapi.API;
import easyinventorydesktop.PasswordChangeDialog.PasswordChange;
import javafx.scene.control.PasswordField;

/**
 *
 * @author 3041TAN-08
 */
public class PasswordChangeDialog extends EditDialog<PasswordChange> {

    public PasswordChangeDialog(API api) {
        super(api, null,"profile.password.change");
    }

    @Override
    protected void save(PasswordChange original, PasswordChange modified) throws FormattedException {
        if(!modified.new_password1.equals(modified.new_password2))
            throw new FormattedException(Localization.CURRENT.getLocalized("profile.password.fail.mismatch"));
        Localization.CURRENT.formatException("profile.password.fail", ()->{
            api.changePassword(modified.old_password, modified.new_password1);
            return true;
        });
    }

    @Override
    protected EditField[] createEditFields(PasswordChange t) {
        return new EditField[]{
            new EditField("profile.password.new",new PasswordField()),
            new EditField("profile.password.new.again",new PasswordField()),
            new EditField("profile.password.old",new PasswordField())
        };
    }

    @Override
    protected PasswordChange applyEdits(EditField[] f) {
        return new PasswordChange(
            ((PasswordField)f[0].node).getText(),
            ((PasswordField)f[1].node).getText(),
            ((PasswordField)f[2].node).getText()
        );
    }
    
    protected static class PasswordChange {
        private final String new_password1,new_password2,old_password;

        private PasswordChange(String new_password1,String new_password2,String old_password) {
            this.new_password1=new_password1;
            this.new_password2=new_password2;
            this.old_password=old_password;
        }
    }
    
}
