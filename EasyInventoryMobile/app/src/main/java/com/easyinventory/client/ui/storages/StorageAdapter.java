package com.easyinventory.client.ui.storages;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.easyinventory.client.R;
import com.easyinventory.client.ui.SelectableArrayAdapter;

import java.util.ArrayList;

import easyinventoryapi.Storage;

public class StorageAdapter extends SelectableArrayAdapter<Storage> {

    public StorageAdapter(Activity activity, ArrayList<Storage> list) {
        super(activity,list, R.layout.storage_template);
    }

    @Override
    protected void initView(View convertView, Storage item) {
        ((TextView) convertView.findViewById(R.id.id)).setText(item.id);
        ((TextView) convertView.findViewById(R.id.name)).setText(item.name);
    }

}
