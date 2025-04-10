package com.easyinventory.client.ui.warehouses;

import android.content.Intent;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.easyinventory.client.ActivityUtils;
import com.easyinventory.client.MainActivity;
import com.easyinventory.client.R;
import com.easyinventory.client.ui.BaseEditActivity;
import com.easyinventory.client.ui.FormattedException;
import com.easyinventory.client.ui.UIUtils;

import easyinventoryapi.Warehouse;

public class WarehouseEditActivity extends BaseEditActivity<Warehouse> {

    public WarehouseEditActivity() {
        super(R.layout.activity_warehouse_edit);
    }

    @Override
    protected Warehouse readContext(Intent i) {
        return ActivityUtils.readContext(i,Warehouse::new);
    }

    @Override
    protected void initUI(@Nullable Warehouse item) {
        if(item==null)
            item=new Warehouse("","","");

        ((EditText)findViewById(R.id.editId)).setText(item.id);
        ((EditText)findViewById(R.id.editName)).setText(item.name);
        ((EditText)findViewById(R.id.editAddress)).setText(item.address);
    }

    @NonNull
    @Override
    protected Warehouse getItem() {
        String id=((EditText)findViewById(R.id.editId)).getText().toString();
        String name=((EditText)findViewById(R.id.editName)).getText().toString();

        if(id.isEmpty())
            id=UIUtils.randomId();
        if(name.isEmpty())
            name=id;

        return new Warehouse(
                id,name,
                ((EditText)findViewById(R.id.editAddress)).getText().toString()
        );
    }

    @Override
    protected void save(@Nullable Warehouse original, @NonNull Warehouse item) throws FormattedException {
        if(original!=null&&!original.id.equals(item.id))
            UIUtils.formatException(()->MainActivity.api.moveWarehouse(original.id,item.id),getResources(),"warehouse_fail_move");
        if(original==null||(!original.name.equals(item.name)))
            UIUtils.formatException(()->
                    MainActivity.api.putWarehouse(item.id,item.name,item.address,original==null,original!=null),getResources(),
                    original==null?"warehouse_fail_create":"warehouse_fail_modify");
    }


}
