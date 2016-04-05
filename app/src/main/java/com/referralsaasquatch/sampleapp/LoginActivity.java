package com.referralsaasquatch.sampleapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.wholepunk.saasquatch.Saasquatch;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends Activity {

    private User mUser = User.getInstance();
    private String mTenant = "acunqvcfij2l4";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void login(View loginButton) {
        EditText emailField = (EditText) findViewById(R.id.login_textfield_email);
        EditText passwordField = (EditText) findViewById(R.id.login_textfield_password);
        String emailValue = emailField.getText().toString();
        String passwordValue = passwordField.getText().toString();

        if (emailValue.equals("bob") && passwordValue.equals("bob")) {

            // Get Bob's info
            final String userId = "876343";
            final String accountId = "613611";
            final String secret = "038tr0810t8h1028th108102085180";

            // Lookup Bob with Referral SaaSquatch
            Saasquatch.getUser(mTenant, userId, accountId, secret,
                    new Saasquatch.FetchContextCompleteListener() {
                        @Override
                        public void onComplete(JSONObject userInfo, String errorMessage, Integer errorCode) {

                            if (errorMessage != null) {
                                // Show an alert describing the error
                                new AlertDialog.Builder(LoginActivity.this)
                                        .setTitle("Login Error")
                                        .setMessage(errorMessage)
                                        .setPositiveButton("OK", null)
                                        .show();
                                return;
                            }

                            // Parse the returned context
                            String email;
                            String firstName;
                            String lastName;
                            String referralCode;
                            try {
                                email = userInfo.getString("email");
                                firstName = userInfo.getString("firstName");
                                lastName = userInfo.getString("lastName");
                                referralCode = userInfo.getString("referralCode");
                            } catch (JSONException e) {
                                // Show an alert describing the error
                                return;
                            }

                            // Login Bob
                            mUser.login(secret, userId, accountId, firstName, lastName, email, referralCode);

                            // Validate Bob's referral code and give him his reward
                            Saasquatch.validateReferralCode(mTenant, referralCode, secret, new Saasquatch.FetchContextCompleteListener() {
                                @Override
                                public void onComplete(JSONObject userInfo, String errorMessage, Integer errorCode) {

                                    if (errorCode != null) {
                                        if (errorCode.equals(401)) {
                                            // The secret was not the same as registered
                                            showRegistrationErrorAlert(errorMessage);
                                        } else if (errorCode.equals(404)) {
                                            // The referral code was not found
                                            showRegistrationErrorAlert("Invalid referral code.\nPlease check your code and try again.");
                                        } else {
                                            showRegistrationErrorAlert(null);
                                        }
                                        return;
                                    }

                                    // Parse the returned info
                                    String code;
                                    String rewardString = "";
                                    String type;
                                    JSONObject reward;
                                    try {
                                        code = userInfo.getString("code");
                                        reward = userInfo.getJSONObject("reward");
                                        type = reward.getString("type");
                                    } catch (JSONException e) {
                                        showRegistrationErrorAlert("Something went wrong with your referral code.");
                                        return;
                                    }

                                    // Parse the reward info
                                    try {
                                        if (type.equals("PCT_DISCOUNT")) {
                                            Integer percent = reward.getInt("discountPercent");
                                            rewardString = percent.toString() + "% off your next SaaS";
                                        } else {
                                            String unit = reward.getString("unit");

                                            if (type.equals("FEATURE")) {
                                                rewardString = "You get a " + unit;
                                            } else { // type == "TIME_CREDIT or type == "CREDIT"
                                                Integer credit = reward.getInt("credit");
                                                rewardString = credit.toString() + " " + unit + " off your next SaaS";
                                            }
                                        }
                                    } catch (JSONException e) {
                                        showRegistrationErrorAlert("Something went wrong with your referral code.");
                                        return;
                                    }

                                    // Give Bob his referral reward
                                    mUser.addReward(code, rewardString);

                                    // Head to welcome screen
                                    Intent intent = new Intent(LoginActivity.this, WelcomeActivity.class);
                                    startActivity(intent);
                                }
                            });
                        }
                    });
        } else {
            Intent intent = new Intent(this, SignupActivity.class);
            startActivity(intent);
        }
    }

    private void showRegistrationErrorAlert(String message) {
        String errorMessage = "Something went wrong with your login\n" + "Please check your details and try again.";
        if (message != null) {
            errorMessage = message;
        }
        new AlertDialog.Builder(this)
                .setTitle("Registration Error")
                .setMessage(errorMessage)
                .setPositiveButton("OK", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public void signup(View signupButton) {
        Intent intent = new Intent(this, SignupActivity.class);
        startActivity(intent);
    }
}
