package com.easyinventory.client.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.easyinventory.client.MainActivity;
import com.easyinventory.client.R;

import java.util.Objects;

public abstract class BaseEditActivity<T> extends AppCompatActivity {

    private final int layout;

    public BaseEditActivity(int layout) {
        this.layout=layout;
    }

    protected abstract void initUI(@Nullable T item);

    protected abstract @NonNull T getItem();

    protected abstract void save(@Nullable T original,@NonNull T item) throws FormattedException;

    protected abstract T readContext(Intent i);

    protected void readExtraContext(Intent i) {}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(MainActivity.isStopped())
            this.finish();

        setContentView(layout);
        setSupportActionBar(findViewById(R.id.toolbar));
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        readExtraContext(this.getIntent());
        T original=readContext(this.getIntent());
        initUI(original);

        ImageButton ec=findViewById(R.id.editConfirm);
        ec.setOnClickListener(e -> {
            ec.setEnabled(false);
            T item = getItem();

            UIUtils.waitForTask(this, this.getWindow().getDecorView().getRootView(), () -> {
                this.save(original, item);
                return true;
            }, r -> {
                ec.setEnabled(true);
                this.finish();
            }, () -> ec.setEnabled(true));
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
