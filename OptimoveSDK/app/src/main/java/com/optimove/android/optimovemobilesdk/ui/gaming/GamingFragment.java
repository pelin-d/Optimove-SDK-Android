package com.optimove.android.optimovemobilesdk.ui.gaming;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import com.optimove.android.optimovemobilesdk.databinding.FragmentGamingBinding;
import com.optimove.android.optimovemobilesdk.ui.BaseFragment;

public class GamingFragment extends BaseFragment {

    private FragmentGamingBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        GamingViewModel gamingViewModel =
                new ViewModelProvider(this).get(GamingViewModel.class);

        binding = FragmentGamingBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        setScreenInfo("Gaming");

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
