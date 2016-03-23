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
    private String mTenant = "SaaS";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        TextView welcome = (TextView) findViewById(R.id.welcome_textview_welcome);
        TextView reward = (TextView) findViewById(R.id.welcome_textview_reward);
        Button claimButton = (Button) findViewById(R.id.welcome_textview_claimbutton);

        String welcomeString = "Welcome, " + mUser.firstName;
        welcome.setText(welcomeString);
        if (mUser.rewards.isEmpty()) {
            reward.setText("You have no rewards to claim.");
            claimButton.setEnabled(false);
        } else {
            mReward = mUser.rewards.pop();
            reward.setText(mReward.reward);
        }
    }

    private void claimReward(View claimButton) {
        final String referralCode = mReward.code;

        // Validate the code with Referral SaaSquatch
        Saasquatch.validateReferralCode(mTenant, referralCode, mUser.secret,
                new Saasquatch.FetchContextCompleteListener() {
                    @Override
                    public void onComplete(JSONObject userInfo, String errorMessage, Integer errorCode) {

                        if (errorCode != null) {
                            // Show an alert describing the error
                            AlertDialog dialog = new AlertDialog.Builder(WelcomeActivity.this)
                                    .setPositiveButton("OK", null)
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .create();
                            if (errorCode.equals(401)) {
                                // The secret was not the same as registered
                                dialog.setTitle("Error");
                                dialog.setMessage(errorMessage);
                            } else if (errorCode.equals(404)) {
                                // The referral code was not found
                                dialog.setTitle("Invalid code");
                                dialog.setMessage("Your referral code is not valid");
                            } else {
                                dialog.setTitle("Unknown Error");
                                dialog.setMessage(errorMessage);
                            }
                            dialog.show();
                            return;
                        }

                        // Apply the referral code to the user's account
                        Saasquatch.applyReferralCode(mTenant, mUser.userId, mUser.accountId, referralCode, mUser.secret, WelcomeActivity.this);

                        // Let the user know their code has been applied successfully
                        new AlertDialog.Builder(WelcomeActivity.this)
                                .setTitle("Success!")
                                .setMessage("Your discount has been applied.")
                                .show();
                    }
                });
    }
}
