package com.referralsaasquatch.sampleapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.wholepunk.saasquatch.Saasquatch;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends Activity {

    private User mUser = User.getInstance();
    private String mTenant = "SaaS";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void login(View loginButton) {
        EditText emailField = (EditText) findViewById(R.id.login_textfield_email);
        EditText passwordField = (EditText) findViewById(R.id.login_textfield_password);
        String emailValue = emailField.getText().toString();
        String passwordValue = passwordField.getText().toString();

        if (emailValue.equals("demo") && passwordValue.equals("demo")) {

            // Get Bob's info
            final String userId = "123456";
            final String accountId = "123456";
            final String secret = "038tr0810t8h1028th108102085180";

            // Lookup Bob with Referral SaaSquatch
            Saasquatch.getUser(mTenant, userId, accountId, secret,
                    new Saasquatch.FetchContextCompleteListener() {
                        @Override
                        public void onComplete(JSONObject context, String errorMessage, Integer errorCode) {

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
                                email = context.getString("email");
                                firstName = context.getString("firstName");
                                lastName = context.getString("lastName");
                                referralCode = context.getString("referralCode");
                            } catch (JSONException e) {
                                // Show an alert describing the error
                                return;
                            }

                            // Login Bob
                            mUser.login(secret, userId, accountId, firstName, lastName, email, referralCode);

                            // Bob has an unclaimed reward for signing up with a Referral Code
                            mUser.addReward("BOBTESTERSON", "$20 off your next SaaS");

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

    public void signup(View signupButton) {
        Intent intent = new Intent(this, SignupActivity.class);
        startActivity(intent);
    }
}
