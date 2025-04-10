package easyinventorydesktop.storage;

import easyinventoryapi.API;
import easyinventoryapi.Storage;
import easyinventorydesktop.FormattedException;
import easyinventorydesktop.LocalEditableView;
import easyinventorydesktop.Localization;
import easyinventorydesktop.UIUtils;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author 3041TAN-06
 */
public class StorageView extends LocalEditableView<Storage> {

    public StorageView(API api) {
        super(api,true);
    }

    @Override
    protected Storage[] reload(String q,int offset,int length,boolean archived) throws FormattedException {
        if(this.getWarehouse()!=null){
            return Localization.CURRENT.formatException("storage.fail.list", ()->api.getStorages(this.getWarehouse(),q,offset,length,archived));
        }else
            return new Storage[0];
    }

    @Override
    protected Node[] createActionButtons(Storage t) {
        Button b=new Button();
        b.getStyleClass().add("limit-button");
        b.setGraphic(new ImageView(new Image("resources/icons/limit.png")));
        b.setOnAction(_e->{
            UIUtils.showDialog("storage.limit", new StorageLimitView(api,t.warehouse.id,t.id));
        });
        
        Button b2=new Button();
        b2.getStyleClass().add("capacity-button");
        b2.setGraphic(new ImageView(new Image("resources/icons/capacity.png")));
        b2.setOnAction(_e->{
            UIUtils.showDialog("storage.capacity", new StorageCapacityView(api,t.warehouse.id,t.id));
        });
        return new Node[]{b,b2,new HBox(super.createActionButtons(t))};
    }
    
    @Override
    protected Node showInfo(Storage t) {
        Label name=new Label();
        name.getStyleClass().add("storage-name");
        name.setText(t.name);
        name.setStyle("-fx-font-weight: bold");

        Label id=new Label();
        id.getStyleClass().add("storage-id");
        id.setText(t.id);
        return new VBox(name,new HBox(10,id));
    }

    @Override
    protected void delete(Storage t) throws FormattedException {
        Localization.CURRENT.formatException("storage.fail.delete", ()->api.deleteStorage(this.getWarehouse(), t.id));
    }

    
    
    private void showLimitsDialog(String warehouse,String storage) {
        
        /*Dialog<ArrayList<Pair<String,Integer>>> dialog = new Dialog<>();
        dialog.setTitle(Localization.CURRENT.getLocalized("storage.limits"));
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        
        UIUtils.runOnWorker(()->{
            Item[] items=api.getItemsCached(false);
            StorageLimits limit=api.getStorageLimits(warehouse,storage);
            ItemLimit[] limits=new ItemLimit[items.length];
            
            for(int i=0;i<items.length;i++){
                int l=limit.getLimit(items[i].id);
                limits[i]=new ItemLimit(items[i].id,items[i].name,l,l);
            }
            
            return limits;
        }, res->{
            ObservableList<ItemLimit> limits=FXCollections.observableArrayList(res);
        
            TableColumn<ItemLimit,String> labelcol = new TableColumn<>(Localization.CURRENT.getLocalized("item.name"));
            labelcol.setCellValueFactory(v->new ReadOnlyObjectWrapper<>(v.getValue().name));
            
            TableColumn<ItemLimit,Integer> limitcol = new TableColumn<>(Localization.CURRENT.getLocalized("storage.limit.amount"));
            limitcol.setCellValueFactory(v->new ReadOnlyObjectWrapper<>(v.getValue().amount));
            limitcol.setCellFactory(TextFieldTableCell.<ItemLimit, Integer>forTableColumn(new IntegerStringConverter()));        
            limitcol.setOnEditCommit(v->{
                v.getRowValue().amount=v.getNewValue();
            });
            
            TableView<ItemLimit> table=new TableView<>();
            table.getColumns().addAll(labelcol,limitcol);
            table.setItems(limits);
            table.setEditable(true);
            
            ScrollPane scr=new ScrollPane(table);
            scr.fitToWidthProperty().setValue(true);
            dialog.getDialogPane().setContent(scr);
            dialog.getDialogPane().requestLayout();
            
            dialog.getDialogPane().getButtonTypes().clear();
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.APPLY, ButtonType.CANCEL);
            
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == ButtonType.APPLY) {
                    ArrayList<Pair<String,Integer>> changes=new ArrayList<>();
                    for(ItemLimit l:limits)
                        if(l.amount!=l.original_amount)
                            changes.add(new Pair(l.id,l.amount));
                    
                    return changes;
                }
                return null;
            });
            
        }, ()->{
            dialog.close();
        });

        dialog.getDialogPane().setContent(new Label(Localization.CURRENT.getLocalized("dialog.loading")));
        dialog.setResizable(true);
        dialog.setHeight(300);
        dialog.setWidth(300);
        dialog.getDialogPane().setMinSize(300, 300);
        
        Window window = dialog.getDialogPane().getScene().getWindow();
        Stage stage = (Stage) window;

        stage.setMinHeight(300);
        stage.setMinWidth(300);
        dialog.setResultConverter(_b ->null);
        dialog.showAndWait().ifPresent(changes->{
            UIUtils.runOnWorker(()->{
                for(Pair<String,Integer> ch:changes)
                    api.setStorageLimit(warehouse, storage, ch.getKey(), ch.getValue());
                return null;
            }, _r->{}, ()->{});
        });*/
    }

    @Override
    protected void showDialog(Storage original) {
        new StorageEditDialog(api,this.getWarehouse(),original).open();
    }

    @Override
    protected long getArchivalUnixtime(Storage t) {
        return t.deleted;
    }
    
    private static class ItemLimit {
        private final String id;
        private final String name;
        private int amount;
        private final int original_amount;

        public ItemLimit(String id, String name, int amount, int original_amount) {
            this.id = id;
            this.name = name;
            this.amount = amount;
            this.original_amount = original_amount;
        }
    }
    
    private void showCapacityDialog(String warehouse,String storage) {
        /*Dialog dialog = new Dialog();
        dialog.setTitle(Localization.CURRENT.getLocalized("storage.capacity"));
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        
        UIUtils.runOnWorker(()->{
            Item[] items=api.getItemsCached(false);
            StorageLimits cap=api.getStorageCapacity(warehouse,storage);
            StorageLimits limit=api.getStorageLimits(warehouse,storage);
            ItemCapacity[] limits=new ItemCapacity[items.length];
            
            for(int i=0;i<items.length;i++){
                int c=cap.getLimit(items[i].id);
                int l=limit.getLimit(items[i].id);
                limits[i]=new ItemCapacity(items[i].name,l-c,l);
            }
            
            return limits;
        }, res->{
            ObservableList<ItemCapacity> limits=FXCollections.observableArrayList(res);
        
            TableColumn<ItemCapacity,String> labelcol = new TableColumn<>(Localization.CURRENT.getLocalized("item.name"));
            labelcol.setCellValueFactory(v->new ReadOnlyObjectWrapper<>(v.getValue().name));
            
            TableColumn<ItemCapacity,Integer> limitcol = new TableColumn<>(Localization.CURRENT.getLocalized("storage.capacity.stored"));
            limitcol.setCellValueFactory(v->new ReadOnlyObjectWrapper<>(v.getValue().stored));
            
            TableColumn<ItemCapacity,Integer> capcol = new TableColumn<>(Localization.CURRENT.getLocalized("storage.capacity.max"));
            capcol.setCellValueFactory(v->new ReadOnlyObjectWrapper<>(v.getValue().capacity));
            
            TableColumn<ItemCapacity,String> usedcol = new TableColumn<>(Localization.CURRENT.getLocalized("storage.capacity.percent"));
            usedcol.setCellValueFactory(v->{
                if(v.getValue().capacity==0)
                    return new ReadOnlyObjectWrapper<>("");
                return new ReadOnlyObjectWrapper<>((v.getValue().stored*100/v.getValue().capacity)+"%");
            });
            
            TableView<ItemCapacity> table=new TableView<>();
            table.getColumns().addAll(labelcol,limitcol,capcol,usedcol);
            table.setItems(limits);
            
            ScrollPane scr=new ScrollPane(table);
            scr.fitToWidthProperty().setValue(true);
            dialog.getDialogPane().setContent(scr);
            dialog.getDialogPane().requestLayout();
        }, ()->{
            dialog.close();
        });

        dialog.getDialogPane().setContent(new Label(Localization.CURRENT.getLocalized("dialog.loading")));
        dialog.setResizable(true);
        dialog.setHeight(300);
        dialog.setWidth(300);
        dialog.getDialogPane().setMinSize(300, 300);
        
        Window window = dialog.getDialogPane().getScene().getWindow();
        Stage stage = (Stage) window;

        stage.setMinHeight(300);
        stage.setMinWidth(300);
        dialog.showAndWait();*/
    }
    
    private static class ItemCapacity {
        private final String name;
        private final int stored;
        private final int capacity;

        private ItemCapacity(String name, int stored, int capacity) {
            this.name = name;
            this.stored = stored;
            this.capacity = capacity;
        }
        
    }
    
}
