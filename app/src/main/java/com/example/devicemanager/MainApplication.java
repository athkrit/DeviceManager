package com.example.devicemanager;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.example.devicemanager.manager.Contextor;

import io.fabric.sdk.android.Fabric;


public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        Contextor.getInstance().init(getApplicationContext());
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
