/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package easyinventorydesktop.unit;

import easyinventoryapi.API;
import easyinventoryapi.Unit;
import easyinventorydesktop.EditableView;
import easyinventorydesktop.FormattedException;
import easyinventorydesktop.Localization;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 *
 * @author 3041TAN-06
 */
public class UnitEditView extends EditableView<Unit> {

    public UnitEditView(API api) {
        super(api,true);
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

    @Override
    protected void delete(Unit t) throws FormattedException {
        Localization.CURRENT.formatException("unit.fail.delete", ()->api.deleteUnit(t.id));
    }

    @Override
    protected void showDialog(Unit original) {
        new UnitEditDialog(api,original).open();
    }

    @Override
    protected long getArchivalUnixtime(Unit t) {
        return t.deleted;
    }

    
    
}
