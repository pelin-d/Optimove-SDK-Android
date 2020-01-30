package com.optimove.sdk.optimove_sdk.optipush.messaging;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;
import com.optimove.sdk.optimove_sdk.optipush.campaigns.ScheduledCampaign;
import com.optimove.sdk.optimove_sdk.optipush.campaigns.TriggeredCampaign;

public class NotificationData {

    @SerializedName("title")
    private String title;
    @SerializedName("content")
    private String body;
    @Nullable
    private String dynamicLink;
    @SerializedName("triggered_campaign")
    @Nullable
    private TriggeredCampaign triggeredCampaign;
    @SerializedName("scheduled_campaign")
    @Nullable
    private ScheduledCampaign scheduledCampaign;
    @SerializedName("collapse_Key")
    @Nullable
    private String collapseKey;

    @SerializedName("media")
    @Nullable
    private NotificationMedia notificationMedia;

    @NonNull
    @Override
    public String toString() {
        String campaign = "";
        if (scheduledCampaign != null){
            campaign = scheduledCampaign.toString();
        } else if (triggeredCampaign != null){
            campaign = triggeredCampaign.toString();
        }
        return "NotificationData{ " + "title='" + title + '\'' +
                ", body='" + body + '\'' +
                ", dynamicLink='" + dynamicLink + '\'' +
                ", campaign=" + campaign +
                ", collapseKey='" + collapseKey + '\'' +
                '}';
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Nullable
    public String getDynamicLink() {
        return dynamicLink;
    }

    public void setDynamicLink(@Nullable String dynamicLink) {
        this.dynamicLink = dynamicLink;
    }

    @Nullable
    public TriggeredCampaign getTriggeredCampaign() {
        return triggeredCampaign;
    }

    public void setTriggeredCampaign(@Nullable TriggeredCampaign triggeredCampaign) {
        this.triggeredCampaign = triggeredCampaign;
    }

    @Nullable
    public String getCollapseKey() {
        return collapseKey;
    }

    public void setCollapseKey(@Nullable String collapseKey) {
        this.collapseKey = collapseKey;
    }

    @Nullable
    public NotificationMedia getNotificationMedia() {
        return notificationMedia;
    }

    public void setNotificationMedia(@Nullable NotificationMedia notificationMedia) {
        this.notificationMedia = notificationMedia;
    }

    @Nullable
    public ScheduledCampaign getScheduledCampaign() {
        return scheduledCampaign;
    }

    public void setScheduledCampaign(@Nullable ScheduledCampaign scheduledCampaign) {
        this.scheduledCampaign = scheduledCampaign;
    }
}