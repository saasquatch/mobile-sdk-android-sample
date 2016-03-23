package com.referralsaasquatch.sampleapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.wholepunk.saasquatch.Saasquatch;

import org.json.JSONException;
import org.json.JSONObject;

public class SignupActivity extends AppCompatActivity {

    private User mUser = User.getInstance();
    private String mTenant;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
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
        Saasquatch.registerUser(mTenant, mUser.userId, mUser.accountId, userInfo,
                new Saasquatch.RegisterUserCompleteListener() {
                    @Override
                    public void onComplete(JSONObject userInfo, String errorMessage, Integer errorCode) {

                        if (errorCode != null) {
                            // Show an alert describing the error
                            showRegistrationErrorAlert(errorMessage);
                            return;
                        }

                        // Validate the referral code
                        Saasquatch.validateReferralCode(mTenant, referralCodeValue, mUser.secret,
                                new Saasquatch.FetchContextCompleteListener() {
                                    @Override
                                    public void onComplete(JSONObject userInfo, String errorMessage, Integer errorCode) {

                                        if (errorCode != null) {
                                            if (errorCode.equals(401)) {
                                                // The secret was not the same as registered
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
                                                } else { // type == "TIME_CREDIT or type == "CREDIT"
                                                    Integer credit = reward.getInt("credit");
                                                    rewardString = credit.toString() + " " + unit + " off your next SaaS";
                                                }
                                            }
                                        } catch (JSONException e) {
                                            showRegistrationErrorAlert("Something went wrong with your referral code.");
                                            return;
                                        }

                                        // Give the user a reward for signing up with referralCode
                                        mUser.addReward(code, rewardString);

                                        // Lookup the person that referred user
                                        Saasquatch.getUserByReferralCode(mTenant, referralCodeValue, mUser.secret,
                                                new Saasquatch.FetchContextCompleteListener() {
                                                    @Override
                                                    public void onComplete(JSONObject userInfo, String errorMessage, Integer errorCode) {

                                                        if (errorCode != null) {
                                                            if (errorCode.equals(401)) {
                                                                // The secret was not the same as registered
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
                                                        String referrerLastName;
                                                        try {
                                                            referrerFirstName = userInfo.getString("firstName");
                                                            referrerLastName = userInfo.getString("lastName");
                                                        } catch (JSONException e) {
                                                            showRegistrationErrorAlert("Something went wrong with your referral code.");
                                                            return;
                                                        }

                                                        showReferralDialog(referrerFirstName, referrerLastName);
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
        String userId = "000001";
        String accountId = "000001";
        String locale = "en_us";
        String referralCode = firstName.toUpperCase() + lastName.toUpperCase();
        String secret = "038tr0810t8h1028th108102085180";

        mUser.login(secret, userId, accountId, firstName, lastName, email, referralCode);

        JSONObject result = new JSONObject();
        try {
            result.put("secret", secret);
            result.put("id", userId);
            result.put("accountId", accountId);
            result.put("email", email);
            result.put("firstName", firstName);
            result.put("lastName", lastName);
            result.put("locale", locale);
            result.put("referralCode", referralCode);
            result.put("imageURL", "");
        } catch (JSONException e) {
            showRegistrationErrorAlert(null);
        }

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

    private void showReferralDialog(String firstName, String lastName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater layoutInflater = this.getLayoutInflater();
        Dialog dialog = builder.setView(layoutInflater.inflate(R.layout.referral_dialog, null))
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(SignupActivity.this, WelcomeActivity.class);
                        startActivity(intent);
                    }
                })
                .create();

        TextView referred = (TextView) findViewById(R.id.reward_textview_referred);
        TextView rewardString = (TextView) findViewById(R.id.reward_textview_rewardstring);
        referred.setText("You've been referred by\n" + firstName + " " + lastName);
        rewardString.setText(mUser.rewards.peekFirst().reward);

        dialog.show();
    }
}
