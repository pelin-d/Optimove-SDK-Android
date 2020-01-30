package com.optimove.sdk.optimove_sdk.optipush.registration;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Set;

import static android.content.Context.MODE_PRIVATE;
import static com.optimove.sdk.optimove_sdk.optipush.OptipushConstants.Registration.DEVICE_ID_KEY;
import static com.optimove.sdk.optimove_sdk.optipush.OptipushConstants.Registration.FAILED_USER_IDS_KEY;
import static com.optimove.sdk.optimove_sdk.optipush.OptipushConstants.Registration.LAST_NOTIFICATION_PERMISSION_STATUS;
import static com.optimove.sdk.optimove_sdk.optipush.OptipushConstants.Registration.LAST_TOKEN_KEY;
import static com.optimove.sdk.optimove_sdk.optipush.OptipushConstants.Registration.REGISTRATION_PREFERENCES_NAME;
import static com.optimove.sdk.optimove_sdk.optipush.OptipushConstants.Registration.SET_USER_FAILED_KEY;
import static com.optimove.sdk.optimove_sdk.optipush.OptipushConstants.Registration.TOKEN_REFRESH_FAILED_KEY;

/**
 * Manages access to every locally stored data relevant to registration operations
 */
public final class RegistrationDao {

  private SharedPreferences registrationPreferences;

  public RegistrationDao(Context context) {
    registrationPreferences = context.getSharedPreferences(REGISTRATION_PREFERENCES_NAME, MODE_PRIVATE);
  }

  public boolean wasTheUserOptIn() {
    return registrationPreferences.getBoolean(LAST_NOTIFICATION_PERMISSION_STATUS, true);
  }

  @Nullable
  public String getLastToken() {
    return registrationPreferences.getString(LAST_TOKEN_KEY, null);
  }
  @Nullable
  public Set<String> getFailedUserAliases() {
    return registrationPreferences.getStringSet(FAILED_USER_IDS_KEY, null);
  }

  public boolean isTokenRefreshMarkedAsFailed() {
    return registrationPreferences.getBoolean(TOKEN_REFRESH_FAILED_KEY, false);
  }
  public boolean isSetUserMarkedAsFailed() {
    return registrationPreferences.getBoolean(SET_USER_FAILED_KEY, false);
  }
  @Nullable
  public String getDeviceId() {
    return registrationPreferences.getString(DEVICE_ID_KEY, null);
  }

  public FlagsEditor editFlags() {
    return new FlagsEditor();
  }

  public class FlagsEditor {

    private SharedPreferences.Editor editor;

    @SuppressLint("CommitPrefEdits")
    private FlagsEditor() {
      this.editor = registrationPreferences.edit();
    }

    public FlagsEditor putNewToken(String newToken) {
      editor.putString(LAST_TOKEN_KEY, newToken);
      return this;
    }

    public FlagsEditor updateLastOptInStatus(boolean optIn) {
      editor.putBoolean(LAST_NOTIFICATION_PERMISSION_STATUS, optIn);
      return this;
    }

    public FlagsEditor markAddUserAliasesAsFailed(@NonNull  Set<String> failedUserIds) {
      editor.putStringSet(FAILED_USER_IDS_KEY, failedUserIds);
      return this;
    }
    public FlagsEditor unmarkAddUserAliaseAsFailed() {
      editor.remove(FAILED_USER_IDS_KEY);
      return this;
    }

    public FlagsEditor markTokenRefreshAsFailed() {
      editor.putBoolean(TOKEN_REFRESH_FAILED_KEY, true);
      return this;
    }
    public FlagsEditor unmarkTokenRefreshAsFailed() {
      editor.remove(TOKEN_REFRESH_FAILED_KEY);
      return this;
    }
    public FlagsEditor setDeviceId(String deviceId) {
      editor.putString(DEVICE_ID_KEY, deviceId);
      return this;
    }
    public FlagsEditor markSetUserAsFailed() {
      editor.putBoolean(SET_USER_FAILED_KEY, true);
      return this;
    }

    public FlagsEditor unmarkSetUserAsFailed() {
      editor.remove(SET_USER_FAILED_KEY);
      return this;
    }

    public void save() {
      this.editor.apply();
    }
  }
}