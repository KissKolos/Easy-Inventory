package com.easyinventory.client.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.easyinventory.client.MainActivity;
import com.easyinventory.client.R;
import com.easyinventory.client.databinding.FragmentProfileBinding;
import com.easyinventory.client.ui.UIUtils;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        root.findViewById(R.id.password_change).setOnClickListener(e->{
            this.requireContext().startActivity(new Intent(this.getContext(), PasswordChangeActivity.class));
        });
        root.findViewById(R.id.logout).setOnClickListener(e-> {
            UIUtils.waitForTask(this.requireActivity(),root,()->UIUtils.formatException(()->{
                MainActivity.api.logout();
                return true;
            },getResources(),"logout_fail"),r->{
                this.requireActivity().finish();
            },()->{});
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}