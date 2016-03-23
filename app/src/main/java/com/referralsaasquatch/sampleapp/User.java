package com.referralsaasquatch.sampleapp;

import java.util.LinkedList;

/**
 * Created by brendancrawford on 2016-03-22.
 */
public class User {
    private static User mInstance = new User();
    public String secret;
    public String userId;
    public String accountId;
    public String firstName;
    public String lastName;
    public String email;
    public String referralCode;
    public LinkedList<Reward> rewards;

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
        this.secret = secret;
        this.userId = id;
        this.accountId = accountId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.referralCode = referralCode;
        rewards = new LinkedList<>();
    }

    public void addReward(String code, String reward) {
        rewards.add(new Reward(code, reward));
    }

    public class Reward {
        public String code;
        public String reward;

        public Reward(String code, String reward) {
            this.code = code;
            this.reward = reward;
        }
    }
}
