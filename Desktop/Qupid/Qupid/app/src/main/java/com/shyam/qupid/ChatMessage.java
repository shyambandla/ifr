package com.shyam.qupid;

import java.util.Date;

/**
 * Created by SHIVACHARAN on 08-11-2017.
 */

public class ChatMessage {
   String message;
    String user;
    long messagetime;

    public long getMessagetime() {
        return messagetime;
    }

    public void setMessagetime(long messagetime) {
        this.messagetime = messagetime;
    }


    public ChatMessage() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public ChatMessage(String message, String user,long Messagetime) {
        this.message = message;
        this.user = user;
        this.messagetime=Messagetime;

    }


}
