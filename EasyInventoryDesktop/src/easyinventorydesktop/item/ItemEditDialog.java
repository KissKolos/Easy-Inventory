/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package easyinventorydesktop.item;

import easyinventoryapi.API;
import easyinventoryapi.Item;
import easyinventoryapi.Unit;
import easyinventorydesktop.EditDialog;
import easyinventorydesktop.FormattedException;
import easyinventorydesktop.Localization;
import easyinventorydesktop.SelectButton;
import easyinventorydesktop.UIUtils;
import easyinventorydesktop.UnitSelectView;
import javafx.scene.control.TextField;

/**
 *
 * @author 3041TAN-08
 */
public class ItemEditDialog extends EditDialog<Item> {

    public ItemEditDialog(API api, Item original) {
        super(api, original,"item.edit");
    }

    @Override
    protected EditField[] createEditFields(Item t) {
        t=t==null?new Item("","",null):t;
        
        return new EditField[]{
            new EditField("item.id",new TextField(t.id)),
            new EditField("item.name",new TextField(t.name)),
            new EditField("item.unit",new SelectButton<>("unit.null",l->new UnitSelectView(api,l),u->u.name,t.unit))
        };
    }

    @Override
    protected Item applyEdits(EditField[] f) {
        String id=((TextField)f[0].node).getText();
        String name=((TextField)f[1].node).getText();
        
        if(id.isEmpty())
            id=UIUtils.randomId();
        if(name.isEmpty())
            name=id;
        
        return new Item(
            id,
            name,
            ((SelectButton<Unit>)f[2].node).getSelected()
        );
    }

    @Override
    protected void save(Item original, Item modified) throws FormattedException {
        if(original!=null&&!original.id.equals(modified.id))
            Localization.CURRENT.formatException("item.fail.move", ()->api.moveItem(original.id, modified.id));
        Localization.CURRENT.formatException(original==null?"item.fail.create":"item.fail.modify",
                ()->api.putItem(modified.id, modified.name,modified.unit.id,original==null,original!=null));
    }
    
}
