package com.optimove.android.optimovemobilesdk.ui;

import android.util.Log;
import android.view.View;

import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.optimove.android.Optimove;
import com.optimove.android.optimovemobilesdk.EventReport;
import com.optimove.android.optimovemobilesdk.SimpleCustomEvent;
import com.optimove.android.optimovemobilesdk.constants.Constants;

import java.util.List;

public class BaseFragment extends Fragment {

    public String screenName;
    public String screenCategory;

    @Override
    public void onStart() {
        super.onStart();
        if (screenName != null) {
            Optimove.getInstance().reportScreenVisit(screenName, screenCategory);
            Log.d(Constants.TAG,
                    "Screen visit reported | screenName: " + screenName +
                            ", screenCategory: " + screenCategory
            );
        }
    }

    /**
     Call this in the onCreate() method in the Fragments to report screen visits.
     * @param screenName the name of the screen visited. Setting this null will prevent the screen info being sent.
     * @param screenCategory the screen category it belongs to.
     */
    public void setScreenInfo(String screenName, String screenCategory) {
        this.screenName = screenName;
        this.screenCategory = screenCategory;
    }

    /**
     Call this in the onCreate() method in the Fragments to report screen visits.
     * @param screenName the name of the screen visited. Setting this null will prevent the screen info being sent.
     */
    public void setScreenInfo(String screenName) {
        setScreenInfo(screenName, null);
    }

    public void reportEvent(View v) {
        if (v == null) return;
        showMessage(v, "Reporting Custom Event for Visitor without optional value");
        EventReport.runFromWorker(() -> Optimove.getInstance().reportEvent(new SimpleCustomEvent()));
        EventReport.runFromWorker(() -> Optimove.getInstance().reportEvent("Event_No ParaMs     "));
    }

    public String jsonToPrettyString(List<?> topics) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(topics);
    }

    public void showMessage(View v, String message) {
        if (message == null) return;
        Snackbar.make(v, message, Snackbar.LENGTH_SHORT).show();
    }

    public void showMessageWithAction(View v, String message, String actionMessage) {
        Snackbar.make(v, message, Snackbar.LENGTH_LONG)
                .setAction(actionMessage, view -> {

                })
                .show();
    }

}
