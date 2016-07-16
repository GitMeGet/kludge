package com.example.sylta.myapplication.wmudatastore;

/** The object model for the data we are sending through endpoints */
class UserEntity {

    private String username;
    private String userID;
    private String tokenID;
    private int snoozeFreq;

    public UserEntity() {}

    public UserEntity(String username, String userID, String tokenID, int snoozeFreq){

        this.username = username;
        this.userID = userID;
        this.tokenID = tokenID;
        this.snoozeFreq = snoozeFreq;

    }

    public String getUsername() {return username;}
    public String getUserID() {return userID;}
    public String getTokenID() {return tokenID;}
    public int getSnoozeFreq() {return snoozeFreq;}

    public void setUsername(String username) {this.username = username;}
    public void setUserID(String userID) {this.userID = userID;}
    public void setTokenID(String tokenID) {this.tokenID = tokenID;}
    public void setSnoozeFreq(int snoozeFreq) {this.snoozeFreq = snoozeFreq;}
}