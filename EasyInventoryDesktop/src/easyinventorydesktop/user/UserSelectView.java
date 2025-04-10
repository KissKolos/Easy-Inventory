/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package easyinventorydesktop.user;

import easyinventoryapi.API;
import easyinventoryapi.User;
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
public class UserSelectView extends SelectView<User> {
    
    public UserSelectView(API api,Consumer<User> select) {
        super(api,select);
    }

    @Override
    protected User[] reload(String q,int offset,int length,boolean archived) throws FormattedException {
        return Localization.CURRENT.formatException("user.fail.list", ()->api.getUsers(q,offset,length));
    }
    
    @Override
    protected Node showInfo(User t) {
        Label name=new Label();
        name.getStyleClass().add("user-name");
        name.setText(t.name);
        name.setStyle("-fx-font-weight: bold");

        Label id=new Label();
        id.getStyleClass().add("user-id");
        id.setText(t.id);

        Label manager=new Label();
        manager.getStyleClass().add("user-manager");
        manager.setText(t.manager==null?"":t.manager.name);
        
        return new VBox(name,new HBox(10,id,manager));
    }
    
}
