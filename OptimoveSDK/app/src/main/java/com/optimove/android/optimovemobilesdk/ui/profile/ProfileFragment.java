package com.optimove.android.optimovemobilesdk.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.optimove.android.Optimove;
import com.optimove.android.optimovemobilesdk.databinding.FragmentProfileBinding;

import java.util.Objects;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ProfileViewModel profileViewModel =
                new ViewModelProvider(this).get(ProfileViewModel.class);

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final MaterialButton buttonUpdateUser = binding.buttonUpdateUser;
        buttonUpdateUser.setOnClickListener(this::updateUserId);

        return root;
    }

    public void updateUserId(View view) {
        String userId = Objects.requireNonNull(binding.editTextUserId.getText()).toString();
        String userEmail = Objects.requireNonNull(binding.editTextUserEmail.getText()).toString();

        if (userEmail.isEmpty()) {
            showMessage("Calling setUserId");
            Optimove.getInstance().setUserId(userId);
        } else if (userId.isEmpty()) {
            showMessage("Calling setUserEmail");
            Optimove.getInstance().setUserEmail(userEmail);
        } else {
            showMessage("Calling registerUser");
            Optimove.getInstance().registerUser(userId, userEmail);
        }
    }

    public void showMessage(String message) {
        Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
