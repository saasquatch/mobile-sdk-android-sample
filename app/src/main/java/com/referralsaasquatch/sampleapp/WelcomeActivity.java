package com.referralsaasquatch.sampleapp;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

import java.util.List;

public class WelcomeActivity extends Activity {

    private User mUser = User.getInstance();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        TextView welcome = (TextView) findViewById(R.id.welcome_textview_welcome);
        TextView referralCode = (TextView) findViewById(R.id.welcome_textview_referralcode);

        String welcomeString = "Welcome, " + mUser.firstName;
        welcome.setText(welcomeString);

        referralCode.setText(mUser.referralCode);

        FacebookSdk.sdkInitialize(getApplicationContext());
    }

    /**
     * This method creates a facebook share button
     *
     * @param shareButton button pressed for a facebook share button
     */
    public void shareFacebook(View shareButton) {
        ShareLinkContent content = new ShareLinkContent.Builder()
                .setContentUrl(Uri.parse(mUser.shareLinks.get("facebook")))
                .setContentTitle("Get SaaS for 10% less!")
                .setContentDescription("Sign up for a SaaS account and we both get 10% off our SaaS!")
                .build();

        ShareDialog dialog = new ShareDialog(this);

        if (ShareDialog.canShow(ShareLinkContent.class)) {
            dialog.show(content);
        }
    }

    /**
     * This method creats a twitter share button
     *
     * @param shareButton button pressed for a twitter share button
     */
    public void shareTwitter(View shareButton) {
        Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Get SaaS for 10% less!");
        shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Sign up for a SaaS account and we both get 10% off our SaaS with link: " + mUser.shareLinks.get("twitter"));

        PackageManager pm = this.getPackageManager();
        List<ResolveInfo> activityList = pm.queryIntentActivities(shareIntent, 0);
        for (final ResolveInfo app : activityList)
        {
            if ((app.activityInfo.name).contains("twitter"))
            {
                final ActivityInfo activity = app.activityInfo;
                final ComponentName name = new ComponentName(activity.applicationInfo.packageName, activity.name);
                shareIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                shareIntent.setComponent(name);
                this.startActivity(shareIntent);
                break;
            }
        }
    }

    public void show(View showButton) {
        Intent intent = new Intent(WelcomeActivity.this, ShowReferralsActivity.class);
        startActivity(intent);
    }

    /**
     * This method directs to the page where a user can edit their info
     *
     * @param editButton The button pressed when the user wants to edit their info
     */
    public void setEditButton(View editButton) {
        Intent intent = new Intent(WelcomeActivity.this, UpdateUserActivity.class);
        startActivity(intent);
    }

    /**
     * This method directs to the page where a cookie user can sign up
     *
     * @param editButton The button pressed when the user wants to set a cookie user
     */
    public void setCookieButton(View editButton) {
        Intent intent = new Intent(WelcomeActivity.this, CookieRegisterActivity.class);
        startActivity(intent);
    }


    /**
     * This method directs to the page where a cookie user can sign up
     *
     * @param shareButton The button pressed when the user wants to set a cookie user
     */
    public void setShareButton(View shareButton) {
        Intent intent = new Intent(WelcomeActivity.this, ShareLinksActivity.class);
        startActivity(intent);
    }
}
