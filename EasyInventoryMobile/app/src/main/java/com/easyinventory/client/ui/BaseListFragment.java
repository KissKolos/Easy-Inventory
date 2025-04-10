package com.easyinventory.client.ui;

import android.content.Intent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.easyinventory.client.LoggedInActivity;
import com.easyinventory.client.R;

import java.util.function.Function;

import easyinventoryapi.ToJSON;


public abstract class BaseListFragment<T extends ToJSON> extends Fragment implements LoggedInActivity.ToolbarController {

    private int offset=0;
    private SelectableArrayAdapter<T> adapter;
    private SwipeRefreshLayout sw;
    private SearchView search;
    private View prev_button,next_button;

    protected abstract T[] load(String query,int offset,int length,boolean archived) throws FormattedException;

    protected abstract void delete(String id) throws FormattedException;

    protected void reload() {
        prev_button.setVisibility(offset>0?View.VISIBLE:View.INVISIBLE);
        sw.setRefreshing(true);
        String query=search==null?"":search.getQuery().toString();
        UIUtils.waitForTaskBackground(this.getActivity(), ()->this.load(query,offset,BaseListActivity.PAGE_SIZE,false), r->{
            adapter.clear();
            adapter.addAll(r);
            next_button.setVisibility(r.length==BaseListActivity.PAGE_SIZE?View.VISIBLE:View.INVISIBLE);
            sw.setRefreshing(false);
        },()-> sw.setRefreshing(false));
    }

    protected void addExtraContext(Intent i) {}

    protected boolean canAdd() {
        return true;
    }

    protected void initRoot(View root, SelectableArrayAdapter<T> adapter, Function<T,String> id_getter,Class<?> edit_activity) {
        this.adapter=adapter;

        ListView list=root.findViewById(R.id.list);
        list.setAdapter(adapter);

        list.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);

        root.findViewById(R.id.add).setOnClickListener(e->{
            if(canAdd()) {
                startActivity(edit_activity);
            }
        });
        if(root.findViewById(R.id.edit)!=null)
            root.findViewById(R.id.edit).setOnClickListener(e-> startActivityForSelected(edit_activity));
        root.findViewById(R.id.delete).setOnClickListener(e->{
            T item=adapter.getSelected();
            if(item!=null) {
                UIUtils.showConfirmDialog(root, R.string.dialog_confirm_delete, R.string.dialog_add, R.string.dialog_cancel, () -> {
                    UIUtils.waitForTask(getActivity(), root, () -> {
                        delete(id_getter.apply(item));
                        return true;
                    }, r -> {
                        adapter.remove(item);
                    }, () -> {});
                }, () -> {});
            }
        });

        prev_button=root.findViewById(R.id.prev_button);
        prev_button.setOnClickListener(e->{
            offset=Math.max(0,offset-BaseListActivity.PAGE_SIZE);
            reload();
        });

        next_button=root.findViewById(R.id.next_button);
        next_button.setOnClickListener(e->{
            offset+=BaseListActivity.PAGE_SIZE;
            reload();
        });

        ((LoggedInActivity)requireActivity()).setToolbarController(this);

        sw=root.findViewById(R.id.swipe);
        sw.setOnRefreshListener(this::reload);
        reload();
    }

    @Override
    public void controlToolbar(TextView title, ImageButton warehouse_button, SearchView search_bar) {
        search=search_bar;
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
    }

    protected void startActivity(Class<?> activity) {
        Intent i = new Intent(this.getContext(), activity);
        addExtraContext(i);
        this.requireContext().startActivity(i);
    }

    protected void startActivityForSelected(Class<?> activity) {
        T item=adapter.getSelected();
        if(item!=null) {
            Intent i = new Intent(this.getContext(), activity);
            i.putExtra("value", item.toJSON().toString());
            addExtraContext(i);
            this.requireContext().startActivity(i);
        }
    }
}
