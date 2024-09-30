package com.optimove.android.optimovemobilesdk;

public class EventReport {

    public static void runFromWorker(Runnable runnable) {
        new Thread(runnable).start();
    }

}
