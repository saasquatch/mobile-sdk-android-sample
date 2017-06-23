package com.referralsaasquatch.sampleapp;

/**
 * Created by trevorlee on 2017-05-15.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
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


public class CookieRegisterActivity extends Activity {
    private User mUser = User.getInstance();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cookieuser);
    }

    /**
     * This method is used to create a cookie user.
     *
     * @param editButton The button pressed to create a cookie user
     */
    public void createCookieUserButton(View editButton) {
        final EditText firstName = (EditText) findViewById(R.id.cookie_firstname);
        final EditText lastName = (EditText) findViewById(R.id.cookie_lastName);
        final EditText email = (EditText) findViewById(R.id.cookie_email);
        final EditText referralCode = (EditText) findViewById(R.id.cookie_referralcode);
        final String firstNameValue = firstName.getText().toString();
        final String lastNameValue = lastName.getText().toString();
        final String emailValue = email.getText().toString();
        final String referralCodeValue = referralCode.getText().toString();




        if (!validateFields()) {
            return;
        }

        JSONObject userInfo = createCookieUser(firstNameValue, lastNameValue, emailValue);

        // use upsert to update information
        Saasquatch.createCookieUser(mUser.tenant, mUser.token, userInfo, this,
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

                        // Dialog confirming user information updated.

                        showEditDialog(firstNameValue, lastNameValue, emailValue);

                    }
                });
    }


    private Boolean validateFields() {
        Boolean result = true;

        EditText firstName = (EditText) findViewById(R.id.cookie_firstname);
        EditText lastName = (EditText) findViewById(R.id.cookie_lastName);
        EditText email = (EditText) findViewById(R.id.cookie_email);

        String firstNameValue = firstName.getText().toString();
        String lastNameValue = lastName.getText().toString();
        String emailValue = email.getText().toString();

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

        return result;
    }

    /**
     * This method is used to create a JSON object of the new cookie user
     *
     * @param firstName The first name of the new sign up
     * @param lastName The last name of the new sign up
     * @param email The email of the new sign up
     * @return A JSON object containing the users information
     */
    private JSONObject createCookieUser(String firstName, String lastName, String email) {

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
        JWTSigner signer = new JWTSigner(mUser.token_raw);
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", accountId + "_" + userId);
        claims.put("user", userInfo);
        claims.put("allowAnonymous", true);
        JWTSigner.Options options = new JWTSigner.Options();
        options.setAlgorithm(Algorithm.HS256);
        String token = signer.sign(claims, options);


        // Uncomment to create with Anonymous User. You must also remove the token section above.
        /*
         String token = null;
        */

        mUser.login(token, mUser.token_raw, mUser.userId, mUser.accountId, mUser.firstName, mUser.lastName, mUser.email, mUser.referralCode, mUser.tenant,null);


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

    private void showEditDialog(String firstName, String lastName, String email) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater layoutInflater = this.getLayoutInflater();
        View dialogView = layoutInflater.inflate(R.layout.referral_dialog, null);
        Dialog dialog = builder.setView(dialogView)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(CookieRegisterActivity.this, WelcomeActivity.class);
                        startActivity(intent);
                    }
                })
                .create();

        TextView referred = (TextView) dialogView.findViewById(R.id.reward_textview_referred);
        String text = "Hi " + firstName + " " + lastName + " your cookie account has been created!";
        referred.setText(text);
        dialog.show();
    }


}
