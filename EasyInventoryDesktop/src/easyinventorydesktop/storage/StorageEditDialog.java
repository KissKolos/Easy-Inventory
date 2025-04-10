/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package easyinventorydesktop.storage;

import easyinventoryapi.API;
import easyinventoryapi.Storage;
import easyinventorydesktop.EditDialog;
import easyinventorydesktop.FormattedException;
import easyinventorydesktop.Localization;
import easyinventorydesktop.UIUtils;
import javafx.scene.control.TextField;

/**
 *
 * @author 3041TAN-08
 */
public class StorageEditDialog extends EditDialog<Storage> {

    private final String warehouse_id;
    
    public StorageEditDialog(API api,String warehouse_id, Storage original) {
        super(api, original,"storage.edit");
        this.warehouse_id=warehouse_id;
    }
    
    @Override
    protected EditField[] createEditFields(Storage t) {
        t=t==null?new Storage(null,"",""):t;
        
        return new EditField[]{
            new EditField("storage.id",new TextField(t.id)),
            new EditField("storage.name",new TextField(t.name))
        };
    }

    @Override
    protected Storage applyEdits(EditField[] f) {
        String id=((TextField)f[0].node).getText();
        String name=((TextField)f[1].node).getText();
        
        if(id.isEmpty())
            id=UIUtils.randomId();
        if(name.isEmpty())
            name=id;
        
        return new Storage(
            null,
            id,
            name
        );
    }

    @Override
    protected void save(Storage original, Storage modified) throws FormattedException {
        if(original!=null&&!original.id.equals(modified.id))
            Localization.CURRENT.formatException("storage.fail.move", ()->api.moveStorage(warehouse_id,original.id, modified.id));
        Localization.CURRENT.formatException(original==null?"storage.fail.create":"storage.fail.modify",
                ()->api.putStorage(warehouse_id, modified.id, modified.name,original==null,original!=null));
    }
    
}
