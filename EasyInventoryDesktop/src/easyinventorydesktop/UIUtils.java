/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package easyinventorydesktop;

import easyinventoryapi.APIException;
import java.io.IOException;
import java.util.function.Consumer;
import java.util.function.Function;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.StringConverter;
import org.json.JSONException;

/**
 *
 * @author 3041TAN-06
 */
public class UIUtils {
    
    
    
    public static void showError(FormattedException e) {
        showError(e.getMessage());
    }
    
    public static BorderPane createLeftRight(Node left,Node right) {
        BorderPane border = new BorderPane();
        border.setLeft(left);
        border.setRight(right);
        return border;
    }
    
    public static BorderPane createRightAside(Node content,Node right) {
        BorderPane border = new BorderPane();
        border.setCenter(content);
        border.setRight(right);
        return border;
    }
    
    public static Region createPadding(Insets padding,Region content) {
        content.setPadding(padding);
        return content;
    }
    
    public static void showError(String msg) {
        Alert alert = new Alert(AlertType.ERROR,msg);
        alert.getDialogPane().getStyleClass().add("alert-dialog");
        alert.show();
        //EasyInventoryDesktop.REGISTRY.registerError(alert);
    }
    
    public static void showDialog(String name_code,Node n) {
        showDialog(name_code,d->n);
    }
    
    public static void showDialog(String name_code,Function<Dialog,Node> n) {
        Dialog dialog = new Dialog<>();
        ((Stage)dialog.getDialogPane().getScene().getWindow()).getIcons().add(new Image("resources/icons/logo.png"));
        dialog.setTitle(Localization.CURRENT.getLocalized(name_code));
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        dialog.getDialogPane().getStyleClass().add("dialog");

        dialog.getDialogPane().setContent(n.apply(dialog));
        dialog.setResizable(true);
        dialog.setHeight(300);
        dialog.setWidth(300);
        dialog.getDialogPane().setMinSize(300, 300);

        Window window = dialog.getDialogPane().getScene().getWindow();
        Stage stage = (Stage) window;

        stage.setMinHeight(300);
        stage.setMinWidth(300);
        dialog.show();
    }
    
    public static <T> void runOnWorker(String failcode,APITask<T> t,Consumer<T> success,Runnable error) {
        Worker.GLOBAL.addTask(()->{
            try{
                T res=Localization.CURRENT.formatException(failcode, t);
                Platform.runLater(()->{
                    success.accept(res);
                });
            }catch(FormattedException e) {
                Platform.runLater(()->{
                    showError(e.getMessage());
                    error.run();
                });
            }
        });
        
    }
    
    public static String randomId() {
        return ""+(int)(Math.random() * 10000000);
    }
    
    public static void showConfirmDialog(String code,Runnable r) {
        Alert dialog = new Alert(AlertType.CONFIRMATION,Localization.CURRENT.getLocalized(code));
        ((Stage)dialog.getDialogPane().getScene().getWindow()).getIcons().add(new Image("resources/icons/logo.png"));
        dialog.getDialogPane().getStyleClass().add("confirm-dialog");
        dialog.showAndWait().ifPresent(b->{
            if(b==ButtonType.OK)
                r.run();
        });
    }
    
    public static interface APITask<T> {
        public T run() throws IOException,APIException,JSONException;
    }
    
    public static class NameIdPair {
        public final String name;
        public final String id;
        
        public NameIdPair(String name,String id) {
            this.name=name;
            this.id=id;
        }

        @Override
        public String toString() {
            return name;
        }
        
        public static <T> NameIdPair[] convert(T[] arr,Function<T,String> name,Function<T,String> id) {
            NameIdPair[] out=new NameIdPair[arr.length];
            for(int i=0;i<arr.length;i++)
                out[i]=new NameIdPair(name.apply(arr[i]),id.apply(arr[i]));
            return out;
        }
        
        public static <T> NameIdPair[] convertAddFirst(String first_name,T[] arr,Function<T,String> name,Function<T,String> id) {
            NameIdPair[] out=new NameIdPair[arr.length+1];
            out[0]=new NameIdPair(first_name,null);
            for(int i=0;i<arr.length;i++)
                out[i+1]=new NameIdPair(name.apply(arr[i]),id.apply(arr[i]));
            return out;
        }
    }
    
    public static class NonNegativeIntConverter extends StringConverter<Integer> {

        @Override
        public String toString(Integer object) {
            return object.toString();
        }

        @Override
        public Integer fromString(String string) {
            try{
                int v=Integer.parseInt(string);
                if(v<0)
                    return 0;
                return v;
            }catch(NumberFormatException _e) {
                return 0;
            }
        }
        
    }
    
}
