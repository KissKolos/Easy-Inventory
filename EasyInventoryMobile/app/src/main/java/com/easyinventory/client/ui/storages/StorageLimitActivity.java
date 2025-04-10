package com.easyinventory.client.ui.storages;

import android.content.Intent;
import android.os.Bundle;

import com.easyinventory.client.ActivityUtils;
import com.easyinventory.client.MainActivity;
import com.easyinventory.client.R;
import com.easyinventory.client.ui.FormattedException;
import com.easyinventory.client.ui.PaginatedListActivity;
import com.easyinventory.client.ui.UIUtils;

import java.util.ArrayList;

import easyinventoryapi.Storage;
import easyinventoryapi.StorageLimit;

public class StorageLimitActivity extends PaginatedListActivity<StorageLimit> {

    private Storage storage;

    public StorageLimitActivity() {
        super(R.layout.activity_storage_limits, a->new StorageLimitAdapter(a,new ArrayList<>()));
    }

    @Override
    protected StorageLimit[] load(String query, int offset, int length, boolean archived) throws FormattedException {
        return UIUtils.formatException(()-> MainActivity.api.getStorageLimits(storage.warehouse.id,storage.id,query,offset,length),getResources(),"storage.fail.limit");
    }

    @Override
    protected void readExtraContext(Intent i) {
        super.readExtraContext(i);
        storage=ActivityUtils.readContext(i,Storage::new);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        findViewById(R.id.edit).setOnClickListener(e->{
            StorageLimit limit=adapter.getSelected();
            if(limit!=null) {
                Intent i = new Intent(this, StorageLimitEditActivity.class);
                i.putExtra("warehouse_id", storage.warehouse.id);
                i.putExtra("storage_id", storage.id);
                i.putExtra("item_id", limit.item.id);
                i.putExtra("limit", limit.amount);
                this.startActivity(i);
            }
        });
    }

}
