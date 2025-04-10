/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package easyinventorydesktop;

import easyinventorydesktop.storage.StorageSelectView;
import easyinventorydesktop.warehouse.WarehouseSelectView;
import easyinventoryapi.API;
import easyinventoryapi.ItemStack;
import easyinventoryapi.Storage;
import easyinventoryapi.Warehouse;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/**
 *
 * @author 3041TAN-06
 */
public final class SearchView extends VBox {
    
    private final API api;
    private final ObservableList<ItemStack> results;
    private boolean searching=false;
    private final TableColumn<ItemStack,String> whcol,stcol,lotcol,manucol,serialcol;
    private final Button search_btn;
    private final Button prev_btn,next_btn;
    private int offset=0;
    private final int PAGE_SIZE=20;

    
    public SearchView(API api) {
        this.api=api;
        this.results=FXCollections.observableArrayList();
        
        
        SelectButton<Warehouse> warehouse_select=new SelectButton<>("warehouse.any",l->new WarehouseSelectView(api,l,true),w->w.name);
        warehouse_select.getStyleClass().add("warehouse-select");
        SelectButton<Storage> storage_select=new SelectButton<>("storage.any",l->{
            Warehouse wh=warehouse_select.getSelected();
            String id=null;
            if(wh!=null)
                id=wh.id;
            return new StorageSelectView(api,l,id,true);
        },w->w.name);
        storage_select.getStyleClass().add("storage-select");
        
        TextField query=new TextField();
        query.getStyleClass().add("search-query");
        CheckBox warehouse=new CheckBox();
        warehouse.getStyleClass().add("search-warehouse");
        CheckBox storage=new CheckBox();
        storage.getStyleClass().add("search-storage");
        CheckBox lot=new CheckBox();
        lot.getStyleClass().add("search-lot");
        CheckBox serial=new CheckBox();
        serial.getStyleClass().add("search-serial");
        
        Runnable update=()->searchLater(
            warehouse_select.getSelected()==null?null:warehouse_select.getSelected().id,
            storage_select.getSelected()==null?null:storage_select.getSelected().id,
            query.getText(),
            warehouse.isSelected(),
            storage.isSelected(),
            lot.isSelected(),
            serial.isSelected()
        );
        
        search_btn=new Button();
        search_btn.getStyleClass().add("search-button");
        search_btn.setGraphic(new ImageView(new Image("resources/icons/search.png")));
        search_btn.setOnAction((_e)->{
            offset=0;
            update.run();
        });
        
        whcol = new TableColumn(Localization.CURRENT.getLocalized("itemstack.warehouse"));
        whcol.setCellValueFactory(v->new ReadOnlyObjectWrapper<>(v.getValue().warehouse==null?"":v.getValue().warehouse.name));
        
        stcol = new TableColumn(Localization.CURRENT.getLocalized("itemstack.storage"));
        stcol.setCellValueFactory(v->new ReadOnlyObjectWrapper<>(v.getValue().storage==null?"":v.getValue().storage.name));
        
        TableColumn<ItemStack,String> typecol = new TableColumn(Localization.CURRENT.getLocalized("itemstack.type"));
        typecol.setCellValueFactory(v->new ReadOnlyObjectWrapper<>(v.getValue().item.name));
        
        lotcol = new TableColumn(Localization.CURRENT.getLocalized("itemstack.lot"));
        lotcol.setCellValueFactory(v->new ReadOnlyObjectWrapper<>(v.getValue().lot));
        
        serialcol = new TableColumn(Localization.CURRENT.getLocalized("itemstack.serial"));
        serialcol.setCellValueFactory(v->new ReadOnlyObjectWrapper<>(
                v.getValue().global_serial==0?"":""+v.getValue().global_serial
        ));
        
        manucol = new TableColumn(Localization.CURRENT.getLocalized("itemstack.manufacturer_serial"));
        manucol.setCellValueFactory(v->new ReadOnlyObjectWrapper<>(v.getValue().manufacturer_serial));
        
        TableColumn<ItemStack,String> amountcol = new TableColumn(Localization.CURRENT.getLocalized("itemstack.amount"));
        amountcol.setCellValueFactory(v->new ReadOnlyObjectWrapper<>(v.getValue().amount+" "+v.getValue().item.unit.name));
        
        TableColumn<ItemStack,String> availablecol = new TableColumn<>(Localization.CURRENT.getLocalized("itemstack.available"));
        availablecol.setCellValueFactory(v->new ReadOnlyObjectWrapper<>(v.getValue().available_amount+" "+v.getValue().item.unit.name));
        
        
        TableView<ItemStack> table=new TableView<>();
        table.getStyleClass().add("search-table");
        table.getColumns().addAll(whcol,stcol,typecol,lotcol,serialcol,manucol,amountcol,availablecol);
        table.setItems(results);
        
        HBox menu=new HBox(
            new Text(Localization.CURRENT.getLocalized("search.warehouse")),
            warehouse,
            new Text(Localization.CURRENT.getLocalized("search.storage")),
            storage,
            new Text(Localization.CURRENT.getLocalized("search.lot")),
            lot,
            new Text(Localization.CURRENT.getLocalized("search.serial")),
            serial,
            search_btn
        );
        menu.setAlignment(Pos.CENTER_LEFT);
        
        next_btn=new Button();
        next_btn.getStyleClass().add("next-button");
        next_btn.setGraphic(new ImageView(new Image("resources/icons/next.png")));
        next_btn.setOnAction((_e)->{
            offset+=PAGE_SIZE;
            update.run();
        });
        next_btn.setVisible(false);
        
        prev_btn=new Button();
        prev_btn.getStyleClass().add("prev-button");
        prev_btn.setGraphic(new ImageView(new Image("resources/icons/prev.png")));
        prev_btn.setOnAction((_e)->{
            offset=Math.max(0,offset-PAGE_SIZE);
            update.run();
        });
        prev_btn.setVisible(false);
        
        VBox settings=new VBox(query,menu);
        settings.setPadding(new Insets(0,5,0,5));
        
        this.getChildren().addAll(
                new HBox(
                    warehouse_select,
                    storage_select
                ),
                settings,
                table,
                new HBox(
                    prev_btn,
                    next_btn
                )
        );
    }
    
    protected void searchLater(String selected_warehouse,String selected_storage,String query,
            boolean qwarehouse,boolean storage,boolean lot,boolean serial) {
        if(!searching) {
            searching=true;
            results.clear();
            
            whcol.setVisible(qwarehouse);
            stcol.setVisible(storage);
            lotcol.setVisible(lot);
            serialcol.setVisible(serial);
            manucol.setVisible(serial);
            search_btn.setDisable(true);
            prev_btn.setVisible(offset>0);
            
            Worker.GLOBAL.addTask(()->{
                try{
                    ItemStack[] data=Localization.CURRENT.formatException("search.fail", ()->api.getCurrentItems(selected_warehouse,selected_storage, query, qwarehouse, storage, lot, serial,offset,PAGE_SIZE));
                    
                    Platform.runLater(()->{
                        next_btn.setVisible(data.length==PAGE_SIZE);
                        results.addAll(data);
                        searching=false;
                        search_btn.setDisable(false);
                    });
                }catch(FormattedException e) {
                    Platform.runLater(()->{
                        UIUtils.showError(e.getMessage());
                        searching=false;
                        search_btn.setDisable(false);
                    });
                }
            });
        }
    }
    
}
