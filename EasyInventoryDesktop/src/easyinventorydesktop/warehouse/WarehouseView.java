/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package easyinventorydesktop.warehouse;

import easyinventoryapi.API;
import easyinventoryapi.Warehouse;
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
public class WarehouseView extends EditableView<Warehouse> {

    public WarehouseView(API api) {
        super(api,true);
    }

    @Override
    protected Warehouse[] reload(String q,int offset,int length,boolean archived) throws FormattedException {
        return Localization.CURRENT.formatException("warehouse.fail.list", ()->api.getWarehouses(q,offset,length,archived));
    }
    
    @Override
    protected Node showInfo(Warehouse t) {
        Label name=new Label();
        name.getStyleClass().add("warehouse-name");
        name.setText(t.name);
        name.setStyle("-fx-font-weight: bold");

        Label id=new Label();
        id.getStyleClass().add("warehouse-id");
        id.setText(t.id);

        Label addr=new Label();
        addr.getStyleClass().add("warehouse-address");
        addr.setText(t.address);
        
        return new VBox(name,new HBox(10,id,addr));
    }

    @Override
    protected void delete(Warehouse t) throws FormattedException {
        Localization.CURRENT.formatException("warehouse.fail.delete", ()->api.deleteWarehouse(t.id));
    }

    @Override
    protected void showDialog(Warehouse original) {
        new WarehouseEditDialog(api,original).open();
    }

    @Override
    protected long getArchivalUnixtime(Warehouse t) {
        return t.deleted;
    }
    
}
