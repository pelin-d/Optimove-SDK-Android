package com.optimove.android.optimovemobilesdk.ui.dashboard;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import com.optimove.android.Optimove;
import com.optimove.android.optimobile.InAppInboxItem;
import com.optimove.android.optimobile.OptimoveInApp;
import com.optimove.android.optimovemobilesdk.SimpleCustomEvent;
import com.optimove.android.optimovemobilesdk.constants.Constants;
import com.optimove.android.optimovemobilesdk.databinding.FragmentDashboardBinding;
import com.optimove.android.optimovemobilesdk.ui.BaseFragment;
import com.optimove.android.preferencecenter.Channel;
import com.optimove.android.preferencecenter.OptimovePreferenceCenter;
import com.optimove.android.preferencecenter.PreferenceUpdate;
import com.optimove.android.preferencecenter.Preferences;
import com.optimove.android.preferencecenter.Topic;

import java.util.ArrayList;
import java.util.List;

public class DashboardFragment extends BaseFragment {

    private FragmentDashboardBinding binding;

    static final String TAG = Constants.TAG;
    static final String PC_TAG = Constants.PC_TAG;

    private TextView outputTv;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DashboardViewModel viewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        outputTv = binding.userIdTextView;

        setOnClickListeners();

        this.hideIrrelevantInputs();

        return root;
    }

    private void setOnClickListeners() {
        binding.updateUserIdButton.setOnClickListener(this::updateUserId);
        binding.customerReportEventButton.setOnClickListener(this::reportEvent);
        binding.readInbox.setOnClickListener(this::readInbox);
        binding.markInboxItemsAsRead.setOnClickListener(this::markInboxAsRead);
        binding.deleteInbox.setOnClickListener(this::deleteInbox);
        binding.getPreferences.setOnClickListener(this::getPreferences);
        binding.setPreferences.setOnClickListener(this::setPreferences);
        binding.submitCredentialsBtn.setOnClickListener(this::setCredentials);
    }

    @Override
    public void reportEvent(View view) {
        if (view == null)
            return;
        outputTv.setText("Reporting Custom Event for Visitor without optional value");
        runFromWorker(() -> Optimove.getInstance().reportEvent(new SimpleCustomEvent()));
        runFromWorker(() -> Optimove.getInstance().reportEvent("Event_No ParaMs     "));
    }

    private void updateUserId(View view) {
        EditText uidInput = binding.userIdInput;
        EditText emailInput = binding.userEmailInput;
        String userId = uidInput.getText().toString();
        String userEmail = emailInput.getText().toString();

        if (userEmail.isEmpty()) {
            outputTv.setText("Calling setUserId");
            Optimove.getInstance().setUserId(userId);
        } else if (userId.isEmpty()) {
            outputTv.setText("Calling setUserEmail");
            Optimove.getInstance().setUserEmail(userEmail);
        } else {
            outputTv.setText("Calling registerUser");
            Optimove.getInstance().registerUser(userId, userEmail);
        }
    }

    private void readInbox(View view) {
        List<InAppInboxItem> items = OptimoveInApp.getInstance().getInboxItems();
        if (items.size() == 0) {
            Log.d(TAG, "no inbox items!");
            return;
        }
        for (int i = 0; i < items.size(); i++) {
            InAppInboxItem item = items.get(i);
            Log.d(TAG, "title: " + item.getTitle() + ", isRead: " + item.isRead());
        }
    }

    private void markInboxAsRead(View view) {
        Log.d(TAG, "mark  all inbox read");

        OptimoveInApp.getInstance().markAllInboxItemsAsRead();
    }

    private void deleteInbox(View view) {

        List<InAppInboxItem> items = OptimoveInApp.getInstance().getInboxItems();
        if (items.size() == 0) {
            Log.d(TAG, "no inbox items!");
            return;
        }
        for (int i = 0; i < items.size(); i++) {
            OptimoveInApp.getInstance().deleteMessageFromInbox(items.get(i));
        }

    }

    // ******************** PC start *********************

    private void getPreferences(View view) {
        OptimovePreferenceCenter.getInstance().getPreferencesAsync((OptimovePreferenceCenter.ResultType result, Preferences preferences) -> {
            switch (result) {
                case ERROR_USER_NOT_SET:
                case ERROR:
                case ERROR_CREDENTIALS_NOT_SET:
                    Log.d(PC_TAG, result.toString());
                    break;
                case SUCCESS: {

                    Log.d(PC_TAG, "configured: " + preferences.getConfiguredChannels().toString());
                    List<Topic> topics = preferences.getCustomerPreferences();
                    for (int i = 0; i < topics.size(); i++) {
                        Topic topic = topics.get(i);
                        Log.d(PC_TAG, topic.getId() + " " + topic.getName() + " " + topic.getSubscribedChannels().toString());
                    }

                    break;
                }
                default:
                    Log.d(PC_TAG, "unknown res type");
            }
        });

    }

    private void setPreferences(View view) {
        OptimovePreferenceCenter.getInstance().getPreferencesAsync((OptimovePreferenceCenter.ResultType result, Preferences preferences) -> {
            switch (result) {
                case ERROR_USER_NOT_SET:
                case ERROR:
                case ERROR_CREDENTIALS_NOT_SET:
                    Log.d(PC_TAG, result.toString());
                    break;
                case SUCCESS: {
                    Log.d(PC_TAG, "loaded prefs for set: good");


                    List<Channel> configuredChannels = preferences.getConfiguredChannels();
                    List<Topic> topics = preferences.getCustomerPreferences();

                    List<PreferenceUpdate> updates = new ArrayList<>();
                    for (int i = 0; i < topics.size(); i++) {
                        updates.add(new PreferenceUpdate(topics.get(i).getId(), configuredChannels.subList(0, 1)));
                    }

                    OptimovePreferenceCenter.getInstance().setCustomerPreferencesAsync((OptimovePreferenceCenter.ResultType setResult) -> {
                        Log.d(PC_TAG, setResult.toString());
                    }, updates);

                    break;
                }
                default:
                    Log.d(PC_TAG, "unknown res type");
            }
        });
    }

    // ******************** PC end *********************

    private void setCredentials(View view) {
        EditText optimoveCreds = binding.optimoveCredInput;
        EditText optimobileCreds = binding.optimobileCredInput;
        EditText pcCreds = binding.prefCenterCredInput;

        String optimoveCredentials = optimoveCreds.getText().toString();
        String optimobileCredentials = optimobileCreds.getText().toString();
        String prefCenterCredentials = pcCreds.getText().toString();

        if (optimoveCredentials.isEmpty() && optimobileCredentials.isEmpty()) {
            return;
        }

        if (optimoveCredentials.isEmpty()) {
            optimoveCredentials = null;
        }

        if (optimobileCredentials.isEmpty()) {
            optimobileCredentials = null;
        }

        if (prefCenterCredentials.isEmpty()) {
            prefCenterCredentials = null;
        }

        try {
            Optimove.setCredentials(optimoveCredentials, optimobileCredentials, prefCenterCredentials);
        } catch (Exception e) {
            outputTv.setText(e.getMessage());
            return;
        }

        outputTv.setText("Credentials submitted");
        Button setCredsBtn = (Button) binding.submitCredentialsBtn;
        setCredsBtn.setEnabled(false);
    }

    private void runFromWorker(Runnable runnable) {
        new Thread(runnable).start();
    }

    private void hideIrrelevantInputs() {
        if (!Optimove.getConfig().isPreferenceCenterConfigured()) {
            Button getPrefsBtn = (Button) binding.getPreferences;
            getPrefsBtn.setVisibility(View.GONE);

            Button setPrefsBtn = (Button) binding.setPreferences;
            setPrefsBtn.setVisibility(View.GONE);
        }


        if (!Optimove.getConfig().usesDelayedConfiguration()) {
            EditText optimoveCredInput = binding.optimoveCredInput;
            optimoveCredInput.setVisibility(View.GONE);

            EditText optimobileCredInput = binding.optimobileCredInput;
            optimobileCredInput.setVisibility(View.GONE);

            EditText pcCredInput = binding.prefCenterCredInput;
            pcCredInput.setVisibility(View.GONE);

            Button setCredsBtn = (Button) binding.submitCredentialsBtn;
            setCredsBtn.setVisibility(View.GONE);


        }
    }
    
}
