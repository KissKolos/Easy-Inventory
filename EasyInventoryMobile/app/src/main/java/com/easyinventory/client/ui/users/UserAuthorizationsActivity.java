package com.easyinventory.client.ui.users;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.easyinventory.client.ActivityUtils;
import com.easyinventory.client.MainActivity;
import com.easyinventory.client.R;
import com.easyinventory.client.ui.FormattedException;
import com.easyinventory.client.ui.UIUtils;

import java.util.Objects;

import easyinventoryapi.Authorization;
import easyinventoryapi.User;

public class UserAuthorizationsActivity extends AppCompatActivity {

    private AuthorizationEntry[] entries;
    private User user;
    private String warehouse_id;

    private AuthorizationEntry[] convertToEntries(Authorization a) {
        AuthorizationEntry[] entries=new AuthorizationEntry[a.getAll().length];
        for(int i=0;i<entries.length;i++)
            entries[i]=new AuthorizationEntry(warehouse_id,a.getAll()[i],a.isGranted(a.getAll()[i]));
        return entries;
    }

    private AuthorizationEntry[] loadEntries() {
        return UIUtils.formatException(() ->{
            if(warehouse_id==null) {
                return convertToEntries(MainActivity.api.getSystemAuthorization(user.id));
            }else
                return convertToEntries(MainActivity.api.getLocalAuthorization(user.id,warehouse_id));
        }, getResources(), "user_fail_authorizations");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_authorizations);
        setSupportActionBar(findViewById(R.id.toolbar));
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        Intent data=getIntent();
        user= ActivityUtils.readContext(data, User::new);
        warehouse_id=data.getStringExtra("warehouse_id");

        UIUtils.waitForTask(this,this.getWindow().getDecorView().getRootView(),this::loadEntries,r->{
            entries=r;
            showEntries();
        }, ()->{
            setResult(RESULT_CANCELED);
            this.finish();
        });

        findViewById(R.id.editConfirm).setOnClickListener(e ->
            UIUtils.waitForTask(this,this.getWindow().getDecorView().getRootView(),()->{
                save();
                return true;
            },r->{
                setResult(RESULT_OK);
                this.finish();
            },()->{
                setResult(RESULT_CANCELED);
                this.finish();
            }));
    }

    private void showEntries() {
        LinearLayout l=findViewById(R.id.list);
        for(int i=0;i<entries.length;i++) {
            var entry=entries[i];
            LinearLayout view = (LinearLayout) getLayoutInflater().inflate(R.layout.authorization_template, l, false);
            if(i==0)
                UIUtils.setBackground(this,view,R.attr.card_bg_start);
            else if(i+1==entries.length)
                UIUtils.setBackground(this,view,R.attr.card_bg_end);
            else
                UIUtils.setBackground(this,view,R.attr.card_bg);

            if(warehouse_id==null)
                ((TextView)view.findViewById(R.id.name)).setText(UIUtils.loadString(getResources(),"authorization_system_" + entry.authorization));
            else
                ((TextView)view.findViewById(R.id.name)).setText(UIUtils.loadString(getResources(),"authorization_local_" + entry.authorization));
            ((SwitchCompat)view.findViewById(R.id.check)).setChecked(entry.granted);
            ((SwitchCompat)view.findViewById(R.id.check)).setOnCheckedChangeListener((_e,val)-> entry.new_granted=val);
            l.addView(view);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        setResult(RESULT_CANCELED);
        finish();
        return true;
    }

    protected void save() throws FormattedException {
        UIUtils.formatException(()->{
            for(AuthorizationEntry e:entries) {
                if(e.granted!=e.new_granted) {
                    if(e.warehouse_id==null) {
                        if(e.new_granted) {
                            MainActivity.api.grantSystemAuthorization(user.id,e.authorization);
                        }else{
                            MainActivity.api.revokeSystemAuthorization(user.id,e.authorization);
                        }
                    }else{
                        if(e.new_granted) {
                            MainActivity.api.grantLocalAuthorization(user.id,e.warehouse_id,e.authorization);
                        }else{
                            MainActivity.api.revokeLocalAuthorization(user.id,e.warehouse_id,e.authorization);
                        }
                    }
                }
            }
            return true;
        },getResources(),"user_fail_authorization");
    }

    public static class AuthorizationEntry {
        private final String warehouse_id;
        private final String authorization;
        private final boolean granted;

        private boolean new_granted;

        private AuthorizationEntry(@Nullable String warehouse_id, @NonNull String authorization, boolean granted) {
            this.warehouse_id=warehouse_id;
            this.authorization=authorization;
            this.granted=granted;
            this.new_granted=granted;
        }
    }

}
