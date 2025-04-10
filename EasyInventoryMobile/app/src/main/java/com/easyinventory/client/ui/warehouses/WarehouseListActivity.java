package com.easyinventory.client.ui.warehouses;

import com.easyinventory.client.MainActivity;
import com.easyinventory.client.R;
import com.easyinventory.client.ui.BaseListActivity;
import com.easyinventory.client.ui.FormattedException;
import com.easyinventory.client.ui.UIUtils;

import java.util.ArrayList;

import easyinventoryapi.Warehouse;

public class WarehouseListActivity extends BaseListActivity<Warehouse> {

    public WarehouseListActivity() {
        super(a->new WarehouseAdapter(a,new ArrayList<>()));
    }

    @Override
    protected Warehouse getNullDisplay() {
        return new Warehouse("",this.getResources().getString(R.string.warehouse_null),"");
    }

    @Override
    protected Warehouse[] load(String query,int offset,int length,boolean archived) throws FormattedException {
        return UIUtils.formatException(()->MainActivity.api.getWarehouses(query,offset,length,archived),getResources(),"warehouse_fail_list");
    }
}
