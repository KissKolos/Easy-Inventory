/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package easyinventorydesktop;

import java.util.function.Consumer;
import java.util.function.Function;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 *
 * @author 3041TAN-08
 * @param <T>
 */
public class SelectButton<T> extends Button {
        private Consumer<T> listener;
        private T selected;
        
        public SelectButton(String name_code,Function<Consumer<T>,SelectView<T>> s,Function<T,String> name_getter) {
            this(name_code,s,name_getter,null);
        }
        
        public SelectButton(String name_code,Function<Consumer<T>,SelectView<T>> s,Function<T,String> name_getter,T selected) {
            this.selected=selected;
            if(selected==null)
                this.setText(Localization.CURRENT.getLocalized(name_code));
            else
                this.setText(name_getter.apply(selected));
            this.setOnAction(_e->{
                Dialog dialog = new Dialog<>();
                ((Stage)dialog.getDialogPane().getScene().getWindow()).getIcons().add(new Image("resources/icons/logo.png"));
                
                dialog.setTitle(Localization.CURRENT.getLocalized(name_code));
                dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

                dialog.getDialogPane().setContent(s.apply(t->{
                    dialog.close();
                    if(t==null)
                        this.setText(Localization.CURRENT.getLocalized(name_code));
                    else
                        this.setText(name_getter.apply(t));
                    this.selected=t;
                    if(listener!=null)
                        listener.accept(t);
                }));
                dialog.setResizable(true);
                dialog.setHeight(300);
                dialog.setWidth(300);
                dialog.getDialogPane().setMinSize(300, 300);

                Window window = dialog.getDialogPane().getScene().getWindow();
                Stage stage = (Stage) window;

                stage.setMinHeight(300);
                stage.setMinWidth(300);
                dialog.show();
            });
        }
        
        public T getSelected() {
            return selected;
        }
        
        public void setOnSelect(Consumer<T> l) {
            listener=l;
        }
    }
