package com.easyinventory.client.ui.units;

import com.easyinventory.client.MainActivity;
import com.easyinventory.client.R;
import com.easyinventory.client.ui.BaseListActivity;
import com.easyinventory.client.ui.FormattedException;
import com.easyinventory.client.ui.UIUtils;

import java.util.ArrayList;

import easyinventoryapi.Unit;

public class UnitListActivity extends BaseListActivity<Unit> {

    public UnitListActivity() {
        super(a->new UnitAdapter(a,new ArrayList<>()));
    }

    @Override
    protected Unit getNullDisplay() {
        return new Unit("",getResources().getString(R.string.unit_null));
    }

    @Override
    protected Unit[] load(String query,int offset,int length,boolean archived) throws FormattedException {
        return UIUtils.formatException(()->MainActivity.api.getUnits(query,offset,length,archived),getResources(),"unit_fail_list");
    }
}
