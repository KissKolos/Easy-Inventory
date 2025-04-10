package easyinventorydesktop.operation;

import easyinventoryapi.API;
import easyinventoryapi.Operation;
import easyinventoryapi.OperationItem;
import easyinventorydesktop.FormattedException;
import easyinventorydesktop.LocalEditableView;
import easyinventorydesktop.Localization;
import easyinventorydesktop.UIUtils;
import easyinventorydesktop.Worker;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;


/**
 *
 * @author 3041TAN-06
 */
public class OperationView extends LocalEditableView<Operation> {
    
    public OperationView(API api) {
        super(api,true);
    }

    @Override
    protected Operation[] reload(String q,int offset,int length,boolean archived) throws FormattedException {
        if(this.getWarehouse()!=null){
            return Localization.CURRENT.formatException("operation.fail.get", ()->api.getOperations(this.getWarehouse(),q,offset,length,archived));
        }else
            return new Operation[0];
    }

    @Override
    protected Node[] createActionButtons(Operation t) {
        final String wh=this.getWarehouse();
        Button finish=new Button();
        finish.getStyleClass().add("commit-button");
        finish.setGraphic(new ImageView(new Image("resources/icons/tick.png")));
        finish.setOnAction(_e->{
            UIUtils.showConfirmDialog("dialog.confirm_delete", ()->{
                Worker.GLOBAL.addTask(()->{
                    try{
                        Localization.CURRENT.formatException("operation.fail.commit", ()->api.finishOperation(wh, t.id));

                        Platform.runLater(()->{
                            this.reloadLater();
                        });
                    }catch(FormattedException e) {
                        Platform.runLater(()->{
                            UIUtils.showError(e.getMessage());
                        });
                    }
                }); 
            });
        });
        
        Button cancel=new Button();
        cancel.getStyleClass().add("cancel-button");
        cancel.setGraphic(new ImageView(new Image("resources/icons/cross.png")));
        cancel.setOnAction(_e->{
            UIUtils.showConfirmDialog("dialog.confirm_delete", ()->{
                Worker.GLOBAL.addTask(()->{
                    try{
                        Localization.CURRENT.formatException("operation.fail.cancel", ()->api.cancelOperation(wh, t.id));

                        Platform.runLater(()->{
                            this.reloadLater();
                        });
                    }catch(FormattedException e) {
                        Platform.runLater(()->{
                            UIUtils.showError(e.getMessage());
                        });
                    }
                });
            });
        });
        
        Button info=new Button();
        info.setGraphic(new ImageView(new Image("resources/icons/view.png")));
        info.getStyleClass().add("view-button");
        info.setOnAction(_e->{
            showInfoDialog(t);
        });
        
        return new Node[]{
            info,cancel,finish
        };
    }
    
    @Override
    protected Node showInfo(Operation t) {
        Label name=new Label();
        name.getStyleClass().add("operation-name");
        name.setText(t.name);
        name.setStyle("-fx-font-weight: bold");

        Label id=new Label();
        id.getStyleClass().add("operation-id");
        id.setText(t.id);
        
        Label type=new Label();
        type.getStyleClass().add("operation-type");
        type.setText(t.is_add?Localization.CURRENT.getLocalized("operation.type.insertion"):Localization.CURRENT.getLocalized("operation.type.removal"));
        
        return new VBox(name,new HBox(10,id,type));
    }

    @Override
    protected void delete(Operation t) throws FormattedException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    protected void showAddDialog() {
        String warehouse_id=this.getWarehouse();
        Dialog<Operation> dialog = new Dialog<>();
        dialog.setTitle(Localization.CURRENT.getLocalized("operation.item"));
        Stage s=(Stage)dialog.getDialogPane().getScene().getWindow();
        s.getIcons().add(new Image("resources/icons/logo.png"));
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField id_inp=new TextField();
        id_inp.getStyleClass().add("operation-id");
        TextField name_inp=new TextField();
        name_inp.getStyleClass().add("operation-name");
        CheckBox is_add=new CheckBox();
        is_add.getStyleClass().add("operation-isadd");
        
        grid.add(new Text(Localization.CURRENT.getLocalized("operation.id")), 0, 0);
        grid.add(id_inp, 1, 0);
        grid.add(new Text(Localization.CURRENT.getLocalized("operation.name")), 0, 1);
        grid.add(name_inp, 1, 1);
        grid.add(new Text(Localization.CURRENT.getLocalized("operation.is_add")), 0, 2);
        grid.add(is_add, 1, 2);
        
        ObservableList<OperationItem> items=FXCollections.observableArrayList();
        
        Button add_btn=new Button("+");
        add_btn.getStyleClass().add("operation-item-add");
        add_btn.setOnAction(_e->{
            new OperationItemDialog(api,warehouse_id,i->{
                items.add(i);
            }).open();
        });
        
        dialog.getDialogPane().setContent(new VBox(grid,createTable(items,true),add_btn));
        dialog.getDialogPane().getStyleClass().add("operation-dialog");
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                String id=id_inp.getText();
                String name=name_inp.getText();

                if(id.isEmpty())
                    id=UIUtils.randomId();
                if(name.isEmpty())
                    name=id;
                
                return new Operation(
                    id,
                    name,
                    is_add.isSelected(),
                    items.toArray(new OperationItem[0])
                );
            }
            return null;
        });
        
        dialog.showAndWait().ifPresent(modified->{
            UIUtils.runOnWorker("operation.fail.create", ()->api.putOperation(this.getWarehouse(), modified.id, modified.name, modified.is_add, modified.items), r->{}, ()->{});
        });
    }
    
    private void showInfoDialog(Operation t) {
        Dialog dialog = new Dialog<>();
        Stage s=(Stage)dialog.getDialogPane().getScene().getWindow();
        s.getIcons().add(new Image("resources/icons/logo.png"));
        dialog.setTitle(t.name);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        
        dialog.getDialogPane().setContent(createTable(FXCollections.observableArrayList(t.items),false));
        dialog.show();
    }
    
    private TableView<OperationItem> createTable(ObservableList<OperationItem> items,boolean can_remove) {
        TableColumn<OperationItem,String> stcol = new TableColumn(Localization.CURRENT.getLocalized("operation.item.storage"));
        stcol.setCellValueFactory(v->new ReadOnlyObjectWrapper<>(v.getValue().storage==null?"":v.getValue().storage.name));
        
        TableColumn<OperationItem,String> typecol = new TableColumn(Localization.CURRENT.getLocalized("operation.item.type"));
        typecol.setCellValueFactory(v->new ReadOnlyObjectWrapper<>(v.getValue().item.name));
        
        TableColumn<OperationItem,String> lotcol = new TableColumn(Localization.CURRENT.getLocalized("operation.item.lot"));
        lotcol.setCellValueFactory(v->new ReadOnlyObjectWrapper<>(v.getValue().lot));
        
        TableColumn<OperationItem,Integer> serialcol = new TableColumn(Localization.CURRENT.getLocalized("operation.item.serial"));
        serialcol.setCellValueFactory(v->new ReadOnlyObjectWrapper<>(v.getValue().global_serial));
        
        TableColumn<OperationItem,String> manucol = new TableColumn(Localization.CURRENT.getLocalized("operation.item.manufacturer_serial"));
        manucol.setCellValueFactory(v->new ReadOnlyObjectWrapper<>(v.getValue().manufacturer_serial));
        
        TableColumn<OperationItem,Integer> amountcol = new TableColumn(Localization.CURRENT.getLocalized("operation.item.amount"));
        amountcol.setCellValueFactory(v->new ReadOnlyObjectWrapper<>(v.getValue().amount));
        
        TableColumn<OperationItem,OperationItem> removecol = new TableColumn<>("");
        removecol.setCellValueFactory(v->new ReadOnlyObjectWrapper<>(v.getValue()));
        removecol.setCellFactory(_p -> new TableCell<OperationItem, OperationItem>() {
            private final Button deleteButton = new Button("-");

            @Override
            protected void updateItem(OperationItem item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null) {
                    setGraphic(null);
                    return;
                }

                setGraphic(deleteButton);
                deleteButton.setOnAction(
                    _e -> getTableView().getItems().remove(item)
                );
            }
        });
        
        TableView<OperationItem> table=new TableView<>();
        table.getColumns().addAll(stcol,typecol,lotcol,serialcol,manucol,amountcol);
        if(can_remove)
            table.getColumns().add(removecol);
        table.setItems(items);
        table.getStyleClass().add("operation-items-table");
        return table;
    }

    @Override
    protected void showDialog(Operation original) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected long getArchivalUnixtime(Operation t) {
        return t.deleted;
    }
}
