/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package easyinventorydesktop.storage;

import easyinventoryapi.API;
import easyinventorydesktop.EditDialog;
import easyinventorydesktop.FormattedException;
import easyinventorydesktop.Localization;
import javafx.scene.control.Spinner;

/**
 *
 * @author 3041TAN-08
 */
public class StorageLimitDialog extends EditDialog<Integer> {

    private final String warehouse_id,storage_id,item_id;
    
    public StorageLimitDialog(API api,String warehouse_id,String storage_id,String item_id, Integer original) {
        super(api, original,"storage.limit");
        this.warehouse_id=warehouse_id;
        this.storage_id=storage_id;
        this.item_id=item_id;
    }

    @Override
    protected EditField[] createEditFields(Integer t) {
        return new EditField[]{
            new EditField("storage.limit",new Spinner<Integer>(0,Integer.MAX_VALUE,t)),
        };
    }

    @Override
    protected Integer applyEdits(EditField[] f) {
        return ((Spinner<Integer>)f[0].node).getValue();
    }

    @Override
    protected void save(Integer original, Integer modified) throws FormattedException {
        Localization.CURRENT.formatException("storage.fail.limit", ()->api.setStorageLimit(warehouse_id, storage_id, item_id, modified));
    }
    
}
