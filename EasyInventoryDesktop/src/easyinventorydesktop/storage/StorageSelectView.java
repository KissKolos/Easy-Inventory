package easyinventorydesktop.storage;

import easyinventoryapi.API;
import easyinventoryapi.Storage;
import easyinventorydesktop.FormattedException;
import easyinventorydesktop.Localization;
import easyinventorydesktop.SelectView;
import java.util.function.Consumer;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author 3041TAN-06
 */
public class StorageSelectView extends SelectView<Storage> {

    private final String warehouse;
    private final boolean allow_null;

    public StorageSelectView(API api, Consumer<Storage> select,String warehouse) {
        this(api,select,warehouse,false);
    }
    
    public StorageSelectView(API api, Consumer<Storage> select,String warehouse,boolean allow_null) {
        super(api, select);
        this.warehouse=warehouse;
        this.allow_null=allow_null;
    }

    @Override
    protected Storage[] reload(String q,int offset,int length,boolean archived) throws FormattedException {
        Storage[] st=Localization.CURRENT.formatException("storage.fail.list", ()->api.getStorages(warehouse,q,offset,length,archived));
        if(allow_null){
            Storage[] st2=new Storage[st.length+1];
            System.arraycopy(st, 0, st2, 1, st.length);
            return st2;
        }else
            return st;
    }
    
    @Override
    protected Node showInfo(Storage t) {
        Label name=new Label();
        name.getStyleClass().add("storage-name");
        name.setText(t==null?Localization.CURRENT.getLocalized("storage.any"):t.name);
        name.setStyle("-fx-font-weight: bold");

        Label id=new Label();
        id.getStyleClass().add("storage-id");
        id.setText(t==null?"":t.id);
        return new VBox(name,new HBox(10,id));
    }
    
}
