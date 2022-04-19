package com.optimove.sdk.optimove_sdk;

import android.util.Base64;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.optimove.sdk.optimove_sdk.main.common.TenantInfo;
import com.optimove.sdk.optimove_sdk.main.tools.opti_logger.LogLevel;
import com.optimove.sdk.optimove_sdk.optimobile.DeferredDeepLinkHandlerInterface;
import com.optimove.sdk.optimove_sdk.optimobile.InternalSdkEmbeddingApi;
import com.optimove.sdk.optimove_sdk.optimobile.UrlBuilder;

import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Represents the configuration for the Optimove client
 */
public final class OptimoveConfig {

    @DrawableRes
    public static final int DEFAULT_NOTIFICATION_ICON_ID = R.drawable.optimobile_ic_stat_notifications;
    static final int DEFAULT_SESSION_IDLE_TIMEOUT_SECONDS = 23;

    private @Nullable
    String apiKey;
    private @Nullable
    String secretKey;

    private @Nullable
    String optimoveToken;
    private @Nullable
    String configFileName;

    @DrawableRes
    private int notificationSmallIconId;
    private InAppConsentStrategy inAppConsentStrategy;
    private int sessionIdleTimeoutSeconds;

    private JSONObject runtimeInfo;
    private JSONObject sdkInfo;
    private @Nullable Map<UrlBuilder.Service, String> baseUrlMap;

    private URL deepLinkCname;
    private DeferredDeepLinkHandlerInterface deferredDeepLinkHandler;

    private @Nullable LogLevel minLogLevel;

    public enum InAppConsentStrategy {
        AUTO_ENROLL,
        EXPLICIT_BY_USER
    }

    // Private constructor to discourage not using the Builder.
    private OptimoveConfig() {
    }

    private void setApiKey(@Nullable String apiKey) {
        this.apiKey = apiKey;
    }

    private void setSecretKey(@Nullable String secretKey) {
        this.secretKey = secretKey;
    }

    private void setOptimoveToken(@Nullable String optimoveToken) {
        this.optimoveToken = optimoveToken;
    }

    private void setConfigFileName(@Nullable String configFileName) {
        this.configFileName = configFileName;
    }

    private void setNotificationSmallIconId(@DrawableRes int notificationSmallIconId) {
        this.notificationSmallIconId = notificationSmallIconId;
    }

    private void setSessionIdleTimeoutSeconds(int timeoutSeconds) {
        this.sessionIdleTimeoutSeconds = timeoutSeconds;
    }

    private void setRuntimeInfo(JSONObject info) {
        this.runtimeInfo = info;
    }

    private void setSdkInfo(JSONObject info) {
        this.sdkInfo = info;
    }

    private void setBaseUrlMap(@Nullable Map<UrlBuilder.Service, String> baseUrlMap) {
        this.baseUrlMap = baseUrlMap;
    }

    private void setInAppConsentStrategy(InAppConsentStrategy strategy) {
        this.inAppConsentStrategy = strategy;
    }

    private void setCname(@Nullable URL deepLinkCname) {
        this.deepLinkCname = deepLinkCname;
    }

    private void setDeferredDeepLinkHandler(DeferredDeepLinkHandlerInterface deferredHandler) {
        this.deferredDeepLinkHandler = deferredHandler;
    }

    private void setMinLogLevel(@Nullable LogLevel minLogLevel){
        this.minLogLevel = minLogLevel;
    }

    @Nullable
    public String getApiKey() {
        return apiKey;
    }

    @Nullable
    public String getSecretKey() {
        return secretKey;
    }

    public @Nullable String getOptimoveToken() {
        return optimoveToken;
    }

    public @Nullable String getConfigFileName() {
        return configFileName;
    }

    public @DrawableRes
    int getNotificationSmallIconId() {
        return notificationSmallIconId;
    }

    public int getSessionIdleTimeoutSeconds() {
        return sessionIdleTimeoutSeconds;
    }

    public JSONObject getRuntimeInfo() {
        return this.runtimeInfo;
    }

    public JSONObject getSdkInfo() {
        return this.sdkInfo;
    }

    public @Nullable Map<UrlBuilder.Service, String> getBaseUrlMap() {
        return baseUrlMap;
    }

    public InAppConsentStrategy getInAppConsentStrategy() {
        return inAppConsentStrategy;
    }

    public @Nullable
    URL getDeepLinkCname() {
        return this.deepLinkCname;
    }

    public DeferredDeepLinkHandlerInterface getDeferredDeepLinkHandler() {
        return this.deferredDeepLinkHandler;
    }

    public boolean isOptimoveConfigured(){
        return this.optimoveToken != null && this.configFileName != null ;
    }

    public boolean isOptimobileConfigured(){
        return this.apiKey != null && this.secretKey != null;
    }

    public @Nullable LogLevel getCustomMinLogLevel(){
        return this.minLogLevel;
    }

    /**
     * Config builder for the Optimobile client
     */
    public static class Builder {
        private @Nullable
        String apiKey;
        private @Nullable
        String secretKey;
        private @Nullable
        String optimoveToken;
        private @Nullable
        String configFileName;

        @DrawableRes
        private int notificationSmallIconDrawableId = OptimoveConfig.DEFAULT_NOTIFICATION_ICON_ID;
        private InAppConsentStrategy consentStrategy = null;
        private int sessionIdleTimeoutSeconds = OptimoveConfig.DEFAULT_SESSION_IDLE_TIMEOUT_SECONDS;

        private JSONObject runtimeInfo;
        private JSONObject sdkInfo;
        private @Nullable Map<UrlBuilder.Service, String> baseUrlMap;

        private @Nullable
        URL deepLinkCname;
        private DeferredDeepLinkHandlerInterface deferredDeepLinkHandler;

        private @Nullable LogLevel minLogLevel;

        public Builder(@Nullable TenantInfo optimoveCredentials, @Nullable String optimobileCredentials) {
            if (optimoveCredentials == null && optimobileCredentials == null) {
                throw new IllegalArgumentException("Should provide at least optimove or optimobile credentials");
            }

            this.setOptimoveCredentials(optimoveCredentials);
            this.setOptimobileCredentials(optimobileCredentials);
        }

        private void setOptimoveCredentials(@Nullable TenantInfo optimoveCredentials) {
            if (optimoveCredentials == null) {
                return;
            }

            this.optimoveToken = optimoveCredentials.getTenantToken();
            this.configFileName = optimoveCredentials.getConfigName();
        }

        private void setOptimobileCredentials(@Nullable String optimobileCredentials) {
            if (optimobileCredentials == null) {
                return;
            }

            try {
                byte[] data = Base64.decode(optimobileCredentials, Base64.DEFAULT);
                JSONArray result = new JSONArray(new String(data, StandardCharsets.UTF_8));

                String region = result.getString(1);
                this.apiKey = result.getString(2);
                this.secretKey = result.getString(3);

                this.baseUrlMap = UrlBuilder.defaultMapping(region);

            } catch (NullPointerException | JSONException | IllegalArgumentException e) {
                throw new IllegalArgumentException("Optimobile credentials are not correct");
            }
        }

        /**
         * Set up the drawable to use for the small push notification icon
         *
         * @param drawableIconId
         * @return
         */
        public Builder setPushSmallIconId(@DrawableRes int drawableIconId) {
            this.notificationSmallIconDrawableId = drawableIconId;
            return this;
        }

        public Builder enableInAppMessaging(InAppConsentStrategy strategy) {
            this.consentStrategy = strategy;
            return this;
        }

        public Builder enableDeepLinking(@NonNull String cname, DeferredDeepLinkHandlerInterface handler) {
            this.deferredDeepLinkHandler = handler;
            try {
                this.deepLinkCname = new URL(cname);
            } catch (MalformedURLException e) {
                e.printStackTrace();
                this.deepLinkCname = null;
            }

            return this;
        }

        public Builder enableDeepLinking(DeferredDeepLinkHandlerInterface handler) {
            this.deferredDeepLinkHandler = handler;
            this.deepLinkCname = null;
            return this;
        }

        /**
         * The minimum amount of time the user has to have left the app for a session end event to be
         * recorded.
         * <p>
         * The idle period starts when a pause lifecycle event is observed, and is reset when any resume
         * event is seen. If no resume event is observed and the idle period elapses, the app is considered
         * to be in the background and the session ends.
         * <p>
         * This defaults to OptimoveConfig.DEFAULT_SESSION_IDLE_TIMEOUT_SECONDS if unspecified.
         *
         * @param idleTimeSeconds
         * @return
         */
        public Builder setSessionIdleTimeoutSeconds(int idleTimeSeconds) {
            this.sessionIdleTimeoutSeconds = Math.abs(idleTimeSeconds);
            return this;
        }

        /**
         * @param minLogLevel Logcat minimum log level to show.
         */
        public Builder setMinLogLevel(LogLevel minLogLevel){
            this.minLogLevel = minLogLevel;
            return this;
        }

        /**
         * Private API
         */
        @InternalSdkEmbeddingApi(purpose = "Allow override of stats data in x-plat SDKs")
        public Builder setRuntimeInfo(JSONObject info) {
            this.runtimeInfo = info;
            return this;
        }

        /**
         * Private API
         */
        @InternalSdkEmbeddingApi(purpose = "Allow override of stats data in x-plat SDKs")
        public Builder setSdkInfo(JSONObject info) {
            this.sdkInfo = info;
            return this;
        }

        /**
         * Private API
         */
        @InternalSdkEmbeddingApi(purpose = "Allow sending traffic to different domains")
        public Builder setBaseUrlMapping(Map<UrlBuilder.Service, String> baseUrlMap) {
            this.baseUrlMap = baseUrlMap;
            return this;
        }

        public OptimoveConfig build() {
            OptimoveConfig newConfig = new OptimoveConfig();
            newConfig.setApiKey(apiKey);
            newConfig.setSecretKey(secretKey);
            newConfig.setOptimoveToken(optimoveToken);
            newConfig.setConfigFileName(configFileName);
            newConfig.setNotificationSmallIconId(notificationSmallIconDrawableId);
            newConfig.setSessionIdleTimeoutSeconds(sessionIdleTimeoutSeconds);
            newConfig.setRuntimeInfo(this.runtimeInfo);
            newConfig.setSdkInfo(this.sdkInfo);
            newConfig.setBaseUrlMap(this.baseUrlMap);

            newConfig.setInAppConsentStrategy(consentStrategy);

            newConfig.setCname(this.deepLinkCname);
            newConfig.setDeferredDeepLinkHandler(this.deferredDeepLinkHandler);

            newConfig.setMinLogLevel(this.minLogLevel);

            return newConfig;
        }
    }
}
