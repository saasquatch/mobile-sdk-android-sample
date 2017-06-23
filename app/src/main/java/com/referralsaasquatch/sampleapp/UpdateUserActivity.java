package com.referralsaasquatch.sampleapp;

/**
 * Created by trevorlee on 2017-05-12.
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


public class UpdateUserActivity extends Activity {

    private User mUser = User.getInstance();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updateuser);
    }


    /*
     *
     * This method is used to collect the users data and change their account info when the button is pressed
     *
     * @param editButton pressed to update the users info
     */
    public void editUser(View editButton) {
        final EditText firstName = (EditText) findViewById(R.id.updateuser_textfield_firstname);
        final EditText lastName = (EditText) findViewById(R.id.updateuser_textfield_lastname);
        final EditText email = (EditText) findViewById(R.id.updateuser_textfield_email);
        final String firstNameValue = firstName.getText().toString();
        final String lastNameValue = lastName.getText().toString();
        final String emailValue = email.getText().toString();
        String referralCode = mUser.referralCode;
        String userID = mUser.userId;
        String accountID = mUser.accountId;



        if (!validateFields()) {
            return;
        }

        JSONObject userInfo = updateUser(userID, accountID, firstNameValue, lastNameValue, emailValue, referralCode);

        // use upsert to update information
        Saasquatch.userUpsert(mUser.tenant, mUser.userId, mUser.accountId, mUser.token, userInfo, this,
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

        EditText firstName = (EditText) findViewById(R.id.updateuser_textfield_firstname);
        EditText lastName = (EditText) findViewById(R.id.updateuser_textfield_lastname);
        EditText email = (EditText) findViewById(R.id.updateuser_textfield_email);

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
     *
     * This method is used to create a JSON object for the  users info
     *
     * @param userId  The unique id of the user
     * @param accountId The unique id of the account
     * @param firstName The first name of the new sign up
     * @param lastName  The last name of the new sign up
     * @param email The email of the new sign up
     * @param referralCode
     * @return A JSON object containing the users information
     */
    private JSONObject updateUser(String userId, String accountId, String firstName, String lastName, String email, String referralCode) {

        JSONObject result = new JSONObject();
        try {
            result.put("id", userId);
            result.put("accountId", accountId);
            result.put("firstName", firstName);
            result.put("lastName", lastName);
            result.put("email", email);
            result.put("referralCode", referralCode);

        } catch (JSONException e) {
            showRegistrationErrorAlert(null);
        }


        HashMap<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", userId);
        userInfo.put("accountId", accountId);
        userInfo.put("email", email);
        userInfo.put("firstName", firstName);
        userInfo.put("lastName", lastName);
        userInfo.put("referralCode", referralCode);


        // NOTE: Add in your API Key
        JWTSigner signer = new JWTSigner(mUser.token_raw);
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", accountId + "_" + userId);
        claims.put("user", userInfo);
        JWTSigner.Options options = new JWTSigner.Options();
        options.setAlgorithm(Algorithm.HS256);
        String token = signer.sign(claims, options);

        //String token = null;
        mUser.login(token, mUser.token_raw, userId, accountId, firstName, lastName, email, referralCode, mUser.tenant,null);
//        Log.w("TOKEN", token);

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
                        Intent intent = new Intent(UpdateUserActivity.this, WelcomeActivity.class);
                        startActivity(intent);
                    }
                })
                .create();

        TextView referred = (TextView) dialogView.findViewById(R.id.reward_textview_referred);
        String text = "Hi " + firstName + " " + lastName + " your information has been changed!";
        referred.setText(text);
        dialog.show();
    }

}
