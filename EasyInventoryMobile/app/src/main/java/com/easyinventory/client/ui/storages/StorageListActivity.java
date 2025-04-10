package com.easyinventory.client.ui.storages;

import android.content.Intent;

import com.easyinventory.client.MainActivity;
import com.easyinventory.client.R;
import com.easyinventory.client.ui.BaseListActivity;
import com.easyinventory.client.ui.FormattedException;
import com.easyinventory.client.ui.UIUtils;

import java.util.ArrayList;

import easyinventoryapi.Storage;

public class StorageListActivity extends BaseListActivity<Storage> {

    private String warehouse_id;

    public StorageListActivity() {
        super(a->new StorageAdapter(a,new ArrayList<>()));
    }

    @Override
    protected void readExtraContext(Intent i) {
        warehouse_id=i.getStringExtra("warehouse_id");
    }

    @Override
    protected Storage getNullDisplay() {
        return new Storage(null,"",this.getResources().getString(R.string.storage_any));
    }

    @Override
    protected Storage[] load(String query,int offset,int length,boolean archived) throws FormattedException {
        return UIUtils.formatException(()->MainActivity.api.getStorages(warehouse_id,query,offset,length,archived),getResources(),"storage_fail_list");
    }
}
