/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package easyinventorydesktop;

import easyinventoryapi.API;
import static easyinventorydesktop.UIUtils.createPadding;
import static easyinventorydesktop.UIUtils.createRightAside;
import java.util.function.Consumer;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 *
 * @author 3041TAN-08
 * @param <T>
 */
public abstract class SelectView<T> extends BasicView<T> {

    private final Consumer<T> select;
    
    private static VBox createContainer() {
        VBox p=new VBox();
        p.setSpacing(5);
        p.setStyle("-fx-padding: 5px;");
        return p;
    }
    
    public SelectView(API api, Consumer<T> select) {
        super(api,createContainer(), false,false);
        this.select=select;
        this.getStyleClass().add("select-view");
    }

    @Override
    protected Node createEntry(T t) {
        Button db=new Button();
        db.getStyleClass().add("select-button");
        db.setGraphic(new ImageView(new Image("resources/icons/tick.png")));
        db.setOnAction(_e->{
           select.accept(t);
        });
        
        Node node=createPadding(
            new Insets(10,10,10,10),
            createRightAside(
                showInfo(t),
                createPadding(
                    new Insets(0,0,0,10),
                    new HBox(db)
                )
            )
        );
        return node;
    }
    
    protected abstract Node showInfo(T t);
    
}
