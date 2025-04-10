package com.easyinventory.client.ui.operations;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.easyinventory.client.R;
import com.easyinventory.client.ui.SelectableArrayAdapter;

import java.util.ArrayList;
import java.util.Locale;

import easyinventoryapi.OperationItem;

public class OperationItemAdapter extends SelectableArrayAdapter<OperationItem> {

    public OperationItemAdapter(Activity activity, ArrayList<OperationItem> list) {
        super(activity,list, R.layout.operation_item_template);
    }

    @Override
    protected void initView(View convertView, OperationItem item) {
        ((TextView) convertView.findViewById(R.id.item)).setText(item.item.name);
        if(item.storage==null)
            ((TextView) convertView.findViewById(R.id.storage)).setText(R.string.storage_null);
        else
            ((TextView) convertView.findViewById(R.id.storage)).setText(item.storage.name);
        ((TextView) convertView.findViewById(R.id.lot)).setText(item.lot);
        ((TextView) convertView.findViewById(R.id.serial)).setText(item.global_serial==0?"":""+item.global_serial);
        ((TextView) convertView.findViewById(R.id.manufacturer_serial)).setText(item.manufacturer_serial);
        ((TextView) convertView.findViewById(R.id.amount)).setText(String.format(Locale.getDefault(),"%d", item.amount));
    }

}
