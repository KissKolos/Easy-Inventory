/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package easyinventorydesktop;

import easyinventoryapi.API;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 *
 * @author 3041TAN-08
 * @param <T>
 */
public abstract class FormDialog<T> extends Dialog<T> {
    
    protected final T original;
    protected final API api;
    protected final String title_code;
    
    public FormDialog(API api,T original,String title_code) {
        this.api=api;
        this.original=original;
        this.title_code=title_code;
    }
    
    public void open() {
        setTitle(Localization.CURRENT.getLocalized(title_code));
        
        //ButtonType loginButtonType = new ButtonType(Localization.CURRENT.getLocalized("dialog.add"), ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        getDialogPane().getStyleClass().add("edit-dialog");
        Stage s=(Stage)getDialogPane().getScene().getWindow();
        s.getIcons().add(new Image("resources/icons/logo.png"));
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        EditField[] fs=this.createEditFields(original);
        for(int i=0;i<fs.length;i++) {
            grid.add(new Text(Localization.CURRENT.getLocalized(fs[i].name_code)), 0, i);
            grid.add(fs[i].node, 1, i);
            fs[i].node.getStyleClass().add("edit-field-"+i);
        }
        
        getDialogPane().setContent(grid);

        setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return this.applyEdits(fs);
            }
            return null;
        });
        
        showAndWait().ifPresent(modified->{
            done(modified);
        });
    }
    
    protected abstract void done(T modified);
    
    protected abstract EditField[] createEditFields(T t);
    
    protected abstract T applyEdits(EditField[] f);
    
    protected static class EditField {
        public final String name_code;
        public final Node node;
        
        public EditField(String name_code,Node node) {
            this.name_code=name_code;
            this.node=node;
        }
    }
    
}
