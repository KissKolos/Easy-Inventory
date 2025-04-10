package com.easyinventory.client.ui.search;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.easyinventory.client.MainActivity;
import com.easyinventory.client.R;
import com.easyinventory.client.ui.UIUtils;

import java.util.Objects;

import easyinventoryapi.ItemStack;

public class SearchActivity extends AppCompatActivity {

    private static final int PAGE_SIZE=30;
    private int offset=0;

    private void createHeader(TableLayout table,boolean q_warehouse,boolean q_storage,boolean lot,boolean serial) {
        View convertView= LayoutInflater.from(getApplicationContext()).inflate(R.layout.itemstack_template,table,false);

        UIUtils.setBackground(this,convertView,R.attr.card_bg_start);

        if(q_warehouse)
            ((TextView)convertView.findViewById(R.id.warehouse)).setText(R.string.itemstack_warehouse);
        else
            convertView.findViewById(R.id.warehouse).setVisibility(View.GONE);

        if(q_storage)
            ((TextView)convertView.findViewById(R.id.storage)).setText(R.string.itemstack_storage);
        else
            convertView.findViewById(R.id.storage).setVisibility(View.GONE);

        ((TextView)convertView.findViewById(R.id.item)).setText(R.string.itemstack_type);
        if(lot)
            ((TextView)convertView.findViewById(R.id.lot)).setText(R.string.itemstack_lot);
        else
            convertView.findViewById(R.id.lot).setVisibility(View.GONE);

        if(serial) {
            ((TextView)convertView.findViewById(R.id.serial)).setText(R.string.itemstack_serial);
        }else
            convertView.findViewById(R.id.serial).setVisibility(View.GONE);

        ((TextView)convertView.findViewById(R.id.amount)).setText(R.string.itemstack_amount);
        ((TextView)convertView.findViewById(R.id.available)).setText(R.string.itemstack_available);
        table.addView(convertView);
    }

    @SuppressLint("SetTextI18n")
    private void createRow(TableLayout table,ItemStack item,int position,int count,boolean q_warehouse,boolean q_storage,boolean lot,boolean serial) {
        View convertView= LayoutInflater.from(getApplicationContext()).inflate(R.layout.itemstack_template,table,false);

        if(position+1==count)
            UIUtils.setBackground(this,convertView,R.attr.card_bg_end);
        else
            UIUtils.setBackground(this,convertView,R.attr.card_bg);

        if(q_warehouse)
            ((TextView)convertView.findViewById(R.id.warehouse)).setText(item.warehouse==null?"":item.warehouse.name);
        else
            convertView.findViewById(R.id.warehouse).setVisibility(View.GONE);

        if(q_storage)
            ((TextView)convertView.findViewById(R.id.storage)).setText(item.storage==null?"":item.storage.name);
        else
            convertView.findViewById(R.id.storage).setVisibility(View.GONE);

        ((TextView)convertView.findViewById(R.id.item)).setText(item.item.name);

        if(lot)
            ((TextView)convertView.findViewById(R.id.lot)).setText(item.lot);
        else
            convertView.findViewById(R.id.lot).setVisibility(View.GONE);

        if(serial) {
            if(item.global_serial!=0)
                ((TextView)convertView.findViewById(R.id.serial)).setText("" + item.global_serial);
        }else
            convertView.findViewById(R.id.serial).setVisibility(View.GONE);

        ((TextView)convertView.findViewById(R.id.amount)).setText(""+item.amount);
        ((TextView)convertView.findViewById(R.id.available)).setText(""+item.available_amount);
        table.addView(convertView);
    }

    private void reload() {
        findViewById(R.id.prev_button).setVisibility(offset>0?View.VISIBLE:View.INVISIBLE);
        Intent i=getIntent();

        String warehouse=i.getStringExtra("warehouse_id");
        String storage=i.getStringExtra("storage_id");
        String query=i.getStringExtra("query");
        boolean q_warehouse=i.getBooleanExtra("warehouse",false);
        boolean q_storage=i.getBooleanExtra("storage",false);
        boolean lot=i.getBooleanExtra("lot",false);
        boolean serial=i.getBooleanExtra("serial",false);

        TableLayout table=findViewById(R.id.table);

        UIUtils.waitForTask(this,this.getWindow().getDecorView(), ()->
                UIUtils.formatException(()->MainActivity.api.getCurrentItems(warehouse,storage,query,q_warehouse,q_storage,lot,serial,offset,PAGE_SIZE),getResources(),"search_fail")
        , r->{
            findViewById(R.id.next_button).setVisibility(r.length==PAGE_SIZE?View.VISIBLE:View.INVISIBLE);
            table.removeAllViews();
            createHeader(table,q_warehouse,q_storage,lot,serial);
            for(int ind=0;ind<r.length;ind++)
                createRow(table,r[ind],ind,r.length,q_warehouse,q_storage,lot,serial);
        }, this::finish);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(MainActivity.isStopped())
            this.finish();

        setContentView(R.layout.activity_search);

        setSupportActionBar(findViewById(R.id.toolbar));
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        findViewById(R.id.prev_button).setOnClickListener(e->{
            offset=Math.max(0,offset-PAGE_SIZE);
            reload();
        });

        findViewById(R.id.next_button).setOnClickListener(e->{
            offset+=PAGE_SIZE;
            reload();
        });

        reload();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
