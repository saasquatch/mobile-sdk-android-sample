package com.referralsaasquatch.sampleapp;

import android.app.ListActivity;
import android.os.Bundle;
import android.app.Activity;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.wholepunk.saasquatch.Saasquatch;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ShowReferralsActivity extends ListActivity {

    private User mUser = User.getInstance();
    private String mTenant = "acunqvcfij2l4";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Saasquatch.listReferralsForTenant(mTenant, mUser.secret, mUser.accountId, mUser.userId, null, null, null, null, null, null, this,
                new Saasquatch.TaskCompleteListener() {
                    @Override
                    public void onComplete(JSONObject userInfo,
                                           String errorMessage,
                                           Integer errorCode) {

                        if (errorCode != null) {
                            return;
                        }

                        JSONArray referrals;

                        try {
                            referrals = userInfo.getJSONArray("referrals");
                        } catch (JSONException e) {
                            return;
                        }

                        String firstName;
                        Integer discountPercent;
                        String[] referralsList = new String[referrals.length()];

                        for (int i = 0; i < referrals.length(); i++) {
                            String referralsString;
                            try {
                                JSONObject referredUser = referrals.getJSONObject(i).getJSONObject("referredUser");
                                firstName = referredUser.getString("firstName");
                                JSONObject referredReward = referrals.getJSONObject(i).getJSONObject("referredReward");
                                discountPercent = referredReward.getInt("discountPercent");
                            } catch (JSONException e) {
                                break;
                            }

                            referralsString = "\nYou gave " + firstName + " " + discountPercent.toString() + "% off their SaaS";
                            referralsList[i] = referralsString;
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(ShowReferralsActivity.this, android.R.layout.simple_list_item_1, referralsList);
                        setListAdapter(adapter);

                    }
                });
    }


}
