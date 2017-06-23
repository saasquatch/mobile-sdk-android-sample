package com.referralsaasquatch.sampleapp;

import java.util.HashMap;

/**
 * Created by brendancrawford on 2016-03-22.
 * Modified by Trevor Lee on 2017-06-18
 */
public class User {
    private static User mInstance = new User();
    public String token;
    public String token_raw;
    public String userId;
    public String accountId;
    public String firstName;
    public String lastName;
    public String email;
    public String referralCode;
    public String tenant;
    public HashMap<String, String> shareLinks;

    public static User getInstance() {
        return mInstance;
    }

    private User() {}

    public void login(String token,
                      String token_raw,
                      String id,
                      String accountId,
                      String firstName,
                      String lastName,
                      String email,
                      String referralCode,
                      String tenant,
                      HashMap<String, String> shareLinks) {
        this.token = token;
        this.token_raw = token_raw;
        this.userId = id;
        this.accountId = accountId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.referralCode = referralCode;
        this.tenant = tenant;
        this.shareLinks = shareLinks;
    }

}
