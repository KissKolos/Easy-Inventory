/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package easyinventorydesktop;

import easyinventoryapi.API;
import easyinventoryapi.Unit;
import java.util.function.Consumer;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 *
 * @author 3041TAN-06
 */
public class UnitSelectView extends SelectView<Unit> {

    public UnitSelectView(API api, Consumer<Unit> select) {
        super(api, select);
    }

    @Override
    protected Unit[] reload(String q,int offset,int length,boolean archived) throws FormattedException {
        return Localization.CURRENT.formatException("unit.fail.list", ()->api.getUnits(q,offset,length,archived));
    }

    @Override
    protected Node showInfo(Unit t) {
        Label name=new Label();
        name.getStyleClass().add("unit-name");
        name.setText(t.name);
        name.setStyle("-fx-font-weight: bold");

        Label id=new Label();
        id.getStyleClass().add("unit-id");
        id.setText(t.id);
        
        return new VBox(name,id);
    }
    
}
