/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package easyinventorydesktop.operation;

import easyinventorydesktop.item.ItemSelectView;
import easyinventorydesktop.storage.StorageSelectView;
import easyinventoryapi.API;
import easyinventoryapi.Item;
import easyinventoryapi.OperationItem;
import easyinventoryapi.Storage;
import easyinventorydesktop.FormDialog;
import easyinventorydesktop.SelectButton;
import java.util.function.Consumer;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;

/**
 *
 * @author 3041TAN-08
 */
public class OperationItemDialog extends FormDialog<OperationItem> {

    private final String warehouse_id;
    private final Consumer<OperationItem> callback;
    
    public OperationItemDialog(API api,String warehouse_id,Consumer<OperationItem> callback) {
        super(api, null,"operation.item");
        this.warehouse_id=warehouse_id;
        this.callback=callback;
    }

    @Override
    protected EditField[] createEditFields(OperationItem t) {
        TextField global_serial=new TextField();
        global_serial.textProperty().addListener((e,ov,nv)->{
            if(!nv.matches("\\d*"))
                global_serial.setText(nv.replaceAll("[^\\d]", ""));
        });
        
        TextField manufacturer_serial=new TextField();
        Spinner<Integer> amount=new Spinner<>(1,Integer.MAX_VALUE,1);
        
        CheckBox cb=new CheckBox();
        
        global_serial.setDisable(true);
        manufacturer_serial.setDisable(true);
        cb.setOnAction(e->{
            global_serial.setDisable(!cb.isSelected());
            manufacturer_serial.setDisable(!cb.isSelected());
            amount.setDisable(cb.isSelected());
            
            if(cb.isSelected()) {
                amount.getValueFactory().setValue(1);
            }else{
                global_serial.setText("");
                manufacturer_serial.setText("");
            }
        });
        
        return new EditField[]{
            new EditField("operation.item.storage",new SelectButton<Storage>("storage.any",f->new StorageSelectView(api,f,warehouse_id),s->s.name)),
            new EditField("operation.item.type",new SelectButton<Item>("item.null",f->new ItemSelectView(api,f),s->s.name)),
            new EditField("operation.item.lot",new TextField()),
            new EditField("operation.item.serial.enable",cb),
            new EditField("operation.item.global_serial",global_serial),
            new EditField("operation.item.manufacturer_serial",manufacturer_serial),
            new EditField("operation.item.amount",amount)
        };
    }

    @Override
    protected OperationItem applyEdits(EditField[] f) {
        String global_serial=((TextField)f[4].node).getText();
        boolean serial_enable=((CheckBox)f[3].node).isSelected();
        int global_serial_v=0;
        String mserial=null;
        
        if(serial_enable) {
            if(global_serial.isEmpty())
                global_serial_v=(int)(Math.random()*1000000);
            else
                global_serial_v=Integer.parseInt(global_serial);
            mserial=((TextField)f[5].node).getText();
        }
        
        return new OperationItem(
                ((SelectButton<Item>)f[1].node).getSelected(),
                ((Spinner<Integer>)f[6].node).getValue(),
                ((SelectButton<Storage>)f[0].node).getSelected(),
                global_serial_v,
                mserial,
                ((TextField)f[2].node).getText()
        );
    }

    @Override
    protected void done(OperationItem modified) {
        callback.accept(modified);
    }
    
}
