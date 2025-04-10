package com.easyinventory.client.ui.items;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.easyinventory.client.MainActivity;
import com.easyinventory.client.R;
import com.easyinventory.client.databinding.FragmentItemsBinding;
import com.easyinventory.client.ui.BaseListFragment;
import com.easyinventory.client.ui.FormattedException;
import com.easyinventory.client.ui.UIUtils;

import java.util.ArrayList;

import easyinventoryapi.Item;

public class ItemsFragment extends BaseListFragment<Item> {

    private FragmentItemsBinding binding;

    @Override
    protected Item[] load(String query,int offset,int length,boolean archived) throws FormattedException {
        return UIUtils.formatException(()->MainActivity.api.getItems(query,offset,length,archived),getResources(),"item_fail_list");
    }

    @Override
    protected void delete(String id) throws FormattedException {
        UIUtils.formatException(()->MainActivity.api.deleteItem(id),getResources(),"item_fail_delete");
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentItemsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        initRoot(root,new ItemAdapter(this.getActivity(),new ArrayList<>()), w->w.id, ItemEditActivity.class);

        return root;
    }

    @Override
    public void controlToolbar(TextView title, ImageButton warehouse_button, SearchView search_bar) {
        super.controlToolbar(title, warehouse_button, search_bar);
        title.setText(R.string.tab_items);
        warehouse_button.setVisibility(View.GONE);
        search_bar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}