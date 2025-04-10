package com.easyinventory.client.ui.items;

import com.easyinventory.client.MainActivity;
import com.easyinventory.client.R;
import com.easyinventory.client.ui.BaseListActivity;
import com.easyinventory.client.ui.FormattedException;
import com.easyinventory.client.ui.UIUtils;

import java.util.ArrayList;

import easyinventoryapi.Item;

public class ItemListActivity extends BaseListActivity<Item> {

    public ItemListActivity() {
        super(a->new ItemAdapter(a,new ArrayList<>()));
    }

    @Override
    protected Item getNullDisplay() {
        return new Item(this.getResources().getString(R.string.item_any),"",null);
    }

    @Override
    protected Item[] load(String query,int offset,int length,boolean archived) throws FormattedException {
        return UIUtils.formatException(()->MainActivity.api.getItems(query,offset,length,archived),getResources(),"item_fail_list");
    }
}
