/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package easyinventorydesktop;

import easyinventoryapi.API;

/**
 *
 * @author 3041TAN-08
 * @param <T>
 */
public abstract class EditDialog<T> extends FormDialog<T> {

    public EditDialog(API api, T original,String title_code) {
        super(api, original,title_code);
    }

    @Override
    protected void done(T modified) {
        try{
            save(original, modified);
        }catch (FormattedException ex) {
            UIUtils.showError(ex);
        }
    }
    
    protected abstract void save(T original,T modified) throws FormattedException;
    
}
