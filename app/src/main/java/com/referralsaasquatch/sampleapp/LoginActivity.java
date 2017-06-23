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

import com.auth0.jwt.Algorithm;
import com.auth0.jwt.JWTSigner;
import com.wholepunk.saasquatch.Saasquatch;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class LoginActivity extends Activity {

    private User mUser = User.getInstance();

    // Insert your tenant alias below
    // ie. let tenant = "test_alqzo6fwdqqw63v4bw"
    private String mTenant = "TENANT_ALIAS_HERE";


    // Insert your API key below
    /* ie.
     let raw_token = "TEST_j0aWxsvRedKkBo5Gv1l9ispXIfsos2CsdeeIL3"
     */
    private String raw_token = "ADD_JWT_HERE";


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

    // Creating a test user
        JSONObject userInfo = createUser("saasquatch", "android", "test@referralsaasquatch.com");
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
                    }
                });
    }

    /**
     * This method is used when the login Button is pressed
     * @param loginButton The button pressed when a user wishes to login
     */
    public void login(View loginButton) {
        EditText emailField = (EditText) findViewById(R.id.login_textfield_email);
        EditText passwordField = (EditText) findViewById(R.id.login_textfield_password);
        String emailValue = emailField.getText().toString();
        String passwordValue = passwordField.getText().toString();

        if (emailValue.equals("email") && passwordValue.equals("password")) {

            // Get Claire's info
            final String userId = "10001110101";
            final String accountId = "10001110101";
            final String token = "978-0440212560";

            // Lookup Claire with Referral SaaSquatch
            Saasquatch.getUser(mTenant, userId, accountId, token, this,
                    new Saasquatch.TaskCompleteListener() {
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

                            // Login test user
                            mUser.login(token,raw_token ,userId, accountId, firstName, lastName, email, referralCode, mTenant,shareLinks);

                            // Head to welcome screen
                            Intent intent = new Intent(LoginActivity.this, WelcomeActivity.class);
                            startActivity(intent);
                        }
                    });
        } else {
            Intent intent = new Intent(this, SignupActivity.class);
            startActivity(intent);
        }
    }


    /**
     * This method is used to create a JSON object for the new user
     *
     * @param firstName The first name of the new sign up
     * @param lastName The last name of the new sign up
     * @param email The email of the new sign up
     * @return A JSON object containing the users information
     */
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


        // NOTE: Add in your API Key
        JWTSigner signer = new JWTSigner(raw_token);
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", accountId + "_" + userId);
        claims.put("user", userInfo);
        JWTSigner.Options options = new JWTSigner.Options();
        options.setAlgorithm(Algorithm.HS256);
        String token = signer.sign(claims, options);


        // Uncomment to create with Anonymous User. You must also remove the token section above.
        /*
         String token = null;
        */



        mUser.login(token, raw_token, userId, accountId, firstName, lastName, email, referralCode, mTenant,null);

        return result;
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

    /**
     * This method is used when the signup button is pressed
     * @param signupButton The button pressed to direct the user to the signup activity
     */
    public void signup(View signupButton) {
        Intent intent = new Intent(this, SignupActivity.class);
        startActivity(intent);
    }
}
