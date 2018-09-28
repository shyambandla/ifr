package com.shyam.qupid;

/**
 * Created by SHIVACHARAN on 05-11-2017.
 */

public class friends {
    String name;
    String uid;
    String lastmessage;
    String time;
    public friends(String name, String uid, String lastmessage, String time) {
        this.name = name;
        this.uid = uid;
        this.lastmessage = lastmessage;
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getLastmessage() {
        return lastmessage;
    }

    public void setLastmessage(String lastmessage) {
        this.lastmessage = lastmessage;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }


}
