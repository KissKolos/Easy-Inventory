/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package easyinventorydesktop;

import easyinventoryapi.API;
import static easyinventorydesktop.UIUtils.createPadding;
import static easyinventorydesktop.UIUtils.createRightAside;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;

/**
 *
 * @author 3041TAN-06
 * @param <T>
 */
public abstract class CardView<T> extends BasicView<T> {
        
    private static FlowPane createContainer() {
        FlowPane p=new FlowPane();
        p.setAlignment(Pos.CENTER);
        p.setPrefWidth(200);
        p.setVgap(5);
        p.setHgap(5);
        p.setStyle("-fx-padding: 5px;");
        return p;
    }
    
    public CardView(API api,boolean can_add,boolean has_archived) {
        super(api,createContainer(),can_add,has_archived);
    }
    
    protected abstract Node[] createActionButtons(T t);
    
    protected abstract long getArchivalUnixtime(T t);
    
    protected abstract Node showInfo(T t);
    
    @Override
    protected Node createEntry(T t) {        
        long time=getArchivalUnixtime(t);
        Region n;
        if(time==Long.MAX_VALUE) {
            n=new HBox(createActionButtons(t));
        }else{
            n=new Label(LocalDateTime.ofEpochSecond(time,0,ZoneOffset.UTC).toString());
        }
        
        Node node=createPadding(
            new Insets(10,10,10,10),
            createRightAside(
                showInfo(t),
                createPadding(
                    new Insets(0,0,0,10),
                    n
                )
            )
        );
        if(time==Long.MAX_VALUE)
            node.setStyle("-fx-background-color: white;-fx-effect: dropshadow( one-pass-box , black , 8 , 0.0 , 2 , 0 );-fx-max-width: 400px;-fx-pref-width: 400px");
        else
            node.setStyle("-fx-background-color: #aaa;-fx-effect: dropshadow( one-pass-box , black , 8 , 0.0 , 2 , 0 );-fx-max-width: 400px;-fx-pref-width: 400px");
        return node;
    }
    
}
