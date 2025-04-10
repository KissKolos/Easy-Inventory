package com.easyinventory.client.ui.items;

import android.content.Intent;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.easyinventory.client.ActivityUtils;
import com.easyinventory.client.MainActivity;
import com.easyinventory.client.R;
import com.easyinventory.client.ui.BaseEditActivity;
import com.easyinventory.client.ui.FormattedException;
import com.easyinventory.client.ui.UIUtils;
import com.easyinventory.client.ui.units.UnitListActivity;

import easyinventoryapi.Item;
import easyinventoryapi.Unit;

public class ItemEditActivity extends BaseEditActivity<Item> {

    private Unit selected_unit=null;
    private final ActivityResultLauncher<Intent> launcher;

    public ItemEditActivity() {
        super(R.layout.activity_item_edit);
        launcher=ActivityUtils.createLauncher(this,d->{
            selected_unit=new Unit(d);
            ((TextView)findViewById(R.id.selected_unit)).setText(selected_unit.name);
        },false);
    }

    @Override
    protected Item readContext(Intent i) {
        return ActivityUtils.readContext(i, Item::new);
    }

    @Override
    protected void initUI(@Nullable Item item) {
        if(item==null)
            item=new Item("","",null);

        ((EditText)findViewById(R.id.editId)).setText(item.id);
        ((EditText)findViewById(R.id.editName)).setText(item.name);
        if(item.unit==null)
            ((TextView)findViewById(R.id.selected_unit)).setText(R.string.unit_null);
        else
            ((TextView)findViewById(R.id.selected_unit)).setText(item.unit.name);
        selected_unit=item.unit;

        findViewById(R.id.select_unit).setOnClickListener(e-> launcher.launch(new Intent(this.getApplicationContext(), UnitListActivity.class)));
    }

    @NonNull
    @Override
    protected Item getItem() {
        String id=((EditText)findViewById(R.id.editId)).getText().toString();
        String name=((EditText)findViewById(R.id.editName)).getText().toString();

        if(id.isEmpty())
            id=UIUtils.randomId();
        if(name.isEmpty())
            name=id;

        return new Item(
                id,name,
                selected_unit
        );
    }

    @Override
    protected void save(@Nullable Item original, @NonNull Item item) throws FormattedException {
        if(original!=null&&!original.id.equals(item.id))
            UIUtils.formatException(()->MainActivity.api.moveItem(original.id,item.id),getResources(),"item_fail_move");
        if(original==null||(!original.name.equals(item.name))||(!original.unit.equals(item.unit)))
            UIUtils.formatException(()->
                    MainActivity.api.putItem(item.id,item.name,item.unit.id,original==null,original!=null),getResources(),
                    original==null?"item_fail_create":"item_fail_modify");
    }


}
