package com.easyinventory.client.ui.operations;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import com.easyinventory.client.ActivityUtils;
import com.easyinventory.client.MainActivity;
import com.easyinventory.client.R;
import com.easyinventory.client.ui.items.ItemListActivity;
import com.easyinventory.client.ui.storages.StorageListActivity;

import java.util.Objects;

import easyinventoryapi.Item;
import easyinventoryapi.OperationItem;
import easyinventoryapi.Storage;


public class OperationItemActivity extends AppCompatActivity {
    private Item item_type=null;
    private Storage storage=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(MainActivity.isStopped())
            this.finish();

        setContentView(R.layout.activity_operation_item_edit);
        setSupportActionBar(findViewById(R.id.toolbar));
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        String warehouse_id=getIntent().getStringExtra("warehouse_id");

        ImageButton type_btn=findViewById(R.id.select_type);
        TextView type_view=findViewById(R.id.selected_type);
        type_view.setText(R.string.item_null);

        ActivityResultLauncher<Intent> launcher= ActivityUtils.createLauncher(this,d->{
            item_type=new Item(d);
            type_view.setText(item_type.name);
        },false);

        type_btn.setOnClickListener(e-> launcher.launch(new Intent(this.getApplicationContext(), ItemListActivity.class)));

        ImageButton storage_btn=findViewById(R.id.select_storage);
        TextView storage_view=findViewById(R.id.selected_storage);
        storage_view.setText(R.string.storage_any);

        ActivityResultLauncher<Intent> launcher2=ActivityUtils.createLauncher(this,d->{
            if(d==null){
                storage=null;
                storage_view.setText(R.string.storage_any);
            }else {
                storage = new Storage(d);
                storage_view.setText(storage.name);
            }
        },true);

        storage_btn.setOnClickListener(e->{
            Intent i=new Intent(this.getApplicationContext(), StorageListActivity.class);
            i.putExtra("warehouse_id",warehouse_id);
            i.putExtra("can_select_null",true);
            launcher2.launch(i);
        });


        TextView m_serial=findViewById(R.id.manufacturer_serial);
        TextView serial=findViewById(R.id.serial);
        TextView amount=findViewById(R.id.amount);
        CheckBox serial_enable=findViewById(R.id.serial_enable);

        m_serial.setEnabled(false);
        serial.setEnabled(false);
        serial_enable.setOnCheckedChangeListener((e,v)->{
            m_serial.setEnabled(v);
            serial.setEnabled(v);
            amount.setEnabled(!v);
            if(v) {
                amount.setText("1");
            }else{
                m_serial.setText("");
                serial.setText("");
            }
        });

        findViewById(R.id.editConfirm).setOnClickListener(e -> {
            String lot=((TextView)findViewById(R.id.lot)).getText().toString().trim();
            String m_serial_v=m_serial.getText().toString().trim();

            if(item_type==null){
                setResult(RESULT_CANCELED);
            }else{
                Intent data=new Intent();
                int serial_v=0,amount_v=1;
                if(serial_enable.isChecked())
                    try {
                        serial_v=Integer.parseInt(serial.getText().toString());
                    }catch(NumberFormatException ignored) {
                        serial_v=(int)(Math.random()*1000000);
                    }
                try {
                    amount_v=Integer.parseInt(amount.getText().toString());
                }catch(NumberFormatException ignored) {}
                OperationItem item=new OperationItem(
                        item_type,
                        amount_v,
                        storage,
                        serial_v,
                        m_serial_v.isEmpty()?null:m_serial_v,
                        lot.isEmpty()?null:lot
                );
                data.putExtra("result",item.toJSON().toString());
                setResult(RESULT_OK,data);
            }
            this.finish();
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        setResult(RESULT_CANCELED);
        finish();
        return true;
    }
}
