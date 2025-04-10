package com.easyinventory.client.ui.units;

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
import com.easyinventory.client.databinding.FragmentUnitsBinding;
import com.easyinventory.client.ui.BaseListFragment;
import com.easyinventory.client.ui.FormattedException;
import com.easyinventory.client.ui.UIUtils;

import java.util.ArrayList;

import easyinventoryapi.Unit;

public class UnitsFragment extends BaseListFragment<Unit> {

    private FragmentUnitsBinding binding;

    @Override
    protected Unit[] load(String query,int offset,int length,boolean archived) throws FormattedException {
        return UIUtils.formatException(()->MainActivity.api.getUnits(query,offset,length,archived),getResources(),"unit_fail_list");
    }

    @Override
    protected void delete(String id) throws FormattedException {
        UIUtils.formatException(()->MainActivity.api.deleteUnit(id),getResources(),"unit_fail_delete");
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentUnitsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        initRoot(root,new UnitAdapter(this.getActivity(),new ArrayList<>()), w->w.id, UnitEditActivity.class);

        return root;
    }

    @Override
    public void controlToolbar(TextView title, ImageButton warehouse_button, SearchView search_bar) {
        super.controlToolbar(title, warehouse_button, search_bar);
        title.setText(R.string.tab_units);
        warehouse_button.setVisibility(View.GONE);
        search_bar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}