package com.optimove.android.optimovemobilesdk.ui.credentials;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import com.optimove.android.Optimove;
import com.optimove.android.optimovemobilesdk.constants.Credentials;
import com.optimove.android.optimovemobilesdk.databinding.FragmentCredentialsBinding;
import com.optimove.android.optimovemobilesdk.BaseFragment;
import com.optimove.android.optimovemobilesdk.ui.initialisation.InitialisationViewModel;

import java.util.Objects;

public class CredentialsFragment extends BaseFragment {

    private FragmentCredentialsBinding binding;
    private InitialisationViewModel initViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentCredentialsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        setScreenInfo("Credentials");

        binding.buttonUpdateUser.setOnClickListener(v -> updateUserId());
        binding.buttonInit.setOnClickListener(v -> lateInitialisation());
        binding.backButton.setOnClickListener(this::goBack);

        initViewModel = new ViewModelProvider(requireActivity()).get(InitialisationViewModel.class);

        return root;
    }

    public void updateUserId() {
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

    private void lateInitialisation() {
        setCredentials();
        registerForPush();
        setInitialized();
    }

    // Finish the initialization, start to get messages
    private void setCredentials() {
        Optimove.setCredentials(Credentials.OPTIMOVE_CREDS, Credentials.OPTIMOBILE_CREDS);
    }

    private void registerForPush() {
        Optimove.getInstance().pushRequestDeviceToken();
    }

    private void setInitialized() {
        initViewModel.getUiState().setValue(new InitialisationViewModel.InitialisationUiState(true));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
