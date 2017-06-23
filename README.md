Referral SaaSquatch Android SDK
==========================================
The Referral SaaSquatch Android SDK integrates a referral program into your Android app.

This repository contains a sample Android app which makes use of the Referral SaaSquatch Android SDK. This sample app shows how the SaaSquatch Android SDK can be integrated into your own Android apps.      

To download, select the `Download ZIP` option from the green `Clone or download` drop-down.


Sample App Demonstrates:
--------------------------
* User Upsert
* Create a User and Account
* Lookup a User
* Create Cookie User
* Get a User by a Referral
* Lookup a Referral code
* Apply a Referral Code
* List Referrals

Setup
-----
Your Tenant ID will need to be added to the following file:
* LoginActivity.java

Your API key will need to be added to the following file:
* LoginActivity.java

The place in the Java file you need to add your Tenant ID and API key is marked with `"TENANT_ALIAS_HERE"` and `"ADD_JWT_HERE"`.  

The Java files are located in **app/src/main/java/com/referralsaasquatch/sampleapp**

Walkthrough
-----------
With your Tenant ID and API key set, you are ready to run the app. The SaaSquatch SDK has already been included in the sample app so you do not need to install it. This sample app has been optimized for the Pixel device.


These steps describe how to use the app as a first time user.

**Step 1:** Open the sample app in Android Studio.
**Step 2:** Clean and Build the project (Build -> Clean Project, Build -> Build APK).   
**Step 3:** Run the app. You will see the login page. A default test user named "saasquatch android" has been created.  
**Step 4:** Navigate to the sign up page by clicking the "sign up" link at the bottom of the login page.  
**Step 5:** Enter your info, being sure to use the referral code `saasquatchandroid`.  
**Step 6:** With a successful sign up, you will be redirected to the welcome page.  
**Step 7:** From the welcome page, you can click the labeled buttons to be redirected to the corresponding pages.   
For example, the "Edit Info" button will redirect you to a page where you can edit the first name, last name, and email of your registered user.         

Resources
---------

The Referral SaaSquatch Developer Documentation contains further information about our Android SDK:

* [Overview of our Android SDK](https://docs.referralsaasquatch.com/mobile/android/)
* [Quickstart guide for the Android SDK](https://docs.referralsaasquatch.com/mobile/android/quickstart/)
