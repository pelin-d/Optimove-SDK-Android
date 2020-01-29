package com.optimove.sdk.optimove_sdk.main.app_update_listener;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;

import com.optimove.sdk.optimove_sdk.main.tools.opti_logger.OptiLogger;

/**
 * When the hosting Application was {@code updated}, this service is notified and ensures a first background silent initialization of the SDK
 */
public class AppUpdateService extends JobIntentService {

  private static final int JOB_ID = 10000;

  /**
   * Convenience method for enqueuing work in to this service.
   */
  static void enqueueWork(Context context, Intent work) {
    enqueueWork(context, AppUpdateService.class, JOB_ID, work);
  }

  @Override
  protected void onHandleWork(@NonNull Intent intent) {
    OptiLogger.f125();
  }
}