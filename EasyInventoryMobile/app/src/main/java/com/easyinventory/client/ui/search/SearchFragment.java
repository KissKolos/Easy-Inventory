package com.easyinventory.client.ui.search;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.easyinventory.client.ActivityUtils;
import com.easyinventory.client.LoggedInActivity;
import com.easyinventory.client.R;
import com.easyinventory.client.databinding.FragmentSearchBinding;
import com.easyinventory.client.ui.storages.StorageListActivity;
import com.easyinventory.client.ui.warehouses.WarehouseListActivity;

import easyinventoryapi.Storage;
import easyinventoryapi.Warehouse;

public class SearchFragment extends Fragment implements LoggedInActivity.ToolbarController {

    private String selected_warehouse_id;
    private String selected_storage_id;
    private FragmentSearchBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        ((LoggedInActivity)requireActivity()).setToolbarController(this);

        TextView wh_selected=root.findViewById(R.id.selected_warehouse);
        ImageButton wh_select=root.findViewById(R.id.select_warehouse);
        wh_selected.setText(R.string.warehouse_any);

        TextView st_selected=root.findViewById(R.id.selected_storage);
        ImageButton st_select=root.findViewById(R.id.select_storage);
        st_selected.setText(R.string.storage_any);

        ActivityResultLauncher<Intent> launcher= ActivityUtils.createLauncher(this, d->{
            if(d==null) {
                wh_selected.setText(R.string.warehouse_any);
                selected_warehouse_id=null;
            }else{
                Warehouse w = new Warehouse(d);
                wh_selected.setText(w.name);
                selected_warehouse_id=w.id;
            }

            st_selected.setText(R.string.storage_any);
            selected_storage_id=null;
        },true);

        wh_select.setOnClickListener(e->{
            Intent i=new Intent(this.getContext(), WarehouseListActivity.class);
            i.putExtra("can_select_null",true);
            launcher.launch(i);
        });

        ActivityResultLauncher<Intent> launcher2= ActivityUtils.createLauncher(this, d->{
            if(d==null) {
                st_selected.setText(R.string.storage_any);
                selected_storage_id=null;
            }else {
                Storage s = new Storage(d);
                st_selected.setText(s.name);
                selected_storage_id = s.id;
            }
        },true);

        st_select.setOnClickListener(e->{
            if(selected_warehouse_id!=null) {
                Intent i = new Intent(getContext(), StorageListActivity.class);
                i.putExtra("can_select_null", true);
                i.putExtra("warehouse_id", selected_warehouse_id);
                launcher2.launch(i);
            }
        });


        TextView query=root.findViewById(R.id.query);
        CheckBox toggle_warehouse=root.findViewById(R.id.toggle_warehouse);
        CheckBox toggle_storage=root.findViewById(R.id.toggle_storage);
        CheckBox toggle_lot=root.findViewById(R.id.toggle_lot);
        CheckBox toggle_serial=root.findViewById(R.id.toggle_serial);

        root.findViewById(R.id.search_button).setOnClickListener(e->{
            Intent i = new Intent(this.getContext(), SearchActivity.class);
            i.putExtra("query",query.getText().toString());

            i.putExtra("warehouse_id",selected_warehouse_id);
            i.putExtra("storage_id",selected_storage_id);
            i.putExtra("warehouse",toggle_warehouse.isChecked());
            i.putExtra("storage",toggle_storage.isChecked());
            i.putExtra("lot",toggle_lot.isChecked());
            i.putExtra("serial",toggle_serial.isChecked());

            this.requireContext().startActivity(i);
        });

        return root;
    }

    @Override
    public void controlToolbar(TextView title, ImageButton warehouse_button, SearchView search_bar) {
        title.setText(R.string.tab_users);
        search_bar.setVisibility(View.GONE);
        warehouse_button.setVisibility(View.GONE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}