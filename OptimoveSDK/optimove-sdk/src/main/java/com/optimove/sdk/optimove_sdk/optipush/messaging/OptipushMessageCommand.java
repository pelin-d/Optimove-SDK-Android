package com.optimove.sdk.optimove_sdk.optipush.messaging;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;

import com.android.volley.VolleyError;
import com.google.firebase.messaging.RemoteMessage;
import com.optimove.sdk.optimove_sdk.main.event_handlers.EventHandler;
import com.optimove.sdk.optimove_sdk.main.events.OptimoveEvent;
import com.optimove.sdk.optimove_sdk.main.events.core_events.notification_events.ScheduledNotificationDeliveredEvent;
import com.optimove.sdk.optimove_sdk.main.events.core_events.notification_events.TriggeredNotificationDeliveredEvent;
import com.optimove.sdk.optimove_sdk.main.tools.DeviceInfoProvider;
import com.optimove.sdk.optimove_sdk.main.tools.OptiUtils;
import com.optimove.sdk.optimove_sdk.main.tools.networking.HttpClient;
import com.optimove.sdk.optimove_sdk.main.tools.opti_logger.OptiLogger;
import com.optimove.sdk.optimove_sdk.main.tools.opti_logger.OptiLoggerStreamsContainer;
import com.optimove.sdk.optimove_sdk.optipush.OptipushConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import static android.content.Context.NOTIFICATION_SERVICE;

public class OptipushMessageCommand {

    private Context context;
    private EventHandler eventHandler;
    private DeviceInfoProvider deviceInfoProvider;
    private NotificationCreator notificationCreator;
    private String fullPackageName;

    public OptipushMessageCommand(Context context, EventHandler eventHandler,
                                  DeviceInfoProvider deviceInfoProvider, NotificationCreator notificationCreator) {
        this.context = context;
        this.eventHandler = eventHandler;
        this.deviceInfoProvider = deviceInfoProvider;
        this.notificationCreator = notificationCreator;
        this.fullPackageName = context.getPackageName();
    }

    public void processRemoteMessage(RemoteMessage remoteMessage,
                                     NotificationData notificationData) {
        if (context.getSystemService(NOTIFICATION_SERVICE) == null) {
            OptiLogger.optipushFailedToProcessNotification_WhenNotificationManagerIsNull();
            return;
        }

        if (notificationData.getScheduledCampaign() != null) {
            eventHandler.reportEvent(Collections.singletonList(new ScheduledNotificationDeliveredEvent(OptiUtils.currentTimeSeconds(), fullPackageName,
                    notificationData.getScheduledCampaign())));
        } else if (notificationData.getTriggeredCampaign() != null) {
            eventHandler.reportEvent(Collections.singletonList(new TriggeredNotificationDeliveredEvent(OptiUtils.currentTimeSeconds(), fullPackageName,
                    notificationData.getTriggeredCampaign())));
        }


        loadDynamicLinkAndShowNotification(remoteMessage, notificationData);
    }


    /* ******************************
     * Deep Linking
     * ******************************/

    /**
     * Check if a dynamic link exists in the push data and extract the full URL.
     */
    private void loadDynamicLinkAndShowNotification(RemoteMessage remoteMessage, NotificationData notificationData) {
        String dlString = remoteMessage.getData()
                .get(OptipushConstants.PushSchemaKeys.DYNAMIC_LINKS_NEW);
        if (dlString == null) {
            showNotificationIfUserIsOptIn(notificationData);
            return;
        }
        String deepLinkPersonalizationValues = remoteMessage.getData()
                .get(OptipushConstants.PushSchemaKeys.DEEP_LINK_PERSONALIZATION_VALUES_NEW);
        if (deepLinkPersonalizationValues != null) {
            dlString = getPersonalizedDeepLink(dlString, deepLinkPersonalizationValues);
        }
        notificationData.setDynamicLink(dlString);
    }

    @Nullable
    private String extractDeepLinkFromRedirectionError(@NonNull VolleyError volleyError) {
        if (volleyError.networkResponse == null || volleyError.networkResponse.headers == null) {
            OptiLoggerStreamsContainer.error("Dynamic link extraction failed due to corrupted networkResponse - %s",
                    volleyError.getMessage());
            return null;
        }
        String redirectionNewLocation = volleyError.networkResponse.headers.get("Location");
        if (redirectionNewLocation != null) {
            try {
                String deepLinkParamsEncoded =
                        redirectionNewLocation.substring(redirectionNewLocation.lastIndexOf("url=") + 4);
                return java.net.URLDecoder.decode(deepLinkParamsEncoded,
                        StandardCharsets.UTF_8.name());
            } catch (UnsupportedEncodingException e) {
                OptiLoggerStreamsContainer.error("Dynamic link params extraction error - %s",
                        e.getMessage());
            } catch (NullPointerException e) {
                OptiLoggerStreamsContainer.error("Dynamic link params extraction error - %s",
                        e.getMessage());
            } catch (Exception e) {
                OptiLoggerStreamsContainer.error("Dynamic link params extraction error - %s",
                        e.getMessage());
            }
        }
        return null;
    }

    @VisibleForTesting
    private String getPersonalizedDeepLink(String deepLink, String deepLinkPersonalizationValues) {
        try {
            JSONObject json = new JSONObject(deepLinkPersonalizationValues);
            Iterator<String> keys = json.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                if (!json.isNull(key)) {
                    deepLink =
                            deepLink.replace(URLEncoder.encode(key, "utf8"), URLEncoder.encode(json.getString(key), "utf8"));
                }
            }
        } catch (JSONException e) {
            OptiLogger.optipushDeepLinkPersonalizationValuesBadJson();
        } catch (UnsupportedEncodingException e) {
            OptiLogger.optipushDeepLinkFailedToDecodeLinkParam();
        }
        return deepLink;
    }


    /* ******************************
     * Notification Builder
     * ******************************/

    private void showNotificationIfUserIsOptIn(NotificationData notificationData) {
        boolean optIn = deviceInfoProvider.notificaionsAreEnabled();
        if (!optIn) {
            OptiLogger.optipushNotificationNotPresented_WhenUserIdOptOut();
            return;
        }
        notificationCreator.showNotification(notificationData);
    }


}
