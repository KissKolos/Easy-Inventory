package com.easyinventory.client;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;

import android.widget.SearchView;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.easyinventory.client.databinding.ActivityLoggedInBinding;

import java.util.Objects;

public class LoggedInActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private SearchView search_bar;
    private TextView title;
    private ImageButton warehouse_button;
    private ToolbarController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(MainActivity.isStopped())
            this.finish();

        com.easyinventory.client.databinding.ActivityLoggedInBinding binding = ActivityLoggedInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        View bar=findViewById(R.id.app_bar_logged_in).findViewById(R.id.appbar);
        search_bar=bar.findViewById(R.id.search_bar);
        title=bar.findViewById(R.id.title);
        warehouse_button=bar.findViewById(R.id.warehouseSelect);

        if(controller!=null)
            controller.controlToolbar(title,warehouse_button,search_bar);

        setSupportActionBar(bar.findViewById(R.id.toolbar));
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_users, R.id.nav_warehouses, R.id.nav_storages,R.id.nav_items,R.id.nav_units,R.id.nav_operations,R.id.nav_search,R.id.nav_profile)
                .setOpenableLayout(drawer)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_logged_in);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        View header= binding.navView.getHeaderView(0);
        ((TextView)header.findViewById(R.id.username)).setText(MainActivity.user_name);
        ((TextView)header.findViewById(R.id.userid)).setText(MainActivity.user_id);
    }

    public void setToolbarController(ToolbarController t) {
        controller=t;
        if(title!=null)
            controller.controlToolbar(title,warehouse_button,search_bar);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_logged_in);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public interface ToolbarController {
        void controlToolbar(TextView title,ImageButton warehouse_button,SearchView search_bar);
    }
}