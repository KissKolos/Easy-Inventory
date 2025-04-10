package com.easyinventory.client.ui.users;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;

import com.easyinventory.client.ActivityUtils;
import com.easyinventory.client.MainActivity;
import com.easyinventory.client.R;
import com.easyinventory.client.databinding.FragmentUsersBinding;
import com.easyinventory.client.ui.BaseListFragment;
import com.easyinventory.client.ui.FormattedException;
import com.easyinventory.client.ui.UIUtils;
import com.easyinventory.client.ui.warehouses.WarehouseListActivity;

import java.util.ArrayList;

import easyinventoryapi.User;
import easyinventoryapi.Warehouse;

public class UsersFragment extends BaseListFragment<User> {

    private FragmentUsersBinding binding;

    @Override
    protected User[] load(String query,int offset,int length,boolean archived) throws FormattedException {
        return UIUtils.formatException(()->MainActivity.api.getUsers(query,offset,length),getResources(),"user_fail_list");
    }

    @Override
    protected void delete(String id) throws FormattedException {
        UIUtils.formatException(()->MainActivity.api.deleteUser(id),getResources(),"user_fail_delete");
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentUsersBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        UsersAdapter adapter=new UsersAdapter(this.getActivity(),new ArrayList<>());
        initRoot(root,adapter,u->u.id,UserEditActivity.class);

        ActivityResultLauncher<Intent> launcher=ActivityUtils.createLauncher(this, d->{
            Warehouse wh=new Warehouse(d);

            User item=adapter.getSelected();
            if(item!=null) {
                Intent i = new Intent(this.getContext(), UserAuthorizationsActivity.class);
                i.putExtra("value", item.toJSON().toString());
                i.putExtra("warehouse_id",wh.id);
                addExtraContext(i);
                this.requireContext().startActivity(i);
            }
        },false);

        root.findViewById(R.id.editAuthorization).setOnClickListener(e-> startActivityForSelected(UserAuthorizationsActivity.class));
        root.findViewById(R.id.editLocalAuthorization).setOnClickListener(e-> {
            if(adapter.getSelected()!=null)
                launcher.launch(new Intent(this.getContext(), WarehouseListActivity.class));
        });

        return root;
    }

    @Override
    public void controlToolbar(TextView title, ImageButton warehouse_button, SearchView search_bar) {
        super.controlToolbar(title, warehouse_button, search_bar);
        title.setText(R.string.tab_users);
        warehouse_button.setVisibility(View.GONE);
        search_bar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}