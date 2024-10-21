package com.optimove.android.optimovemobilesdk.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.google.android.material.button.MaterialButton;
import com.optimove.android.Optimove;
import com.optimove.android.optimovemobilesdk.databinding.FragmentProfileBinding;
import com.optimove.android.optimovemobilesdk.ui.BaseFragment;

import java.util.Objects;

public class ProfileFragment extends BaseFragment {

    private FragmentProfileBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        setScreenInfo("Profile");

        final MaterialButton buttonUpdateUser = binding.buttonUpdateUser;
        buttonUpdateUser.setOnClickListener(this::updateUserId);

        return root;
    }

    public void updateUserId(View view) {
        String userId = Objects.requireNonNull(binding.editTextUserId.getText()).toString();
        String userEmail = Objects.requireNonNull(binding.editTextUserEmail.getText()).toString();

        if (userEmail.isEmpty()) {
            showMessage(binding.getRoot(), "Calling setUserId");
            Optimove.getInstance().setUserId(userId);
        } else if (userId.isEmpty()) {
            showMessage(binding.getRoot(), "Calling setUserEmail");
            Optimove.getInstance().setUserEmail(userEmail);
        } else {
            showMessage(binding.getRoot(), "Calling registerUser");
            Optimove.getInstance().registerUser(userId, userEmail);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
