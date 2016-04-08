/*
Cloud icon by https://www.iconfinder.com/aha-soft is licensed under http://creativecommons.org/licenses/by/3.0/
 */

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

import java.util.HashMap;

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

        if (emailValue.equals("email") && passwordValue.equals("password")) {

            // Get Claire's info
            final String userId = "10001110101";
            final String accountId = "10001110101";
            final String secret = "978-0440212560";

            // Lookup Claire with Referral SaaSquatch
            Saasquatch.getUser(mTenant, userId, accountId, secret, this,
                    new Saasquatch.FetchTaskCompleteListener() {
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

                            // Parse the returned data
                            String email;
                            String firstName;
                            String lastName;
                            String referralCode;
                            String shareLink;
                            String facebookShareLink;
                            String twitterShareLink;
                            JSONObject shareLinksJSON;

                            try {
                                email = userInfo.getString("email");
                                firstName = userInfo.getString("firstName");
                                lastName = userInfo.getString("lastName");
                                referralCode = userInfo.getString("referralCode");
                                shareLinksJSON = userInfo.getJSONObject("shareLinks");
                            } catch (JSONException e) {
                                // Show an alert describing the error
                                showRegistrationErrorAlert("Login error.\nPlease try again.");
                                return;
                            }

                            try {
                                shareLink = shareLinksJSON.getString("shareLink");
                                facebookShareLink = shareLinksJSON.getString("mobileFacebookShareLink");
                                twitterShareLink = shareLinksJSON.getString("mobileTwitterShareLink");
                            } catch (JSONException e) {
                                // Show an alert describing the error
                                showRegistrationErrorAlert("Login error.\nPlease try again.");
                                return;
                            }

                            HashMap<String, String> shareLinks = new HashMap<String, String>();
                            shareLinks.put("shareLink", shareLink);
                            shareLinks.put("facebook", facebookShareLink);
                            shareLinks.put("twitter", twitterShareLink);

                            // Login Claire
                            mUser.login(secret, userId, accountId, firstName, lastName, email, referralCode, shareLinks);

                            // Validate Claire's referral code and give her reward to her
                            Saasquatch.validateReferralCode(mTenant, referralCode, secret, LoginActivity.this, new Saasquatch.FetchTaskCompleteListener() {
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

                                    // Give Claire her referral reward
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
