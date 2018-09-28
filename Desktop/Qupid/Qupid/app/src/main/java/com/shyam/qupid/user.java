package com.shyam.qupid;

/**
 * Created by SHIVACHARAN on 04-11-2017.
 */

public class user {
    public user(String userid, String name) {
        Userid = userid;
        this.name = name;
    }

    String Userid;

    public user() {
    }

    public String getUserid() {
        return Userid;
    }

    public void setUserid(String userid) {
        Userid = userid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    String name;
}
