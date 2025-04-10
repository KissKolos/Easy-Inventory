package com.easyinventory.client.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.easyinventory.client.MainActivity;
import com.easyinventory.client.R;

import java.util.Objects;
import java.util.function.Function;

public abstract class PaginatedListActivity<T> extends AppCompatActivity {
    public static final int PAGE_SIZE=20;
    protected int offset=0;

    private final int activity;
    private final Function<Activity,SelectableArrayAdapter<T>> adapter_factory;

    protected SearchView search;
    protected SelectableArrayAdapter<T> adapter;
    protected SwipeRefreshLayout sw;

    protected PaginatedListActivity(int activity,Function<Activity,SelectableArrayAdapter<T>> adapter_factory) {
        this.activity=activity;
        this.adapter_factory=adapter_factory;
    }

    protected void reload() {
        String query=search.getQuery().toString();
        findViewById(R.id.prev_button).setVisibility(offset>0?View.VISIBLE:View.INVISIBLE);

        UIUtils.waitForTaskBackground(this, ()->this.load(query,offset,PAGE_SIZE,false),r->{
            adapter.clear();
            adapter.addAll(r);
            sw.setRefreshing(false);
            findViewById(R.id.next_button).setVisibility(r.length==PAGE_SIZE?View.VISIBLE:View.INVISIBLE);
        },()-> sw.setRefreshing(false));
    }

    protected abstract T[] load(String query,int offset,int length,boolean archived) throws FormattedException;

    protected void readExtraContext(Intent i) {}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(MainActivity.isStopped())
            this.finish();

        setContentView(activity);

        findViewById(R.id.warehouseSelect).setVisibility(View.GONE);
        ((TextView)findViewById(R.id.title)).setText(this.getTitle());

        readExtraContext(this.getIntent());

        adapter=adapter_factory.apply(this);

        ListView list=findViewById(R.id.list);
        list.setAdapter(adapter);
        list.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);

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

        search=findViewById(R.id.search_bar);
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                offset=0;
                reload();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        sw=findViewById(R.id.swipe);
        sw.setOnRefreshListener(this::reload);
        reload();
    }

    @Override
    public boolean onSupportNavigateUp() {
        setResult(RESULT_CANCELED);
        finish();
        return true;
    }
}
