package com.easyinventory.client.ui;

import android.content.Intent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.Nullable;

import com.easyinventory.client.ActivityUtils;
import com.easyinventory.client.R;
import com.easyinventory.client.ui.warehouses.WarehouseListActivity;

import java.util.function.Function;

import easyinventoryapi.ToJSON;
import easyinventoryapi.Warehouse;

public abstract class LocalListFragment<T extends ToJSON> extends BaseListFragment<T> {

    private Warehouse selected_warehouse;
    private TextView title;
    private ActivityResultLauncher<Intent> launcher;
    @Override
    protected void addExtraContext(Intent i) {
        i.putExtra("warehouse_id",selected_warehouse.id);
    }

    @Override
    protected boolean canAdd() {
        return getSelectedWarehouse()!=null;
    }

    @Override
    protected void initRoot(View root, SelectableArrayAdapter<T> adapter, Function<T, String> id_getter, Class<?> edit_activity) {
        super.initRoot(root, adapter, id_getter, edit_activity);

        launcher= ActivityUtils.createLauncher(this,d->{
            selected_warehouse = new Warehouse(d);
            title.setText(selected_warehouse.name);
            reload();
        },false);
    }

    @Override
    public void controlToolbar(TextView title, ImageButton warehouse_button, SearchView search_bar) {
        super.controlToolbar(title, warehouse_button, search_bar);
        title.setText(R.string.warehouse_null);
        warehouse_button.setVisibility(View.VISIBLE);
        search_bar.setVisibility(View.VISIBLE);

        this.title=title;
        warehouse_button.setOnClickListener(e-> launcher.launch(new Intent(this.getContext(), WarehouseListActivity.class)));
    }

    public @Nullable Warehouse getSelectedWarehouse() {
        return selected_warehouse;
    }
}
