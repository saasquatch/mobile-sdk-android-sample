package com.referralsaasquatch.sampleapp;

import android.app.Application;

import com.wholepunk.saasquatch.Saasquatch;

/**
 * Created by brendancrawford on 2016-03-24.
 */
public class SampleApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Saasquatch.initialize(getApplicationContext());
    }
}
