/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package easyinventorydesktop.item;

import easyinventoryapi.API;
import easyinventoryapi.Item;
import easyinventorydesktop.FormattedException;
import easyinventorydesktop.Localization;
import easyinventorydesktop.SelectView;
import java.util.function.Consumer;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 *
 * @author 3041TAN-08
 */
public class ItemSelectView extends SelectView<Item> {

    public ItemSelectView(API api, Consumer<Item> select) {
        super(api, select);
    }

    @Override
    protected Node showInfo(Item t) {
        Label name=new Label();
        name.getStyleClass().add("item-name");
        name.setText(t==null?Localization.CURRENT.getLocalized("warehouse.any"):t.name);
        name.setStyle("-fx-font-weight: bold");

        Label id=new Label();
        id.getStyleClass().add("item-id");
        id.setText(t==null?"":t.id);

        Label unit=new Label();
        unit.getStyleClass().add("item-unit");
        unit.setText(t==null?"":t.unit.name);
        
        return new VBox(name,new HBox(10,id,unit));
    }

    @Override
    protected Item[] reload(String q, int offset, int length, boolean archived) throws FormattedException {
        return Localization.CURRENT.formatException("item.fail.list", ()->api.getItems(q,offset,length,archived));
    }
    
}
