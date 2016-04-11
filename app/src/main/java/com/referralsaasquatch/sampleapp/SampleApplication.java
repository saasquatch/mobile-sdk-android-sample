package com.referralsaasquatch.sampleapp;

import android.app.Application;

import com.wholepunk.saasquatch.Saasquatch;

public class SampleApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Saasquatch.initialize(getApplicationContext());
    }
}