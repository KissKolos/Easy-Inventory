package com.easyinventory.client.ui.storages;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.easyinventory.client.R;
import com.easyinventory.client.ui.SelectableArrayAdapter;

import java.util.ArrayList;

import easyinventoryapi.StorageLimit;

public class StorageLimitAdapter extends SelectableArrayAdapter<StorageLimit> {

    public StorageLimitAdapter(Activity activity, ArrayList<StorageLimit> list) {
        super(activity,list, R.layout.storage_limit_template);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void initView(View convertView, StorageLimit item) {
        ((TextView) convertView.findViewById(R.id.item)).setText(item.item.name);
        ((TextView) convertView.findViewById(R.id.limit)).setText(""+item.amount);
    }

}
