package com.easyinventory.client.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.easyinventory.client.MainActivity;
import com.easyinventory.client.R;

import java.util.function.Function;

import easyinventoryapi.ToJSON;

public abstract class BaseListActivity<T extends ToJSON> extends PaginatedListActivity<T> {

    private T null_replacement_item=null;

    protected BaseListActivity(Function<Activity,SelectableArrayAdapter<T>> adapter_factory) {
        super(R.layout.activity_list,adapter_factory);
    }

    protected abstract T getNullDisplay();

    @Override
    protected void reload() {
        String query=search.getQuery().toString();
        findViewById(R.id.prev_button).setVisibility(offset>0? View.VISIBLE:View.INVISIBLE);

        UIUtils.waitForTaskBackground(this, ()->this.load(query,offset,PAGE_SIZE,false),r->{
            adapter.clear();
            if(null_replacement_item!=null)
                adapter.add(null_replacement_item);
            adapter.addAll(r);
            findViewById(R.id.next_button).setVisibility(r.length==PAGE_SIZE?View.VISIBLE:View.INVISIBLE);
            sw.setRefreshing(false);
        },()-> sw.setRefreshing(false));
    }

    protected abstract T[] load(String query,int offset,int length,boolean archived) throws FormattedException;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(MainActivity.isStopped())
            this.finish();

        if(this.getIntent().getBooleanExtra("can_select_null",false))
            null_replacement_item=getNullDisplay();

        findViewById(R.id.selectButton).setOnClickListener(e->{
            if(null_replacement_item!=null&&null_replacement_item==adapter.getSelected()) {
                setResult(RESULT_OK, new Intent());
                finish();
            }else if(adapter.getSelected()!=null){
                Intent data=new Intent();
                data.putExtra("result",adapter.getSelected().toJSON().toString());
                setResult(RESULT_OK,data);
                finish();
            }
        });
    }
}
