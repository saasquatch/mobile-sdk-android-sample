package com.referralsaasquatch.sampleapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.wholepunk.saasquatch.Saasquatch;

import org.json.JSONObject;
import org.w3c.dom.Text;

public class WelcomeActivity extends Activity {

    private User mUser = User.getInstance();
    private User.Reward mReward;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        TextView welcome = (TextView) findViewById(R.id.welcome_textview_welcome);
        TextView reward = (TextView) findViewById(R.id.welcome_textview_reward);

        String welcomeString = "Welcome, " + mUser.firstName;
        welcome.setText(welcomeString);
        if (mUser.rewards.isEmpty()) {
            reward.setText("You have no rewards.");
        } else {
            mReward = mUser.rewards.pop();
            reward.setText(mReward.reward);
        }
    }
}
