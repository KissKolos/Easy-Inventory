package com.easyinventory.client.ui.search;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.easyinventory.client.R;

import java.util.ArrayList;

import easyinventoryapi.ItemStack;

public class ItemStackAdapter extends ArrayAdapter<ItemStack>  {

    public ItemStackAdapter(Context c) {
        super(c,0,new ArrayList<>());
    }

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ItemStack item=getItem(position);

        if(convertView==null) {
            convertView= LayoutInflater.from(getContext()).inflate(R.layout.itemstack_template,parent,false);
        }

        if(position==0)
            convertView.setBackgroundResource(R.drawable.card_bg_start);
        else if(position+1==getCount())
            convertView.setBackgroundResource(R.drawable.card_bg_end);
        else
            convertView.setBackgroundResource(R.drawable.card_bg);

        if(item!=null) {
            ((TextView)convertView.findViewById(R.id.warehouse)).setText(item.warehouse==null?"":item.warehouse.name);
            ((TextView)convertView.findViewById(R.id.storage)).setText(item.storage==null?"":item.storage.name);
            ((TextView)convertView.findViewById(R.id.lot)).setText(item.lot);
            ((TextView)convertView.findViewById(R.id.serial)).setText("" + item.global_serial);
            ((TextView)convertView.findViewById(R.id.amount)).setText(""+item.amount);
            ((TextView)convertView.findViewById(R.id.available)).setText(""+item.available_amount);
        }

        return convertView;
    }

}
