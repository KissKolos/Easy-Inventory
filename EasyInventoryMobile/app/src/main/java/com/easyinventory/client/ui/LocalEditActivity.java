package com.easyinventory.client.ui;

import android.content.Intent;

public abstract class LocalEditActivity<T> extends BaseEditActivity<T> {

    private String selected_warehouse;

    public LocalEditActivity(int layout) {
        super(layout);
    }

    @Override
    protected void readExtraContext(Intent i) {
        selected_warehouse=i.getStringExtra("warehouse_id");
    }

    public String getSelectedWarehouseId() {
        return selected_warehouse;
    }
}
