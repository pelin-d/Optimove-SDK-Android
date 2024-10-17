package com.optimove.android.optimovemobilesdk;

import static com.optimove.android.optimovemobilesdk.ui.preferencecenter.PreferenceCenterViewModel.*;

import android.app.Activity;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.CountDownTimer;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.optimove.android.Optimove;

import com.optimove.android.OptimoveConfig;
import com.optimove.android.optimovemobilesdk.constants.Constants;
import com.optimove.android.optimovemobilesdk.databinding.ActivityMainBinding;
import com.optimove.android.optimovemobilesdk.ui.preferencecenter.PreferenceCenterViewModel;
import com.optimove.android.preferencecenter.Channel;
import com.optimove.android.preferencecenter.OptimovePreferenceCenter;
import com.optimove.android.preferencecenter.PreferenceUpdate;
import com.optimove.android.preferencecenter.Preferences;
import com.optimove.android.preferencecenter.Topic;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@SuppressLint("SetTextI18n")
public class MainActivity extends AppCompatActivity {

    private static final int WRITE_EXTERNAL_PERMISSION_REQUEST_CODE = 169;
    AtomicReference<Preferences> preferences = new AtomicReference<>();

    private PreferenceCenterViewModel preferenceCenterViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        hideKeyboard(this);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupWithNavController(binding.navView, navController);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_PERMISSION_REQUEST_CODE);
        }

        //deferred deep links
        Optimove.getInstance().seeIntent(getIntent(), savedInstanceState);

        preferenceCenterViewModel = new ViewModelProvider(this).get(PreferenceCenterViewModel.class);
        setupPreferenceCenterObservers();

    }

    private void setupPreferenceCenterObservers() {
        preferenceCenterViewModel.getStandardInitEvent().observe(this, aVoid -> {
            preferenceCenterStandardInit();
        });
        preferenceCenterViewModel.getDelayedInitEvent().observe(this, aVoid -> {
            preferenceCenterDelayedInit();
        });
        preferenceCenterViewModel.getPreferencesEvent().observe(this, aVoid -> {
            fetchCustomerPreferences();
            displayCustomerPreferences();
        });
    }

    private void preferenceCenterStandardInit() {
        Optimove.initialize(getApplication(), new OptimoveConfig.Builder(Constants.OPTIMOVE_CREDS, Constants.OPTIMOBILE_CREDS)
                .enablePreferenceCenter(Constants.PREFERENCE_CENTER_CREDENTIALS)
                .build());
        preferenceCenterViewModel.onTextChanged("Preference Center Standard Init", TextChange.REPLACE);
    }

    private void preferenceCenterDelayedInit() {
        OptimoveConfig.FeatureSet featureSet = new OptimoveConfig.FeatureSet().withOptimove().withOptimobile().withPreferenceCenter();
        Optimove.initialize(getApplication(), new OptimoveConfig.Builder(OptimoveConfig.Region.DEV, featureSet).build());

        // time passes...
        startDelayedInitTimer();
    }

    private void startDelayedInitTimer() {
        new CountDownTimer(3000, 1000) { // 3000 milliseconds = 3 seconds
            public void onTick(long millisUntilFinished) {
                preferenceCenterViewModel.onTextChanged("Seconds remaining: " + millisUntilFinished / 1000, TextChange.REPLACE);
            }

            public void onFinish() {
                Optimove.setCredentials(
                        Constants.OPTIMOVE_CREDS,
                        Constants.OPTIMOBILE_CREDS,
                        Constants.PREFERENCE_CENTER_CREDENTIALS
                );
                preferenceCenterViewModel.onTextChanged("Preference Center Delayed Init finished", TextChange.REPLACE);
            }
        }.start();
    }

    private void fetchCustomerPreferences() {
        OptimovePreferenceCenter.getInstance().getPreferencesAsync((OptimovePreferenceCenter.ResultType result, Preferences fetchPreferences) -> {
            switch (result) {
                case ERROR_USER_NOT_SET:
                    preferenceCenterViewModel.onTextChanged("GetCustomerPreferences ERROR: User not set", TextChange.APPEND);
                    break;
                case ERROR_CREDENTIALS_NOT_SET:
                    preferenceCenterViewModel.onTextChanged("GetCustomerPreferences ERROR: Credentials not set", TextChange.APPEND);
                    break;
                case ERROR:
                    preferenceCenterViewModel.onTextChanged("GetCustomerPreferences ERROR", TextChange.APPEND);
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
            preferenceCenterViewModel.onTextChanged("GetCustomerPreferences SUCCESS, No preferences found", TextChange.APPEND);
            return;
        }
        List<Channel> configuredChannels = preferences.get().getConfiguredChannels(); // List<Channel> e.g. MOBILE_PUSH, SMS
        List<Topic> topics = preferences.get().getCustomerPreferences();

        String channelJson = jsonToPrettyString(configuredChannels);
        String topicsJson = jsonToPrettyString(topics);

        String channelText = String.format(getString(R.string.subscribed_channels_text), channelJson);
        String topicsText = String.format(getString(R.string.subscribed_topics_text), topicsJson);

        preferenceCenterViewModel.onTextChanged(channelText, TextChange.APPEND);
        preferenceCenterViewModel.onTextChanged(topicsText, TextChange.APPEND);
    }

    private String jsonToPrettyString(List<?> topics) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(topics);
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

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        //deferred deep links
        Optimove.getInstance().seeInputFocus(hasFocus);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        //deferred deep links
        Optimove.getInstance().seeIntent(intent);
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}
