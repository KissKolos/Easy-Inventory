package com.easyinventory.client.ui.units;

import android.content.Intent;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.easyinventory.client.ActivityUtils;
import com.easyinventory.client.MainActivity;
import com.easyinventory.client.R;
import com.easyinventory.client.ui.BaseEditActivity;
import com.easyinventory.client.ui.FormattedException;
import com.easyinventory.client.ui.UIUtils;

import easyinventoryapi.Unit;

public class UnitEditActivity extends BaseEditActivity<Unit> {

    public UnitEditActivity() {
        super(R.layout.activity_unit_edit);
    }

    @Override
    protected Unit readContext(Intent i) {
        return ActivityUtils.readContext(i,Unit::new);
    }

    @Override
    protected void initUI(@Nullable Unit item) {
        if(item==null)
            item=new Unit("","");

        ((EditText)findViewById(R.id.editId)).setText(item.id);
        ((EditText)findViewById(R.id.editName)).setText(item.name);
    }

    @NonNull
    @Override
    protected Unit getItem() {
        String id=((EditText)findViewById(R.id.editId)).getText().toString();
        String name=((EditText)findViewById(R.id.editName)).getText().toString();

        if(id.isEmpty())
            id=UIUtils.randomId();
        if(name.isEmpty())
            name=id;

        return new Unit(id,name);
    }

    @Override
    protected void save(@Nullable Unit original, @NonNull Unit item) throws FormattedException {
        if(original!=null&&!original.id.equals(item.id))
            UIUtils.formatException(()->MainActivity.api.moveUnit(original.id,item.id),getResources(),"unit_fail_move");
        if(original==null||(!original.name.equals(item.name)))
            UIUtils.formatException(()->
                    MainActivity.api.putUnit(item.id,item.name,original==null,original!=null),getResources(),
                    original==null?"unit_fail_create":"unit_fail_modify");
    }


}
