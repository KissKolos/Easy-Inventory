package com.easyinventory.client.ui.operations;

import android.content.Intent;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TableLayout;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.easyinventory.client.ActivityUtils;
import com.easyinventory.client.MainActivity;
import com.easyinventory.client.R;
import com.easyinventory.client.ui.FormattedException;
import com.easyinventory.client.ui.LocalEditActivity;
import com.easyinventory.client.ui.UIUtils;

import java.util.ArrayList;

import easyinventoryapi.Operation;
import easyinventoryapi.OperationItem;

public class OperationEditActivity extends LocalEditActivity<Operation> {

    private final ArrayList<OperationItem> items=new ArrayList<>();
    private TableLayout table;
    public OperationEditActivity() {
        super(R.layout.activity_operation_edit);
    }

    @Override
    protected Operation readContext(Intent i) {
        return null;
    }

    @Override
    protected void initUI(@Nullable Operation item) {
        table=findViewById(R.id.table);
        OperationViewActivity.createHeader(this,table);

        ActivityResultLauncher<Intent> launcher= ActivityUtils.createLauncher(this,d->{
            OperationItem oi=new OperationItem(d);
            items.add(oi);
            OperationViewActivity.createRow(this,table,oi,0,2);
        },false);

        findViewById(R.id.add).setOnClickListener(e->{
            Intent i=new Intent(this.getApplicationContext(), OperationItemActivity.class);
            i.putExtra("warehouse_id", getSelectedWarehouseId());
            launcher.launch(i);
        });
    }

    @NonNull
    @Override
    protected Operation getItem() {
        String id=((EditText)findViewById(R.id.editId)).getText().toString();
        String name=((EditText)findViewById(R.id.editName)).getText().toString();

        if(id.isEmpty())
            id=UIUtils.randomId();
        if(name.isEmpty())
            name=id;

        return new Operation(
                id, name,
                ((CheckBox)findViewById(R.id.is_add)).isChecked(),
                items.toArray(new OperationItem[0])
        );
    }

    @Override
    protected void save(@Nullable Operation original, @NonNull Operation item) throws FormattedException {
        UIUtils.formatException(()->MainActivity.api.putOperation(getSelectedWarehouseId(),item.id,item.name,item.is_add,item.items),getResources(),"operation_fail_create");
    }

}
