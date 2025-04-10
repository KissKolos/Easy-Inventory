/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package easyinventorydesktop;

import easyinventoryapi.API;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 *
 * @author 3041TAN-06
 * @param <T>
 */
public abstract class EditableView<T> extends CardView<T> {
        
    public EditableView(API api,boolean has_archived) {
        super(api,true,has_archived);
    }
    
    @Override
    protected Node[] createActionButtons(T t) {
        Button mb=new Button();
        mb.getStyleClass().add("edit-button");
        mb.setGraphic(new ImageView(new Image("resources/icons/edit.png")));
        mb.setOnAction(_e->{
            this.showDialog(t);
            this.reloadLater();
        });

        Button db=new Button();
        db.getStyleClass().add("delete-button");
        db.setGraphic(new ImageView(new Image("resources/icons/delete.png")));
        db.setOnAction(_e->{
            UIUtils.showConfirmDialog("dialog.confirm_delete", ()->{
                db.setDisable(true);
                Worker.GLOBAL.addTask(()->{
                    try{
                        this.delete(t);
                        Platform.runLater(()->this.reloadLater());
                    }catch (FormattedException ex) {
                        Platform.runLater(()->UIUtils.showError(ex));
                    }
                    Platform.runLater(()->db.setDisable(false));
                });
            });
        });
        
        return new Node[]{mb,db};
    }
    
    protected abstract void delete(T t) throws FormattedException;
    
    protected abstract void showDialog(T original);
    
    @Override
    protected void showAddDialog() {
        showDialog(null);
    }
    
}
