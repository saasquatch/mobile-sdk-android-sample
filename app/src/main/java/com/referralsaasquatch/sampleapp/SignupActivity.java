package com.referralsaasquatch.sampleapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.wholepunk.saasquatch.Saasquatch;
import com.auth0.jwt.*;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class SignupActivity extends Activity {

    private User mUser = User.getInstance();
    private String mTenant = "acunqvcfij2l4";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        Intent intent = getIntent();
        String referralCode = null;
        if (intent != null) {
            Uri uri = intent.getData();
            if (uri != null) {
                referralCode = uri.getQueryParameter("referralCode");
            }
        }
        final EditText referralCodeField = (EditText) findViewById(R.id.signup_textfield_referralcode);
        if (referralCode != null) {
            referralCodeField.setText(referralCode, TextView.BufferType.EDITABLE);
        }

        final TextView rewardView = (TextView) findViewById(R.id.signup_textview_rewardlabel);
        rewardView.setVisibility(View.GONE);
        referralCodeField.setCompoundDrawablesWithIntrinsicBounds(0, 0, android.R.drawable.checkbox_off_background, 0);

        // Set up a listener to check the referral code as it is entered
        referralCodeField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {

                Saasquatch.lookupReferralCode(mTenant, s.toString(), mUser.token, SignupActivity.this,
                        new Saasquatch.TaskCompleteListener() {
                            @Override
                            public void onComplete(JSONObject userInfo, String errorMessage, Integer errorCode) {

                                if (errorCode != null) {
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
                                        } else { // type is "TIME_CREDIT or "CREDIT"
                                            Integer credit = reward.getInt("credit");
                                            rewardString = credit.toString() + " " + unit + " off your next SaaS";
                                        }
                                    }
                                } catch (JSONException e) {
                                    return;
                                }

                                rewardView.setVisibility(View.VISIBLE);
                                rewardView.setText(rewardString);
                                referralCodeField.setCompoundDrawablesWithIntrinsicBounds(0, 0, android.R.drawable.checkbox_on_background, 0);
                            }
                        });
            }
        });
    }

    public void signup(View signupButton) {
        EditText firstName = (EditText) findViewById(R.id.signup_textfield_firstname);
        EditText lastName = (EditText) findViewById(R.id.signup_textfield_lastname);
        EditText email = (EditText) findViewById(R.id.signup_textfield_email);
        EditText password = (EditText) findViewById(R.id.signup_textfield_password);
        final EditText referralCode = (EditText) findViewById(R.id.signup_textfield_referralcode);
        String firstNameValue = firstName.getText().toString();
        String lastNameValue = lastName.getText().toString();
        String emailValue = email.getText().toString();
        String passwordValue = password.getText().toString();
        final String referralCodeValue = referralCode.getText().toString();

        if (!validateFields()) {
            return;
        }

        JSONObject userInfo = createUser(firstNameValue, lastNameValue, emailValue);

        // Register the user with Referral SaaSquatch
        Saasquatch.registerUser(mTenant, mUser.userId, mUser.accountId, mUser.token, userInfo, this,
                new Saasquatch.TaskCompleteListener() {
                    @Override
                    public void onComplete(JSONObject userInfo, String errorMessage, Integer errorCode) {

                        if (errorCode != null) {
                            // Show an alert describing the error
                            showRegistrationErrorAlert(errorMessage);
                            return;
                        }

                        // Parse the returned information
                        String shareLink;
                        String facebookShareLink;
                        String twitterShareLink;
                        JSONObject shareLinksJSON;

                        try {
                            shareLinksJSON = userInfo.getJSONObject("shareLinks");
                        } catch (JSONException e) {
                            // Show an alert describing the error
                            showRegistrationErrorAlert("Registration error.\nPlease try again.");
                            return;
                        }

                        try {
                            shareLink = shareLinksJSON.getString("shareLink");
                            facebookShareLink = shareLinksJSON.getString("mobileFacebookShareLink");
                            twitterShareLink = shareLinksJSON.getString("mobileTwitterShareLink");
                        } catch (JSONException e) {
                            // Show an alert describing the error
                            showRegistrationErrorAlert("Registration error.\nPlease try again.");
                            return;
                        }

                        HashMap<String, String> shareLinks = new HashMap<String, String>();
                        shareLinks.put("shareLink", shareLink);
                        shareLinks.put("facebook", facebookShareLink);
                        shareLinks.put("twitter", twitterShareLink);

                        // Set share links
                        mUser.shareLinks = shareLinks;

                        // Apply the referral code
                        Saasquatch.applyReferralCode(mTenant, mUser.userId, mUser.accountId, referralCodeValue, mUser.token, SignupActivity.this,
                                new Saasquatch.TaskCompleteListener() {
                                    @Override
                                    public void onComplete(JSONObject userInfo, String errorMessage, Integer errorCode) {

                                        if (errorCode != null) {
                                            if (errorCode.equals(401)) {
                                                // The token was not the same as registered
                                                showRegistrationErrorAlert(errorMessage);
                                            } else if (errorCode.equals(404)) {
                                                // The referral code was not found
                                                showRegistrationErrorAlert("Invalid referral code.\nPlease check your code and try again.");
                                                referralCode.setBackground(ContextCompat.getDrawable(SignupActivity.this, R.drawable.button_borderred));
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
                                                } else { // type is "TIME_CREDIT or "CREDIT"
                                                    Integer credit = reward.getInt("credit");
                                                    rewardString = credit.toString() + " " + unit + " off your next SaaS";
                                                }
                                            }
                                        } catch (JSONException e) {
                                            showRegistrationErrorAlert("Something went wrong with your referral code.");
                                            return;
                                        }
                                        final String rewardStr = rewardString;

                                        // Lookup the person that referred user
                                        Saasquatch.getUserByReferralCode(mTenant, referralCodeValue, mUser.token, SignupActivity.this,
                                                new Saasquatch.TaskCompleteListener() {
                                                    @Override
                                                    public void onComplete(JSONObject userInfo, String errorMessage, Integer errorCode) {

                                                        if (errorCode != null) {
                                                            if (errorCode.equals(401)) {
                                                                // The token was not the same as registered
                                                                showRegistrationErrorAlert(errorMessage);
                                                            } else if (errorCode.equals(404)) {
                                                                // The user associated with the referral code was not found
                                                                showRegistrationErrorAlert("Invalid referral code.\nPlease check your code and try again.");
                                                                referralCode.setBackground(ContextCompat.getDrawable(SignupActivity.this, R.drawable.button_borderred));
                                                            } else {
                                                                showRegistrationErrorAlert(null);
                                                            }
                                                            return;
                                                        }

                                                        // Parse the userInfo
                                                        String referrerFirstName;
                                                        String referrerLastInitial;
                                                        try {
                                                            referrerFirstName = userInfo.getString("firstName");
                                                            referrerLastInitial = userInfo.getString("lastInitial");
                                                        } catch (JSONException e) {
                                                            showRegistrationErrorAlert("Something went wrong with your referral code.");
                                                            return;
                                                        }

                                                        showReferralDialog(referrerFirstName, referrerLastInitial, rewardStr);
                                                    }
                                                });
                                    }
                                });

                    }
                });
    }

    private Boolean validateFields() {
        Boolean result = true;

        EditText firstName = (EditText) findViewById(R.id.signup_textfield_firstname);
        EditText lastName = (EditText) findViewById(R.id.signup_textfield_lastname);
        EditText email = (EditText) findViewById(R.id.signup_textfield_email);
        EditText password = (EditText) findViewById(R.id.signup_textfield_password);
        EditText repeatPassword = (EditText) findViewById(R.id.signup_textfield_repeatpassword);
        EditText referralCode = (EditText) findViewById(R.id.signup_textfield_referralcode);
        String firstNameValue = firstName.getText().toString();
        String lastNameValue = lastName.getText().toString();
        String emailValue = email.getText().toString();
        String passwordValue = password.getText().toString();
        String repeatPasswordValue = repeatPassword.getText().toString();
        String referralCodeValue = referralCode.getText().toString();

        if (firstNameValue.equals("")) {
            firstName.setBackground(ContextCompat.getDrawable(this, R.drawable.button_borderred));
            result = false;
        }
        if (lastNameValue.equals("")) {
            lastName.setBackground(ContextCompat.getDrawable(this, R.drawable.button_borderred));
            result = false;
        }
        if (emailValue.equals("")) {
            email.setBackground(ContextCompat.getDrawable(this, R.drawable.button_borderred));
            result = false;
        }
        if (passwordValue.equals("")) {
            password.setBackground(ContextCompat.getDrawable(this, R.drawable.button_borderred));
            result = false;
        }
        if (referralCodeValue.equals("")) {
            referralCode.setBackground(ContextCompat.getDrawable(this, R.drawable.button_borderred));
            result = false;
        }
        if (!passwordValue.equals(repeatPasswordValue)) {
            password.setBackground(ContextCompat.getDrawable(this, R.drawable.button_borderred));
            repeatPassword.setBackground(ContextCompat.getDrawable(this, R.drawable.button_borderred));
            result = false;
        }

        return result;
    }

    private JSONObject createUser(String firstName, String lastName, String email) {
        Random rand = new Random();
        String userId = String.valueOf(rand.nextInt());
        String accountId = String.valueOf(rand.nextInt());
        String locale = "en_US";
        String referralCode = firstName.toUpperCase() + lastName.toUpperCase();

        JSONObject result = new JSONObject();
        try {
            result.put("id", userId);
            result.put("accountId", accountId);
            result.put("email", email);
            result.put("firstName", firstName);
            result.put("lastName", lastName);
            result.put("locale", locale);
            result.put("referralCode", referralCode);
            result.put("imageUrl", "");
        } catch (JSONException e) {
            showRegistrationErrorAlert(null);
        }

        HashMap<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", userId);
        userInfo.put("accountId", accountId);
        userInfo.put("email", email);
        userInfo.put("firstName", firstName);
        userInfo.put("lastName", lastName);
        userInfo.put("locale", locale);
        userInfo.put("referralCode", referralCode);
        userInfo.put("imageUrl", "");

        JWTSigner signer = new JWTSigner("LIVE_WxIp37Pbgmt9jnxDmZvVIOf13OLg0k9F");
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", accountId + "_" + userId);
        claims.put("user", userInfo);
        JWTSigner.Options options = new JWTSigner.Options();
        options.setAlgorithm(Algorithm.HS256);
        String token = signer.sign(claims, options);

        mUser.login(token, userId, accountId, firstName, lastName, email, referralCode, null);
        Log.w("TOKEN", token);

        return result;
    }

    private void showRegistrationErrorAlert(String message) {
        String errorMessage = "Something went wrong with your sign up\n" + "Please check your details and try again.";
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

    private void showReferralDialog(String firstName, String lastInitial, String reward) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater layoutInflater = this.getLayoutInflater();
        View dialogView = layoutInflater.inflate(R.layout.referral_dialog, null);
        Dialog dialog = builder.setView(dialogView)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(SignupActivity.this, WelcomeActivity.class);
                        startActivity(intent);
                    }
                })
                .create();

        TextView referred = (TextView) dialogView.findViewById(R.id.reward_textview_referred);
        TextView rewardString = (TextView) dialogView.findViewById(R.id.reward_textview_rewardstring);
        String referredString = "You've been referred by\n" + firstName + " " + lastInitial;
        referred.setText(referredString);
        rewardString.setText(reward);

        dialog.show();
    }
}
