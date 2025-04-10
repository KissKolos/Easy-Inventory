/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package easyinventorydesktop.warehouse;

import easyinventoryapi.API;
import easyinventoryapi.Warehouse;
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
 * @author 3041TAN-06
 */
public class WarehouseSelectView extends SelectView<Warehouse> {

    private final boolean allow_null;
    
    public WarehouseSelectView(API api, Consumer<Warehouse> select) {
        this(api, select,false);
    }
    
    public WarehouseSelectView(API api, Consumer<Warehouse> select,boolean allow_null) {
        super(api, select);
        this.allow_null=allow_null;
    }

    @Override
    protected Warehouse[] reload(String q,int offset,int length,boolean archived) throws FormattedException {
        Warehouse[] wh=Localization.CURRENT.formatException("warehouse.fail.list", ()->api.getWarehouses(q,offset,length,archived));
        if(allow_null){
            Warehouse[] wh2=new Warehouse[wh.length+1];
            System.arraycopy(wh, 0, wh2, 1, wh.length);
            return wh2;
        }else
            return wh;
    }
    
    @Override
    protected Node showInfo(Warehouse t) {
        Label name=new Label();
        name.getStyleClass().add("warehouse-name");
        name.setText(t==null?Localization.CURRENT.getLocalized("warehouse.any"):t.name);
        name.setStyle("-fx-font-weight: bold");

        Label id=new Label();
        id.getStyleClass().add("warehouse-id");
        id.setText(t==null?"":t.id);

        Label addr=new Label();
        addr.getStyleClass().add("warehouse-address");
        addr.setText(t==null?"":t.address);
        
        return new VBox(name,new HBox(10,id,addr));
    }
    
}
