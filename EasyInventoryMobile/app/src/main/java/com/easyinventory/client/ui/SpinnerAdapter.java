package com.easyinventory.client.ui;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.function.Function;

public class SpinnerAdapter<T> extends ArrayAdapter<T> {

    private final Function<T,String> converter;

    public SpinnerAdapter(Context context, ArrayList<T> list, Function<T,String> converter) {
        super(context,0,list);
        this.converter=converter;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        T item=getItem(position);

        if(convertView==null) {
            convertView=new TextView(this.getContext());
        }

        if(item!=null) {
            ((TextView) convertView).setText(converter.apply(item));
        }

        return convertView;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getView(position, convertView, parent);
    }
}
