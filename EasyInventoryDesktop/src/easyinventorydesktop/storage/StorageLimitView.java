/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package easyinventorydesktop.storage;

import easyinventoryapi.API;
import easyinventoryapi.StorageLimit;
import easyinventorydesktop.BasicView;
import easyinventorydesktop.FormattedException;
import easyinventorydesktop.Localization;
import static easyinventorydesktop.UIUtils.createPadding;
import static easyinventorydesktop.UIUtils.createRightAside;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 *
 * @author 3041TAN-08
 */
public class StorageLimitView extends BasicView<StorageLimit> {

    private final String warehouse_id;
    private final String storage_id;
    
    private static VBox createContainer() {
        VBox p=new VBox();
        p.setSpacing(5);
        p.setStyle("-fx-padding: 5px;");
        return p;
    }
    
    public StorageLimitView(API api,String warehouse_id,String storage_id) {
        super(api, createContainer(), false,false);
        this.warehouse_id=warehouse_id;
        this.storage_id=storage_id;
        this.getStyleClass().add("limit-view");
    }

    @Override
    protected StorageLimit[] reload(String q, int offset, int length, boolean archived) throws FormattedException {
        return Localization.CURRENT.formatException("storage.fail.limit", ()->api.getStorageLimits(warehouse_id, storage_id, q, offset, length));
    }

    @Override
    protected Node createEntry(StorageLimit t) {
        Button edit=new Button();
        edit.getStyleClass().add("edit-button");
        edit.setGraphic(new ImageView(new Image("resources/icons/edit.png")));
        edit.setOnAction((_e)->{
            new StorageLimitDialog(api,warehouse_id,storage_id,t.item.id,t.amount).open();
        });
        
        Label name=new Label(t.item.name);
        name.getStyleClass().add("item-name");
        
        Label amount=new Label(""+t.amount);
        amount.getStyleClass().add("item-amount");
        
        HBox editp=new HBox(edit);
        editp.setPadding(new Insets(0,0,0,5));
        
        HBox data=new HBox(name,amount,editp);
        data.setAlignment(Pos.CENTER);
        name.setMaxWidth(Integer.MAX_VALUE);
        HBox.setHgrow(name, Priority.ALWAYS);
        
        return createPadding(
            new Insets(10,10,10,10),
            data
        );
    }
    
}
