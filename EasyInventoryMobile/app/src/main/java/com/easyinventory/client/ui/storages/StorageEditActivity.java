package com.easyinventory.client.ui.storages;

import android.content.Intent;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.easyinventory.client.ActivityUtils;
import com.easyinventory.client.MainActivity;
import com.easyinventory.client.R;
import com.easyinventory.client.ui.FormattedException;
import com.easyinventory.client.ui.LocalEditActivity;
import com.easyinventory.client.ui.UIUtils;

import easyinventoryapi.Storage;

public class StorageEditActivity extends LocalEditActivity<Storage> {

    public StorageEditActivity() {
        super(R.layout.activity_storage_edit);
    }

    @Override
    protected Storage readContext(Intent i) {
        return ActivityUtils.readContext(i,Storage::new);
    }

    @Override
    protected void initUI(@Nullable Storage item) {
        if(item==null)
            item=new Storage(null,"","");

        ((EditText)findViewById(R.id.editId)).setText(item.id);
        ((EditText)findViewById(R.id.editName)).setText(item.name);
    }

    @NonNull
    @Override
    protected Storage getItem() {
        String id=((EditText)findViewById(R.id.editId)).getText().toString();
        String name=((EditText)findViewById(R.id.editName)).getText().toString();

        if(id.isEmpty())
            id=UIUtils.randomId();
        if(name.isEmpty())
            name=id;

        return new Storage(
                null,
                id,name
        );
    }

    @Override
    protected void save(@Nullable Storage original, @NonNull Storage item) throws FormattedException {
        if(original!=null&&!original.id.equals(item.id))
            UIUtils.formatException(()->MainActivity.api.moveStorage(getSelectedWarehouseId(),original.id,item.id),getResources(),"storage_fail_move");
        if(original==null||(!original.name.equals(item.name)))
            UIUtils.formatException(()->
                    MainActivity.api.putStorage(getSelectedWarehouseId(),item.id,item.name,original==null,original!=null),getResources(),
                    original==null?"storage_fail_create":"storage_fail_modify");
    }

}
