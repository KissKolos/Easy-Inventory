package com.easyinventory.client.ui.warehouses;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.easyinventory.client.R;
import com.easyinventory.client.ui.SelectableArrayAdapter;

import java.util.ArrayList;

import easyinventoryapi.Warehouse;

public class WarehouseAdapter extends SelectableArrayAdapter<Warehouse> {

    public WarehouseAdapter(Activity activity, ArrayList<Warehouse> list) {
        super(activity,list, R.layout.warehouse_template);
    }

    @Override
    protected void initView(View convertView, Warehouse item) {
        ((TextView) convertView.findViewById(R.id.id)).setText(item.id);
        ((TextView) convertView.findViewById(R.id.name)).setText(item.name);
        //((TextView) convertView.findViewById(R.id.address)).setText(item.address);
    }

}
