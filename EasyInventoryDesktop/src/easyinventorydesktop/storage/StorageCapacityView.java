/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package easyinventorydesktop.storage;

import easyinventoryapi.API;
import easyinventoryapi.StorageCapacity;
import easyinventorydesktop.BasicView;
import easyinventorydesktop.FormattedException;
import easyinventorydesktop.Localization;
import static easyinventorydesktop.UIUtils.createPadding;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

/**
 *
 * @author 3041TAN-08
 */
public class StorageCapacityView extends BasicView<StorageCapacity> {

    private final String warehouse_id;
    private final String storage_id;
    
    private static VBox createContainer() {
        VBox p=new VBox();
        p.setSpacing(5);
        p.setStyle("-fx-padding: 5px;");
        return p;
    }
    
    public StorageCapacityView(API api,String warehouse_id,String storage_id) {
        super(api, createContainer(), false,false);
        this.warehouse_id=warehouse_id;
        this.storage_id=storage_id;
        this.getStyleClass().add("storage-capacity-view");
    }

    @Override
    protected StorageCapacity[] reload(String q, int offset, int length, boolean archived) throws FormattedException {
        return Localization.CURRENT.formatException("storage.fail.capacity", ()->api.getStorageCapacity(warehouse_id, storage_id, q, offset, length));
    }

    @Override
    protected Node createEntry(StorageCapacity t) {
        Label l;
        
        if(t.limit==0) {
            l=new Label(t.stored_amount+"/"+t.limit);
            l.setTextFill(Color.color(1, 0, 0));
        }else{
            double r=Math.min(1,((double)t.stored_amount)/(double)t.limit);
            l=new Label(t.stored_amount+"/"+t.limit+" ("+((int)(r*100))+"%)");
            l.setTextFill(Color.color(r, 1-r, 0));
        }
        l.getStyleClass().add("item-capacity");
        
        Label name=new Label(t.item.name);
        name.getStyleClass().add("item-name");
        name.setMaxWidth(Integer.MAX_VALUE);
        HBox.setHgrow(name, Priority.ALWAYS);
        
        HBox data=new HBox(name,l);
        data.setAlignment(Pos.CENTER);
        
        return createPadding(
            new Insets(10,10,10,10),
            data
        );
    }
    
}
