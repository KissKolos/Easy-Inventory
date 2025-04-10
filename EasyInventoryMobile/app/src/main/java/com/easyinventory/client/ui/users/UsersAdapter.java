package com.easyinventory.client.ui.users;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.easyinventory.client.R;
import com.easyinventory.client.ui.SelectableArrayAdapter;

import java.util.ArrayList;

import easyinventoryapi.User;

public class UsersAdapter extends SelectableArrayAdapter<User> {

    public UsersAdapter(Activity activity, ArrayList<User> list) {
        super(activity,list,R.layout.user_template);
    }

    @Override
    protected void initView(View convertView, User item) {
        ((TextView) convertView.findViewById(R.id.userid)).setText(item.id);
        ((TextView) convertView.findViewById(R.id.username)).setText(item.name);
        //((TextView) convertView.findViewById(R.id.manager)).setText(item.manager==null?"":item.manager.name);
    }
}
