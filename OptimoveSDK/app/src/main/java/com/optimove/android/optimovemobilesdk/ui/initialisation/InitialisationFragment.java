package com.optimove.android.optimovemobilesdk.ui.initialisation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.optimove.android.Optimove;
import com.optimove.android.OptimoveConfig;
import com.optimove.android.optimovemobilesdk.BaseFragment;
import com.optimove.android.optimovemobilesdk.R;
import com.optimove.android.optimovemobilesdk.constants.Credentials;
import com.optimove.android.optimovemobilesdk.databinding.FragmentInitialisationBinding;

public class InitialisationFragment extends BaseFragment {

    private FragmentInitialisationBinding binding;
    private InitialisationViewModel initViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentInitialisationBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.standardButton.setOnClickListener(v -> standardInitialisation());
        binding.lateButton.setOnClickListener(v -> lateInitialisation());

        initViewModel = new ViewModelProvider(requireActivity()).get(InitialisationViewModel.class);

        return root;
    }

    private void navigateHome() {
        Navigation.findNavController(getView()).navigate(R.id.action_navigation_initialisation_to_navigation_home);
    }

    private void standardInitialisation() {
        standardAuthentication();
        enableRemoteLogs();
        registerForPush();
        setInitialized();
        navigateHome();
    }

    private void lateInitialisation() {
        lateAuthentication();
        enableRemoteLogs();
        navigateHome();
    }

    private void setInitialized() {
        initViewModel.getUiState().setValue(new InitialisationViewModel.InitialisationUiState(true));
    }

    private void standardAuthentication() {
        Optimove.initialize(requireActivity().getApplication(), new OptimoveConfig.Builder(
                Credentials.OPTIMOVE_CREDS, Credentials.OPTIMOBILE_CREDS)
                .enableInAppMessaging(OptimoveConfig.InAppConsentStrategy.AUTO_ENROLL)
                .build());
    }

    // In rare cases when you determine credentials dynamically
    // When SDK is initialised this way, events and deferred deep link clicks are cached, in-app messages are not delivered.
    private void lateAuthentication() {
        OptimoveConfig.FeatureSet desiredFeatures = new OptimoveConfig.FeatureSet().withOptimobile().withOptimove();
        Optimove.initialize(
                requireActivity().getApplication(),
                new OptimoveConfig.Builder(OptimoveConfig.Region.EU, desiredFeatures).build()
        );
    }

    private void enableRemoteLogs() {
        // Shouldn't be called unless explicitly told to
        Optimove.enableStagingRemoteLogs();
    }

    private void registerForPush() {
        Optimove.getInstance().pushRequestDeviceToken();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
