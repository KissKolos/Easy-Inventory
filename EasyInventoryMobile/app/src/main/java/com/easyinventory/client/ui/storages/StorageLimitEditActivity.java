package com.easyinventory.client.ui.storages;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.easyinventory.client.MainActivity;
import com.easyinventory.client.R;
import com.easyinventory.client.ui.BaseEditActivity;
import com.easyinventory.client.ui.FormattedException;
import com.easyinventory.client.ui.UIUtils;

import java.util.Objects;

public class StorageLimitEditActivity extends BaseEditActivity<Integer> {

    private String warehouse_id;
    private String storage_id;
    private String item_id;

    public StorageLimitEditActivity() {
        super(R.layout.activity_storage_limit_edit);
    }

    @Override
    protected void readExtraContext(Intent i) {
        super.readExtraContext(i);
        warehouse_id=i.getStringExtra("warehouse_id");
        storage_id=i.getStringExtra("storage_id");
        item_id=i.getStringExtra("item_id");
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void initUI(@Nullable Integer item) {
        ((EditText)findViewById(R.id.limit)).setText(Objects.requireNonNull(item).toString());
    }

    @NonNull
    @Override
    protected Integer getItem() {
        String limit=((EditText)findViewById(R.id.limit)).getText().toString();
        try{
            return Integer.parseInt(limit);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    @Override
    protected void save(@Nullable Integer original, @NonNull Integer current) throws FormattedException {
        UIUtils.formatException(()-> MainActivity.api.setStorageLimit(warehouse_id,storage_id,item_id,current),getResources(),"storage.fail.limit");
    }

    @Override
    protected Integer readContext(Intent i) {
        return i.getIntExtra("limit",0);
    }
}
