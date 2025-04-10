package com.easyinventory.client.ui.items;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.easyinventory.client.R;
import com.easyinventory.client.ui.SelectableArrayAdapter;

import java.util.ArrayList;

import easyinventoryapi.Item;

public class ItemAdapter extends SelectableArrayAdapter<Item> {

    public ItemAdapter(Activity activity, ArrayList<Item> list) {
        super(activity,list, R.layout.item_template);
    }

    @Override
    protected void initView(View convertView, Item item) {
        ((TextView) convertView.findViewById(R.id.id)).setText(item.id);
        ((TextView) convertView.findViewById(R.id.name)).setText(item.name);
        //((TextView) convertView.findViewById(R.id.unit)).setText(item.unit.name);
    }

}
