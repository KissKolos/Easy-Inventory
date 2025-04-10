package com.easyinventory.client.ui.profile;

import android.content.Intent;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.easyinventory.client.MainActivity;
import com.easyinventory.client.R;
import com.easyinventory.client.ui.BaseEditActivity;
import com.easyinventory.client.ui.FormattedException;
import com.easyinventory.client.ui.UIUtils;

public class PasswordChangeActivity extends BaseEditActivity<PasswordChangeActivity.PasswordChange> {
    public PasswordChangeActivity() {
        super(R.layout.activity_password_change);
    }

    @Override
    protected void initUI(@Nullable PasswordChange item) {}

    @NonNull
    @Override
    protected PasswordChange getItem() {
        return new PasswordChange(
            ((EditText)findViewById(R.id.new_password1)).getText().toString(),
            ((EditText)findViewById(R.id.new_password2)).getText().toString(),
            ((EditText)findViewById(R.id.old_password)).getText().toString()
        );
    }

    @Override
    protected void save(@Nullable PasswordChange original, @NonNull PasswordChange item) throws FormattedException {
        if(!item.new_password1.equals(item.new_password2))
            throw new FormattedException(getResources().getString(R.string.profile_password_fail_mismatch),null);
        UIUtils.formatException(()-> {
            MainActivity.api.changePassword(item.old_password,item.new_password1);
            return true;
        },getResources(),"profile_password_fail");
    }

    @Override
    protected PasswordChange readContext(Intent i) {
        return null;
    }

    protected static class PasswordChange {
        private final String new_password1,new_password2,old_password;

        private PasswordChange(String new_password1,String new_password2,String old_password) {
            this.new_password1=new_password1;
            this.new_password2=new_password2;
            this.old_password=old_password;
        }
    }
}
