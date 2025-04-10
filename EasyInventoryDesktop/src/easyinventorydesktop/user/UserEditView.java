/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package easyinventorydesktop.user;

import easyinventorydesktop.warehouse.WarehouseSelectView;
import easyinventoryapi.API;
import easyinventoryapi.User;
import easyinventorydesktop.EditableView;
import easyinventorydesktop.FormattedException;
import easyinventorydesktop.Localization;
import easyinventorydesktop.UIUtils;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 *
 * @author 3041TAN-06
 */
public class UserEditView extends EditableView<User> {
    
    public UserEditView(API api) {
        super(api,false);
    }

    @Override
    protected User[] reload(String q,int offset,int length,boolean archived) throws FormattedException {
        return Localization.CURRENT.formatException("user.fail.list", ()->api.getUsers(q,offset,length));
    }

    @Override
    protected Node[] createActionButtons(User t) {
        Button b=new Button();
        b.getStyleClass().add("auth-button");
        b.setGraphic(new ImageView(new Image("resources/icons/authorizations.png")));
        b.setOnAction(_e->{
            UIUtils.runOnWorker("user.fail.auth", ()->api.getSystemAuthorization(t.id), a->{
                new UserAuthorizationDialog(api,a,t.id,null).open();
            }, ()->{});
        });
        
        Button b2=new Button();
        b2.getStyleClass().add("local-auth-button");
        b2.setGraphic(new ImageView(new Image("resources/icons/local_authorizations.png")));
        b2.setOnAction(_e->{
            UIUtils.showDialog("warehouse.select", d->new WarehouseSelectView(api,w->{
                d.close();
                UIUtils.runOnWorker("user.fail.auth", ()->api.getLocalAuthorization(t.id,w.id), a->{
                    new UserAuthorizationDialog(api,a,t.id,w.id).open();
                }, ()->{});
            }));
        });
        return new Node[]{b,b2,new HBox(super.createActionButtons(t))};
    }
    
    @Override
    protected Node showInfo(User t) {
        Label name=new Label();
        name.getStyleClass().add("user-name");
        name.setText(t.name);
        name.setStyle("-fx-font-weight: bold");

        Label id=new Label();
        id.setTooltip(new Tooltip(t.id));
        id.getStyleClass().add("user-id");
        id.setText(t.id);

        Label manager=new Label();
        manager.getStyleClass().add("user-manager");
        manager.setText(t.manager==null?"":t.manager.name);
        manager.setTooltip(new Tooltip(t.manager==null?"":t.manager.name));
        
        return new VBox(name,new HBox(10,id,manager));
    }

    @Override
    protected void delete(User t) throws FormattedException {
        Localization.CURRENT.formatException("user.fail.delete", ()->api.deleteUser(t.id));
    }

    @Override
    protected void showDialog(User original) {
        new UserEditDialog(api,original).open();
    }

    @Override
    protected long getArchivalUnixtime(User t) {
        return Long.MAX_VALUE;
    }
    
}
