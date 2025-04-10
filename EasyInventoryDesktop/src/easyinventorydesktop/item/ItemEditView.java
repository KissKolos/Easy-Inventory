/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package easyinventorydesktop.item;

import easyinventoryapi.API;
import easyinventoryapi.Item;
import easyinventorydesktop.EditableView;
import easyinventorydesktop.FormattedException;
import easyinventorydesktop.Localization;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 *
 * @author 3041TAN-06
 */
public class ItemEditView extends EditableView<Item> {
    
    public ItemEditView(API api) {
        super(api,true);
    }

    @Override
    protected Item[] reload(String q,int offset,int length,boolean archived) throws FormattedException {
        return Localization.CURRENT.formatException("item.fail.get", ()->api.getItems(q,offset,length,archived));
    }

    @Override
    protected Node showInfo(Item t) {
        Label name=new Label();
        name.getStyleClass().add("item-name");
        name.setText(t.name);
        name.setStyle("-fx-font-weight: bold");

        Label id=new Label();
        id.getStyleClass().add("item-id");
        id.setText(t.id);

        Label unit=new Label();
        unit.getStyleClass().add("item-unit");
        unit.setText(t.unit.name);
        
        return new VBox(name,new HBox(10,id,unit));
    }

    @Override
    protected void delete(Item t) throws FormattedException {
        Localization.CURRENT.formatException("item.fail.delete", ()->api.deleteItem(t.id));
    }

    @Override
    protected void showDialog(Item original) {
        new ItemEditDialog(api,original).open();
    }

    @Override
    protected long getArchivalUnixtime(Item t) {
        return t.deleted;
    }
    
}
