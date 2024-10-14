package com.optimove.android.optimovemobilesdk.ui.preferencecenter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.optimove.android.optimovemobilesdk.databinding.FragmentPreferenceCenterBinding;
import com.optimove.android.optimovemobilesdk.ui.BaseFragment;

public class PreferenceCenterFragment extends BaseFragment {

    private FragmentPreferenceCenterBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        PreferenceCenterViewModel viewModel =
                new ViewModelProvider(getActivity()).get(PreferenceCenterViewModel.class);

        binding = FragmentPreferenceCenterBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        setScreenInfo("PreferenceCenter");

        binding.standardInitButton.setOnClickListener(v -> viewModel.onStandardInitClick());
        binding.delayedInitButton.setOnClickListener(v -> viewModel.onDelayedInitClick());

        viewModel.getText().observe(getViewLifecycleOwner(), newText -> {
            binding.contentText.setText(newText);
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
