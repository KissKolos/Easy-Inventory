package com.easyinventory.client.ui.operations;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.easyinventory.client.MainActivity;
import com.easyinventory.client.R;
import com.easyinventory.client.databinding.FragmentOperationsBinding;
import com.easyinventory.client.ui.FormattedException;
import com.easyinventory.client.ui.LocalListFragment;
import com.easyinventory.client.ui.UIUtils;

import java.util.ArrayList;
import java.util.Objects;

import easyinventoryapi.Operation;
import easyinventoryapi.Warehouse;

public class OperationsFragment extends LocalListFragment<Operation> {

    private FragmentOperationsBinding binding;

    @Override
    protected Operation[] load(String query,int offset,int length,boolean archived) throws FormattedException {
        if(getSelectedWarehouse()==null)
            return new Operation[0];
        return UIUtils.formatException(()->MainActivity.api.getOperations(getSelectedWarehouse().id,query,offset,length,archived),getResources(),"operation_fail_list");
    }

    @Override
    protected void delete(String id) throws FormattedException {
        UIUtils.formatException(()->MainActivity.api.cancelOperation(Objects.requireNonNull(getSelectedWarehouse()).id,id),getResources(),"operation_fail_cancel");
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentOperationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        OperationAdapter adapter=new OperationAdapter(this.getActivity(),new ArrayList<>());
        initRoot(root,adapter, s->s.id, OperationEditActivity.class);

        root.findViewById(R.id.commit).setOnClickListener(e->{
            Operation item=adapter.getSelected();
            Warehouse selected=getSelectedWarehouse();
            if(item!=null&&selected!=null) {
                UIUtils.showConfirmDialog(root, R.string.app_name, R.string.app_name, R.string.app_name, () -> {
                    UIUtils.waitForTask(getActivity(), root, () -> {
                        UIUtils.formatException(()->MainActivity.api.finishOperation(selected.id,item.id),getResources(),"operation_fail_commit");
                        return true;
                    }, r -> {
                        adapter.remove(item);
                    }, () -> {});
                }, () -> {});
            }
        });

        root.findViewById(R.id.view).setOnClickListener(e->this.startActivityForSelected(OperationViewActivity.class));

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}