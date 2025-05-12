package com.javanostra.meetyourmatch;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.javanostra.meetyourmatch.persistance.ServerPinger;

public class MeetYourMatchApp extends Application implements Application.ActivityLifecycleCallbacks {
    private boolean isAppInBackground;

    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(this);
        isAppInBackground = false;
    }


    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {

    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {

    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        isAppInBackground = false;
        ServerPinger.getInstance(activity).startPinging();
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {
        isAppInBackground = true;
        ServerPinger.getInstance(activity).stopPinging();
    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {

    }

    public boolean isAppInBackground() {
        return isAppInBackground;
    }
}
