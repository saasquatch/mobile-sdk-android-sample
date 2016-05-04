package com.referralsaasquatch.sampleapp;

import java.util.HashMap;

/**
 * Created by brendancrawford on 2016-03-22.
 */
public class User {
    private static User mInstance = new User();
    public String token;
    public String userId;
    public String accountId;
    public String firstName;
    public String lastName;
    public String email;
    public String referralCode;
    public HashMap<String, String> shareLinks;

    public static User getInstance() {
        return mInstance;
    }

    private User() {}

    public void login(String token,
                      String id,
                      String accountId,
                      String firstName,
                      String lastName,
                      String email,
                      String referralCode,
                      HashMap<String, String> shareLinks) {
        this.token = token;
        this.userId = id;
        this.accountId = accountId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.referralCode = referralCode;
        this.shareLinks = shareLinks;
    }

}
