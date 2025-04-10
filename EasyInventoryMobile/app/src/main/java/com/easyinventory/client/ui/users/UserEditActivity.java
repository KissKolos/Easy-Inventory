package com.easyinventory.client.ui.users;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
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

import easyinventoryapi.User;

public class UserEditActivity extends BaseEditActivity<User> {

    private User selected_manager=null;
    public UserEditActivity() {
        super(R.layout.activity_user_edit);
    }

    @Override
    protected User readContext(Intent i) {
        return ActivityUtils.readContext(i,User::new);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImageButton b=findViewById(R.id.editManager);
        TextView manager=findViewById(R.id.manager);
        ActivityResultLauncher<Intent> launcher=ActivityUtils.createLauncher(this,d->{
            if(d==null){
                selected_manager=null;
                manager.setText(R.string.user_null);
            }else{
                selected_manager=new User(d);
                manager.setText(selected_manager.name);
            }
        },true);
        b.setOnClickListener(e-> launcher.launch(new Intent(this.getApplicationContext(), UserListActivity.class)));
    }

    @Override
    protected void initUI(@Nullable User item) {
        if(item==null)
            item=new User("","",null,null);

        ((EditText)findViewById(R.id.editId)).setText(item.id);
        ((EditText)findViewById(R.id.editName)).setText(item.name);

        EditText password= findViewById(R.id.editPassword);
        CheckBox password_check=findViewById(R.id.editPasswordCheck);

        password.setEnabled(false);
        password_check.setChecked(false);

        password_check.setOnCheckedChangeListener((e,a)-> password.setEnabled(password_check.isChecked()));

        selected_manager=item.manager;

        TextView manager=findViewById(R.id.manager);

        if(selected_manager==null)
            manager.setText(R.string.user_null);
        else
            manager.setText(selected_manager.name);

    }

    @NonNull
    @Override
    protected User getItem() {
        EditText password= findViewById(R.id.editPassword);
        CheckBox password_check=findViewById(R.id.editPasswordCheck);
        String id=((EditText)findViewById(R.id.editId)).getText().toString();
        String name=((EditText)findViewById(R.id.editName)).getText().toString();

        if(id.isEmpty())
            id=UIUtils.randomId();
        if(name.isEmpty())
            name=id;

        return new User(
                id,name,
                password_check.isChecked()?password.getText().toString():"",
                selected_manager
        );
    }

    @Override
    protected void save(@Nullable User original, @NonNull User item) throws FormattedException {
        if(original!=null&&!original.id.equals(item.id))
            UIUtils.formatException(()->MainActivity.api.moveUser(original.id,item.id),getResources(),"user_fail_move");
        if(original==null||(!original.name.equals(item.name))||(!original.manager.equals(item.manager)))
            UIUtils.formatException(()->
                    MainActivity.api.putUser(item.id,item.name,item.password,item.manager==null?null:item.manager.id,original==null,original!=null),getResources(),
                    original==null?"user_fail_create":"user_fail_modify");
    }
}
