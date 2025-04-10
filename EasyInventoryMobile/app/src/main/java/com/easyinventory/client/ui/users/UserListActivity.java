package com.easyinventory.client.ui.users;

import com.easyinventory.client.MainActivity;
import com.easyinventory.client.R;
import com.easyinventory.client.ui.BaseListActivity;
import com.easyinventory.client.ui.FormattedException;
import com.easyinventory.client.ui.UIUtils;

import java.util.ArrayList;

import easyinventoryapi.User;

public class UserListActivity extends BaseListActivity<User> {

    public UserListActivity() {
        super(a->new UsersAdapter(a,new ArrayList<>()));
    }

    @Override
    protected User getNullDisplay() {
        return new User("",this.getResources().getString(R.string.user_null),"",null);
    }

    @Override
    protected User[] load(String query,int offset,int length,boolean archived) throws FormattedException {
        return UIUtils.formatException(()->MainActivity.api.getUsers(query,offset,length),getResources(),"user_fail_list");
    }
}
