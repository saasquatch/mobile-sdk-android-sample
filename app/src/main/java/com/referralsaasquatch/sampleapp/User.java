package com.referralsaasquatch.sampleapp;

import java.util.LinkedList;

/**
 * Created by brendancrawford on 2016-03-22.
 */
public class User {
    private static User mInstance = new User();
    private String mSecret;
    private String mId;
    private String mAccountId;
    private String mFirstName;
    private String mLastName;
    private String mEmail;
    private String mReferralCode;
    private LinkedList<Reward> mRewards;

    public static User getInstance() {
        return mInstance;
    }

    private User() {}

    public void login(String secret,
                      String id,
                      String accountId,
                      String firstName,
                      String lastName,
                      String email,
                      String referralCode) {
        mSecret = secret;
        mId = id;
        mAccountId = accountId;
        mFirstName = firstName;
        mLastName = lastName;
        mEmail = email;
        mReferralCode = referralCode;
        mRewards = new LinkedList<>();
    }

    public void addReward(String code, String reward) {
        mRewards.add(new Reward(code, reward));
    }

    private class Reward {
        private String mCode;
        private String mReward;

        public Reward(String code, String reward) {
            mCode = code;
            mReward = reward;
        }
    }
}
