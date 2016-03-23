package com.referralsaasquatch.sampleapp;

import android.app.Activity;
import android.os.Bundle;

public class WelcomeActivity extends Activity {

    private User mUser = User.getInstance();
    private String mTenant = "SaaS";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
    }
}
