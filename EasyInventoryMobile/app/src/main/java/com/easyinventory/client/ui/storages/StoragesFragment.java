package com.easyinventory.client.ui.storages;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;

import com.easyinventory.client.LoggedInActivity;
import com.easyinventory.client.MainActivity;
import com.easyinventory.client.R;
import com.easyinventory.client.databinding.FragmentStoragesBinding;
import com.easyinventory.client.ui.FormattedException;
import com.easyinventory.client.ui.LocalListFragment;
import com.easyinventory.client.ui.UIUtils;

import java.util.ArrayList;
import java.util.Objects;

import easyinventoryapi.Storage;

public class StoragesFragment extends LocalListFragment<Storage> {

    private FragmentStoragesBinding binding;

    @Override
    protected Storage[] load(String query,int offset,int length,boolean archived) throws FormattedException {
        if(getSelectedWarehouse()==null)
            return new Storage[0];
        return UIUtils.formatException(()->MainActivity.api.getStorages(getSelectedWarehouse().id,query,offset,length,archived),getResources(),"storages_fail_list");
    }

    @Override
    protected void delete(String id) throws FormattedException {
        UIUtils.formatException(()->MainActivity.api.deleteStorage(Objects.requireNonNull(getSelectedWarehouse()).id,id),getResources(),"storages_fail_delete");
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentStoragesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //requireActivity().setTitle("lol");
        //Toolbar t=((View)container.getParent().getParent()).findViewById(R.id.toolbar);
        //t.setTitle("lol");

        StorageAdapter adapter=new StorageAdapter(this.getActivity(),new ArrayList<>());
        initRoot(root,adapter,s->s.id,StorageEditActivity.class);

        root.findViewById(R.id.editLimits).setOnClickListener(e-> startActivityForSelected(StorageLimitActivity.class));

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}