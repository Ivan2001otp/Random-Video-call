package com.ivan.omegleclone.Models;

public class User {

    private String name,profileImageUrl,place,uid;
    private long coins;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public long getCoins() {
        return coins;
    }

    public void setCoins(long coins) {
        this.coins = coins;
    }

    public User(){}

    public User(String uid,String name,String profileImageUrl,String place,long coins){
        this.name = name;
        this.uid = uid;
        this.coins = coins;
        this.profileImageUrl = profileImageUrl;
        this.place = place;
    }


}
