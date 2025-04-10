/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package easyinventorydesktop;

import easyinventorydesktop.warehouse.WarehouseSelectView;
import easyinventoryapi.API;
import easyinventoryapi.Warehouse;

/**
 *
 * @author 3041TAN-06
 * @param <T>
 */
public abstract class LocalEditableView<T> extends EditableView<T> {
    
    private final SelectButton<Warehouse> wh_select;
    
    public LocalEditableView(API api,boolean has_archived) {
        super(api,has_archived);
        
        wh_select=new SelectButton<>("selection.warehouse",l->new WarehouseSelectView(api,l),w->w.name);
        wh_select.getStyleClass().add("warehouse-select");
        wh_select.setOnSelect(_e->{
            this.reloadLater();
        });
        this.setTop(wh_select);
    }
    
    protected String getWarehouse() {
        Warehouse wh=wh_select.getSelected();
        return wh==null?null:wh.id;
    }
    
}
