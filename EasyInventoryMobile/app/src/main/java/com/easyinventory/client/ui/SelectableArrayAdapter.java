package com.easyinventory.client.ui;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.easyinventory.client.R;

import java.util.ArrayList;

public abstract class SelectableArrayAdapter<T> extends ArrayAdapter<T> {

    private final int template;
    private final Activity activity;
    private int selected=0;
    public SelectableArrayAdapter(Activity activity, ArrayList<T> list,int template) {
        super(activity.getApplicationContext(),0,list);
        this.activity=activity;
        this.template=template;
    }

    public T getSelected() {
        if(selected<this.getCount())
            return this.getItem(selected);
        else
            return null;
    }

    protected abstract void initView(View convertView,T item);

    private void setBackground(View v,int attr) {
        UIUtils.setBackground(activity,v,attr);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        T item=getItem(position);

        if(convertView==null) {
            convertView= LayoutInflater.from(getContext()).inflate(template,parent,false);
        }

        if(item!=null) {
            if(position==selected){
                if(position==0&&this.getCount()==1){
                    setBackground(convertView,R.attr.card_bg_selected_start_end);
                }else if(position==0){
                    setBackground(convertView,R.attr.card_bg_selected_start);
                }else if(position+1==this.getCount()) {
                    setBackground(convertView,R.attr.card_bg_selected_end);
                }else{
                    setBackground(convertView,R.attr.card_bg_selected);
                }
            }else {
                if(position==0&&this.getCount()==1){
                    setBackground(convertView,R.attr.card_bg_start_end);
                }else if(position==0){
                    setBackground(convertView,R.attr.card_bg_start);
                }else if(position+1==this.getCount()) {
                    setBackground(convertView,R.attr.card_bg_end);
                }else{
                    setBackground(convertView,R.attr.card_bg);
                }
            }

            convertView.setOnClickListener(e->{
                selected=position;
                this.notifyDataSetChanged();
            });
            initView(convertView,item);
        }

        return convertView;
    }

}
