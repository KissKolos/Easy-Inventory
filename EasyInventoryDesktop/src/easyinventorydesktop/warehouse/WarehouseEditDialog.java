/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package easyinventorydesktop.warehouse;

import easyinventoryapi.API;
import easyinventoryapi.Warehouse;
import easyinventorydesktop.EditDialog;
import easyinventorydesktop.FormattedException;
import easyinventorydesktop.Localization;
import easyinventorydesktop.UIUtils;
import javafx.scene.control.TextField;

/**
 *
 * @author 3041TAN-08
 */
public class WarehouseEditDialog extends EditDialog<Warehouse> {

    public WarehouseEditDialog(API api, Warehouse original) {
        super(api, original,"warehouse.edit");
    }
    
    @Override
    protected EditField[] createEditFields(Warehouse t) {
        t=t==null?new Warehouse("","",""):t;
        
        return new EditField[]{
            new EditField("warehouse.id",new TextField(t.id)),
            new EditField("warehouse.name",new TextField(t.name)),
            new EditField("warehouse.address",new TextField(t.address))
        };
    }

    @Override
    protected Warehouse applyEdits(EditField[] f) {
        String id=((TextField)f[0].node).getText();
        String name=((TextField)f[1].node).getText();
        
        if(id.isEmpty())
            id=UIUtils.randomId();
        if(name.isEmpty())
            name=id;
        
        return new Warehouse(
            id,name,
            ((TextField)f[2].node).getText()
        );
    }

    @Override
    protected void save(Warehouse original, Warehouse modified) throws FormattedException {
        if(original!=null&&!original.id.equals(modified.id))
            Localization.CURRENT.formatException("warehouse.fail.move", ()->api.moveWarehouse(original.id, modified.id));
        Localization.CURRENT.formatException(original==null?"warehouse.fail.create":"warehouse.fail.modify",
                ()->api.putWarehouse(modified.id, modified.name, modified.address,original==null,original!=null));
    }
    
}
