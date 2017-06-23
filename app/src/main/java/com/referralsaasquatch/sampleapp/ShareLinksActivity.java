package com.referralsaasquatch.sampleapp;

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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ArrayAdapter;

import com.wholepunk.saasquatch.Saasquatch;
import com.auth0.jwt.*;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.ArrayList;

import android.util.Log;
import java.util.Iterator;



/**
 * Created by trevorlee on 2017-05-29.
 */

public class ShareLinksActivity extends Activity {
    private User mUser = User.getInstance();

    private Spinner engagementSpinner;
    private Spinner shareSpinner;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sharelinks);

        addItemsOnEngagementSpinner();
        addItemsOnShareSpinner();
    }

    /*
     * This method is used to add engagement medium options to the spinner
     */
    public void addItemsOnEngagementSpinner() {
        engagementSpinner = (Spinner) findViewById(R.id.engagement_spinner);
        List<String> list = new ArrayList<String>();
        list.add("ALL");
        list.add("HOSTED");
        list.add("EMAIL");
        list.add("POPUP");
        list.add("MOBILE");
        list.add("EMBED");
        list.add("UNKNOWN");


        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        engagementSpinner.setAdapter(dataAdapter);
    }

    /*
     * This method is used to add share medium options to the spinner
     */
    public void addItemsOnShareSpinner() {
        shareSpinner = (Spinner) findViewById(R.id.shareMedium_spinner);
        List<String> list = new ArrayList<String>();
        list.add("ALL");
        list.add("EMAIL");
        list.add("SMS");
        list.add("WHATSAPP");
        list.add("LINKEDIN");
        list.add("TWITTER");
        list.add("FBMESSENGER");
        list.add("UNKNOWN");
        list.add("DIRECT");
        list.add("FACEBOOK");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        shareSpinner.setAdapter(dataAdapter);
    }

    /**
     * This method is used to output the sharelinks.
     *
     * @param shareButton The button pressed to get sharelinks
     */
    public void createShareLinkButton(View shareButton) {
        engagementSpinner = (Spinner) findViewById(R.id.engagement_spinner);
        shareSpinner = (Spinner) findViewById(R.id.shareMedium_spinner);

        String engagementSpinnerValue = String.valueOf(engagementSpinner.getSelectedItem());
        String shareSpinnerValue = String.valueOf(shareSpinner.getSelectedItem());

        // get all engagement values
        if(engagementSpinnerValue.equals("ALL")) {
            engagementSpinnerValue = null;
        }

        // get all share medium values
        if(shareSpinnerValue.equals("ALL")) {
            shareSpinnerValue = null;
        }

        final String engagementVal = engagementSpinnerValue;
        final String shareVal = shareSpinnerValue;

        // get the sharelinks
        Saasquatch.getSharelinks(mUser.tenant, mUser.accountId,mUser.userId,engagementVal,shareVal,mUser.token,ShareLinksActivity.this,new Saasquatch.TaskCompleteListener() {
            @Override
            public void onComplete(JSONObject userInfo, String errorMessage, Integer errorCode) {

                if (errorCode != null) {
                    // Show an alert describing the error
                    showErrorAlert(errorMessage);
                    return;
                }

                JSONObject shareLinksJSON;

                try {
                    shareLinksJSON = userInfo.getJSONObject("shareLinks");


                    // display the links
                    Iterator engagementKeysIterator = shareLinksJSON.keys();
                    List<String> engagementKeysList = new ArrayList<String>();
                    while(engagementKeysIterator.hasNext()) {
                        String key = (String) engagementKeysIterator.next();
                        engagementKeysList.add(key);
                    }
                    String[] engagementKeyArray = engagementKeysList.toArray(new String[engagementKeysList.size()]);
                    int engagementSize =  engagementKeyArray.length;

                    Iterator shareKeysIterator = shareLinksJSON.getJSONObject(engagementKeyArray[0]).keys();
                    List<String> shareKeysList = new ArrayList<String>();
                    while(shareKeysIterator.hasNext()) {
                        String key = (String) shareKeysIterator.next();
                        shareKeysList.add(key);
                    }
                    String[] shareKeyArray = shareKeysList.toArray(new String[shareKeysList.size()]);
                    int shareSize =  shareKeyArray.length;

                    String output = "";
                    for(int i = 0; i < engagementSize; i++) {
                        for(int j = 0; j < shareSize; j++) {
                            String link = shareLinksJSON.getJSONObject(engagementKeyArray[i]).getString(shareKeyArray[j]);
                            output = output + engagementKeyArray[i] + " + " + shareKeyArray[j] + " = " + link + "\n";
                        }
                        output = output + "\n";
                    }

                    TextView links = (TextView) findViewById(R.id.links_textview);
                    links.setText(output);


                } catch (JSONException e) {
                    // Show an alert describing the error (can't get sharelinks)
                    showErrorAlert("ShareLinks error.\nPlease try again.");
                    return;
                }
            }

        });

    }


    private void showErrorAlert(String message) {
        String errorMessage = "Something went wrong getting your sharelinks\n" + "Please check your entries and try again.";
        if (message != null) {
            errorMessage = message;
        }
        new AlertDialog.Builder(this)
                .setTitle("ShareLinks Error")
                .setMessage(errorMessage)
                .setPositiveButton("OK", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}
