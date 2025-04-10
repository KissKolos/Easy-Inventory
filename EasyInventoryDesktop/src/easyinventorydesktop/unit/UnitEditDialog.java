/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package easyinventorydesktop.unit;

import easyinventoryapi.API;
import easyinventoryapi.Unit;
import easyinventorydesktop.EditDialog;
import easyinventorydesktop.FormattedException;
import easyinventorydesktop.Localization;
import easyinventorydesktop.UIUtils;
import javafx.scene.control.TextField;

/**
 *
 * @author 3041TAN-08
 */
public class UnitEditDialog extends EditDialog<Unit> {

    public UnitEditDialog(API api, Unit original) {
        super(api, original,"unit.edit");
    }
    @Override
    protected EditField[] createEditFields(Unit t) {
        t=t==null?new Unit("",""):t;
        
        return new EditField[]{
            new EditField("unit.id",new TextField(t.id)),
            new EditField("unit.name",new TextField(t.name))
        };
    }

    @Override
    protected Unit applyEdits(EditField[] f) {
        String id=((TextField)f[0].node).getText();
        String name=((TextField)f[1].node).getText();
        
        if(id.isEmpty())
            id=UIUtils.randomId();
        if(name.isEmpty())
            name=id;
        
        return new Unit(id,name);
    }

    @Override
    protected void save(Unit original, Unit modified) throws FormattedException {
        if(original!=null&&!original.id.equals(modified.id))
            Localization.CURRENT.formatException("unit.fail.move", ()->api.moveUnit(original.id, modified.id));
        Localization.CURRENT.formatException(original==null?"unit.fail.create":"unit.fail.modify",
                ()->api.putUnit(modified.id, modified.name,original==null,original!=null));
    }
}
