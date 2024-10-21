package com.optimove.android.optimovemobilesdk.ui.preferencecenter;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import com.optimove.android.Optimove;
import com.optimove.android.OptimoveConfig;
import com.optimove.android.optimovemobilesdk.R;
import com.optimove.android.optimovemobilesdk.constants.Credentials;
import com.optimove.android.optimovemobilesdk.databinding.FragmentPreferenceCenterBinding;
import com.optimove.android.optimovemobilesdk.ui.BaseFragment;
import com.optimove.android.preferencecenter.Channel;
import com.optimove.android.preferencecenter.OptimovePreferenceCenter;
import com.optimove.android.preferencecenter.PreferenceUpdate;
import com.optimove.android.preferencecenter.Preferences;
import com.optimove.android.preferencecenter.Topic;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class PreferenceCenterFragment extends BaseFragment {

    private FragmentPreferenceCenterBinding binding;
    private AtomicReference<Preferences> preferences = new AtomicReference<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentPreferenceCenterBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        setScreenInfo("PreferenceCenter");

        binding.standardInitButton.setOnClickListener(v -> preferenceCenterStandardInit());
        binding.delayedInitButton.setOnClickListener(v -> preferenceCenterDelayedInit());
        binding.getCustomerPrefsButton.setOnClickListener(v -> {
            fetchCustomerPreferences();
            displayCustomerPreferences();
        });

        return root;
    }

    private void preferenceCenterStandardInit() {
        Optimove.initialize(requireActivity().getApplication(), new OptimoveConfig.Builder(Credentials.OPTIMOVE_CREDS, Credentials.OPTIMOBILE_CREDS)
                .enablePreferenceCenter(Credentials.PREFERENCE_CENTER_CREDS)
                .build());
        setDisplayText("Preference Center Standard Init", TextChange.REPLACE);
    }

    private void preferenceCenterDelayedInit() {
        OptimoveConfig.FeatureSet featureSet = new OptimoveConfig.FeatureSet().withOptimove().withOptimobile().withPreferenceCenter();
        Optimove.initialize(requireActivity().getApplication(), new OptimoveConfig.Builder(OptimoveConfig.Region.DEV, featureSet).build());

        // time passes...
        startDelayedInitTimer();
    }

    private void startDelayedInitTimer() {
        new CountDownTimer(3000, 1000) { // 3000 milliseconds = 3 seconds
            public void onTick(long millisUntilFinished) {
                setDisplayText("Seconds remaining: " + millisUntilFinished / 1000, TextChange.REPLACE);
            }

            public void onFinish() {
                Optimove.setCredentials(
                        Credentials.OPTIMOVE_CREDS,
                        Credentials.OPTIMOBILE_CREDS,
                        Credentials.PREFERENCE_CENTER_CREDS
                );
                setDisplayText("Preference Center Delayed Init finished", TextChange.REPLACE);
            }
        }.start();
    }

    private void fetchCustomerPreferences() {
        OptimovePreferenceCenter.getInstance().getPreferencesAsync((OptimovePreferenceCenter.ResultType result, Preferences fetchPreferences) -> {
            switch (result) {
                case ERROR_USER_NOT_SET:
                    setDisplayText("GetCustomerPreferences ERROR: User not set", TextChange.APPEND);
                    break;
                case ERROR_CREDENTIALS_NOT_SET:
                    setDisplayText("GetCustomerPreferences ERROR: Credentials not set", TextChange.APPEND);
                    break;
                case ERROR:
                    setDisplayText("GetCustomerPreferences ERROR", TextChange.APPEND);
                    break;
                case SUCCESS: {
                    preferences.set(fetchPreferences);
                    break;
                }
            }
        });
    }

    private void displayCustomerPreferences() {
        if (preferences.get() == null) {
            setDisplayText("GetCustomerPreferences SUCCESS, No preferences found", TextChange.APPEND);
            return;
        }
        List<Channel> configuredChannels = preferences.get().getConfiguredChannels(); // List<Channel> e.g. MOBILE_PUSH, SMS
        List<Topic> topics = preferences.get().getCustomerPreferences();

        String channelJson = jsonToPrettyString(configuredChannels);
        String topicsJson = jsonToPrettyString(topics);

        String channelText = String.format(getString(R.string.subscribed_channels_text), channelJson);
        String topicsText = String.format(getString(R.string.subscribed_topics_text), topicsJson);

        setDisplayText(channelText, TextChange.APPEND);
        setDisplayText(topicsText, TextChange.APPEND);
    }

    private void setCustomerPreferences() {
        List<PreferenceUpdate> updates = new ArrayList<>();
//        for (int i = 0; i < topics.size(); i++) {
//            // Note, you can only subscribe to channels that are configured
//            updates.add(new PreferenceUpdate(topics.get(i).getId(), configuredChannels.subList(0, 1)));
//        }

        OptimovePreferenceCenter
                .getInstance()
                .setCustomerPreferencesAsync((OptimovePreferenceCenter.ResultType setResult) -> {
                    // TODO handle new updates
                }, updates);
    }

    public void setDisplayText(String message, TextChange state) {
        switch (state) {
            case APPEND:
                binding.contentText.append("\n" + message);
                break;
            case REPLACE:
                binding.contentText.setText(message);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public enum TextChange {
        APPEND,
        REPLACE,
    }

}
