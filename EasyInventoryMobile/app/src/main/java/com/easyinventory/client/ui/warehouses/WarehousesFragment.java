package com.easyinventory.client.ui.warehouses;

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
import com.easyinventory.client.databinding.FragmentWarehousesBinding;
import com.easyinventory.client.ui.BaseListFragment;
import com.easyinventory.client.ui.FormattedException;
import com.easyinventory.client.ui.UIUtils;

import java.util.ArrayList;

import easyinventoryapi.Warehouse;

public class WarehousesFragment extends BaseListFragment<Warehouse> {

    private FragmentWarehousesBinding binding;

    @Override
    protected Warehouse[] load(String query,int offset,int length,boolean archived) throws FormattedException {
        return UIUtils.formatException(()->MainActivity.api.getWarehouses(query,offset,length,archived),getResources(),"warehouse_fail_list");
    }

    @Override
    protected void delete(String id) throws FormattedException {
        UIUtils.formatException(()->MainActivity.api.deleteWarehouse(id),getResources(),"warehouse_fail_delete");
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentWarehousesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        initRoot(root,new WarehouseAdapter(this.getActivity(),new ArrayList<>()),w->w.id,WarehouseEditActivity.class);

        return root;
    }

    @Override
    public void controlToolbar(TextView title, ImageButton warehouse_button, SearchView search_bar) {
        super.controlToolbar(title, warehouse_button, search_bar);
        title.setText(R.string.tab_warehouses);
        warehouse_button.setVisibility(View.GONE);
        search_bar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}