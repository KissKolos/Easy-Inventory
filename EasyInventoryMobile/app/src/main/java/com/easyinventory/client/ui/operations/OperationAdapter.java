package com.easyinventory.client.ui.operations;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.easyinventory.client.R;
import com.easyinventory.client.ui.SelectableArrayAdapter;

import java.util.ArrayList;

import easyinventoryapi.Operation;

public class OperationAdapter extends SelectableArrayAdapter<Operation> {

    public OperationAdapter(Activity activity, ArrayList<Operation> list) {
        super(activity,list, R.layout.operation_template);
    }

    @Override
    protected void initView(View convertView, Operation item) {
        ((TextView) convertView.findViewById(R.id.id)).setText(item.id);
        ((TextView) convertView.findViewById(R.id.name)).setText(item.name);
        ((TextView) convertView.findViewById(R.id.type)).setText(item.is_add?R.string.operation_type_insertion:R.string.operation_type_removal);
    }

}
