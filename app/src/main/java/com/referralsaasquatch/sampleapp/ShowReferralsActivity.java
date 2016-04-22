package com.referralsaasquatch.sampleapp;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.wholepunk.saasquatch.Saasquatch;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ShowReferralsActivity extends Activity {

    private User mUser = User.getInstance();
    private String mTenant = "acunqvcfij2l4";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_referrals);

        final TextView textView = (TextView) findViewById(R.id.showreferrals_textview_textview);

        Saasquatch.listReferralsForTenant(mTenant, mUser.secret, mUser.accountId, mUser.userId, null, null, null, null, null, null, this,
                new Saasquatch.TaskCompleteListener() {
                    @Override
                    public void onComplete(JSONObject userInfo,
                                           String errorMessage,
                                           Integer errorCode) {

                        if (errorCode != null) {
                            textView.setText(errorMessage);
                            return;
                        }

                        JSONArray referrals;
                        String referralsString = "";

                        try {
                            referrals = userInfo.getJSONArray("referrals");
                        } catch (JSONException e) {
                            textView.setText(e.getLocalizedMessage());
                            return;
                        }

                        String firstName;
                        Integer discountPercent;

                        for (int i = 0; i < referrals.length(); i++) {
                            try {
                                JSONObject referredUser = referrals.getJSONObject(i).getJSONObject("referredUser");
                                firstName = referredUser.getString("firstName");
                                JSONObject referredReward = referrals.getJSONObject(i).getJSONObject("referredReward");
                                discountPercent = referredReward.getInt("discountPercent");
                            } catch (JSONException e) {
                                break;
                            }

                            referralsString += "\nYou gave " + firstName + " " + discountPercent.toString() + "% off their SaaS";
                        }

                        textView.setText(referralsString);
                    }
                });
    }
}
