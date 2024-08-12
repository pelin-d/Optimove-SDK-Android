package com.optimove.android.preferencecenter;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.optimove.android.Optimove;
import com.optimove.android.OptimoveConfig;
import com.optimove.android.main.common.UserInfo;
import com.optimove.android.main.tools.networking.HttpClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Response;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OptimovePreferenceCenter {
    private static final String TAG = "OptimovePrefCenter";
    private static OptimovePreferenceCenter shared;
    private static OptimoveConfig optimoveConfig;

    static ExecutorService executorService;

    static final Handler handler = new Handler(Looper.getMainLooper());

    public interface PreferencesGetHandler {
        void run(ResultType result, @Nullable Preferences preferences);
    }

    public interface PreferencesSetHandler {
        void run(ResultType result);
    }

    public enum ResultType {
        SUCCESS,
        ERROR_USER_NOT_SET,
        ERROR
    }

    public enum Channel {
        MOBILE_PUSH(489), WEB_PUSH(490), SMS(493), IN_APP(427), WHATSAPP(498), MAIL(15), INBOX(495);
        private final int channel;

        Channel(int channel) {
            this.channel = channel;
        }

        @NonNull
        public int getValue() {
            return channel;
        }

        private static Channel getChannelByValue(int value) {
            switch (value) {
                case 489:
                    return MOBILE_PUSH;
                case 490:
                    return WEB_PUSH;
                case 493:
                    return SMS;
                case 427:
                    return IN_APP;
                case 498:
                    return WHATSAPP;
                case 15:
                    return MAIL;
                case 495:
                    return INBOX;
                default:
                    throw new IllegalArgumentException("Preference center does not support channel " + value);
            }
        }
    }

    public static OptimovePreferenceCenter getInstance() {
        if (shared == null) {
            throw new IllegalStateException("OptimovePreferenceCenter is not initialized");
        }
        return shared;
    }

    /**
     * Initializes an instance of OptimovePreferenceCenter
     * <p>
     * This API is intended for internal SDK use. Do not call this API or depend on it in your app.
     *
     * @param currentConfig current config
     */
    public static void initialize(OptimoveConfig currentConfig) {
        shared = new OptimovePreferenceCenter();
        optimoveConfig = currentConfig;

        executorService = Executors.newSingleThreadExecutor();
    }

    /**
     * Asynchronously runs preferences get handler on UI thread. Handler receives a single argument Preferences
     *
     * @param preferencesGetHandler handler
     */
    public void getPreferencesAsync(@NonNull PreferencesGetHandler preferencesGetHandler) {
        UserInfo userInfo = Optimove.getInstance().getUserInfo();
        String userId = userInfo.getUserId();

        if (userId == null || Objects.equals(userId, userInfo.getVisitorId())) {
            Log.w(TAG, "Customer ID is not set");
            handler.post(() -> preferencesGetHandler.run(ResultType.ERROR_USER_NOT_SET, null));
            return;
        }

        Runnable task = new GetPreferencesRunnable(userId, preferencesGetHandler);
        executorService.submit(task);
    }

    /**
     * Asynchronously runs preferences set handler on UI thread. Handler receives a single Boolean result argument
     *
     * @param preferencesSetHandler handler
     * @param updates               list of preference updates to set
     */
    public void setCustomerPreferencesAsync(@NonNull PreferencesSetHandler preferencesSetHandler, List<PreferenceUpdate> updates) {
        UserInfo userInfo = Optimove.getInstance().getUserInfo();
        String userId = userInfo.getUserId();

        if (userId == null || Objects.equals(userId, userInfo.getVisitorId())) {
            Log.w(TAG, "Customer ID is not set");
            handler.post(() -> preferencesSetHandler.run(ResultType.ERROR_USER_NOT_SET));
            return;
        }

        Runnable task = new SetPreferencesRunnable(userId, preferencesSetHandler, updates);
        executorService.submit(task);
    }

    static class GetPreferencesRunnable implements Runnable {
        private final String customerId;
        private final PreferencesGetHandler callback;

        GetPreferencesRunnable(String customerId, PreferencesGetHandler callback) {
            this.customerId = customerId;
            this.callback = callback;
        }

        @Override
        public void run() {
            Config config = optimoveConfig.getPreferenceCenterConfig();
            String region = config.getRegion();
            HttpClient httpClient = HttpClient.getInstance();
            Preferences preferences = null;
            ResultType resultType = ResultType.ERROR;
            try {
                String encodedCustomerId = URLEncoder.encode(this.customerId, "UTF-8");
                String url = "https://preference-center-" + region + ".optimove.net/api/v1/preferences?customerId=" + encodedCustomerId + "&brandGroupId=" + config.getBrandGroupId();

                try (Response response = httpClient.getSync(url, config.getTenantId())) {
                    if (!response.isSuccessful()) {
                        logFailedResponse(response);
                    } else {
                        preferences = mapResponseToPreferences(response);
                        resultType = ResultType.SUCCESS;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            this.fireCallback(resultType, preferences);
        }

        private void fireCallback(ResultType result, @Nullable Preferences preferences) {
            handler.post(() -> GetPreferencesRunnable.this.callback.run(result, preferences));
        }
    }

    static class SetPreferencesRunnable implements Runnable {
        private final String customerId;
        private final PreferencesSetHandler callback;
        private final List<PreferenceUpdate> updates;

        SetPreferencesRunnable(String customerId, PreferencesSetHandler callback, List<PreferenceUpdate> updates) {
            this.customerId = customerId;
            this.callback = callback;
            this.updates = updates;
        }

        @Override
        public void run() {
            Config config = optimoveConfig.getPreferenceCenterConfig();
            String region = config.getRegion();
            HttpClient httpClient = HttpClient.getInstance();

            ResultType result = ResultType.ERROR;
            try {
                String encodedCustomerId = URLEncoder.encode(this.customerId, "UTF-8");
                String url = "https://preference-center-" + region + ".optimove.net/api/v1/preferences?customerId=" + encodedCustomerId + "&brandGroupId=" + config.getBrandGroupId();
                JSONArray data = mapPreferenceUpdatesToArray(updates);

                try (Response response = httpClient.putSync(url, data, config.getTenantId())) {
                    if (response.isSuccessful()) {
                        result = ResultType.SUCCESS;
                    }
                }
            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }

            this.fireCallback(result);
        }

        private void fireCallback(ResultType result) {
            handler.post(() -> SetPreferencesRunnable.this.callback.run(result));
        }
    }

    private static JSONArray mapPreferenceUpdatesToArray(List<PreferenceUpdate> updates) throws JSONException {
        JSONArray updatesArray = new JSONArray();

        for (int i = 0; i < updates.size(); i++) {
            String topicId = updates.get(i).getTopicId();
            List<Channel> channels = updates.get(i).getSubscribedChannels();
            JSONArray subscribedChannels = new JSONArray();
            for (int j = 0; j < channels.size(); j++) {
                subscribedChannels.put(channels.get(j).getValue());
            }

            JSONObject mappedUpdate = new JSONObject();
            mappedUpdate.put("topicId", topicId);
            mappedUpdate.put("channelSubscription", subscribedChannels);

            updatesArray.put(mappedUpdate);
        }

        return updatesArray;
    }

    private static Preferences mapResponseToPreferences(Response response) {
        try {
            JSONObject data = new JSONObject(response.body().string());

            JSONArray channels = data.getJSONArray("channels");
            List<Channel> configuredChannels = new ArrayList<>();

            int len = channels.length();
            for (int i = 0; i < len; i++) {
                configuredChannels.add(Channel.getChannelByValue(channels.getInt(i)));
            }

            JSONArray topics = data.getJSONArray("topics");
            List<Topic> customerPreferences = new ArrayList<>();

            int topicLength = topics.length();
            for (int i = 0; i < topicLength; i++) {
                JSONObject topicObj = topics.getJSONObject(i);

                List<Channel> subscribedChannels = new ArrayList<>();
                JSONArray channelSubscriptionArray = topicObj.getJSONArray("channelSubscription");
                for (int j = 0; j < channelSubscriptionArray.length(); j++) {
                    subscribedChannels.add(Channel.getChannelByValue(channelSubscriptionArray.getInt(j)));
                }

                Topic topic = new Topic(topicObj.getString("topicId"), topicObj.getString("topicName"), topicObj.getString("topicDescription"), subscribedChannels);

                customerPreferences.add(topic);
            }

            return new Preferences(configuredChannels, customerPreferences);
        } catch (NullPointerException | JSONException | IOException e) {
            Log.e(TAG, e.getMessage());
            return null;
        }
    }

    private static void logFailedResponse(Response response) {
        switch (response.code()) {
            case 400:
                Log.e(TAG, "Status code 400: check preference center configuration");
                break;
            default:
                Log.e(TAG, response.message());
                break;
        }
    }
}
