package com.easyinventory.client.ui.operations;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.easyinventory.client.ActivityUtils;
import com.easyinventory.client.R;
import com.easyinventory.client.ui.UIUtils;

import java.util.Objects;

import easyinventoryapi.Operation;
import easyinventoryapi.OperationItem;

public class OperationViewActivity extends AppCompatActivity {

    public static void createHeader(Activity activity, TableLayout table) {
        View convertView= LayoutInflater.from(activity.getApplicationContext()).inflate(R.layout.operation_item_template,table,false);

        UIUtils.setBackground(activity,convertView,R.attr.card_bg_start);

        ((TextView)convertView.findViewById(R.id.item)).setText(R.string.operation_item_type);
        ((TextView)convertView.findViewById(R.id.storage)).setText(R.string.operation_item_storage);
        ((TextView)convertView.findViewById(R.id.lot)).setText(R.string.operation_item_lot);
        ((TextView)convertView.findViewById(R.id.manufacturer_serial)).setText(R.string.operation_item_manufacturer_serial);
        ((TextView)convertView.findViewById(R.id.serial)).setText(R.string.operation_item_serial);
        ((TextView)convertView.findViewById(R.id.amount)).setText(R.string.operation_item_amount);
        table.addView(convertView);
    }

    @SuppressLint("SetTextI18n")
    public static void createRow(Activity activity, TableLayout table, OperationItem item, int position, int count) {
        View convertView= LayoutInflater.from(activity.getApplicationContext()).inflate(R.layout.operation_item_template,table,false);

        if(position+1==count)
            UIUtils.setBackground(activity,convertView,R.attr.card_bg_end);
        else
            UIUtils.setBackground(activity,convertView,R.attr.card_bg);

        ((TextView)convertView.findViewById(R.id.item)).setText(item.item.name);
        ((TextView)convertView.findViewById(R.id.storage)).setText(item.storage==null?"":item.storage.name);
        ((TextView)convertView.findViewById(R.id.lot)).setText(item.lot);
        System.out.println(item.manufacturer_serial);
        ((TextView)convertView.findViewById(R.id.manufacturer_serial)).setText(item.manufacturer_serial);
        if(item.global_serial!=0)
            ((TextView)convertView.findViewById(R.id.serial)).setText("" + item.global_serial);
        ((TextView)convertView.findViewById(R.id.amount)).setText(""+item.amount);
        table.addView(convertView);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operation_view);

        setSupportActionBar(findViewById(R.id.toolbar));
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        Operation op=ActivityUtils.readContext(this.getIntent(), Operation::new);
        if(op==null) {
            finish();
            return;
        }

        TableLayout table=findViewById(R.id.table);
        createHeader(this,table);
        for(int i=0;i<op.items.length;i++)
            createRow(this,table,op.items[i], i,op.items.length);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

}
