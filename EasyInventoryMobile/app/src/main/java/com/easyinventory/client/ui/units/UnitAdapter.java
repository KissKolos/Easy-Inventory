package com.easyinventory.client.ui.units;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.easyinventory.client.R;
import com.easyinventory.client.ui.SelectableArrayAdapter;

import java.util.ArrayList;

import easyinventoryapi.Unit;

public class UnitAdapter extends SelectableArrayAdapter<Unit> {

    public UnitAdapter(Activity activity, ArrayList<Unit> list) {
        super(activity,list, R.layout.unit_template);
    }

    @Override
    protected void initView(View convertView, Unit item) {
        ((TextView) convertView.findViewById(R.id.id)).setText(item.id);
        ((TextView) convertView.findViewById(R.id.name)).setText(item.name);
    }

}
