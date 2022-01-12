package com.example.whatsapp;

public class MessageGroup {
    private String currentid,currenttime,currentusername,currentdate,message;

    public MessageGroup(){}

    public MessageGroup(String currentdate, String currentid,String message , String currentusername, String currenttime) {
        this.currentid = currentid;
        this.currenttime = currenttime;
        this.currentusername = currentusername;
        this.currentdate = currentdate;
        this.message = message;
    }

    public String getCurrentid() {
        return currentid;
    }

    public void setCurrentid(String currentid) {
        this.currentid = currentid;
    }

    public String getCurrenttime() {
        return currenttime;
    }

    public void setCurrenttime(String currenttime) {
        this.currenttime = currenttime;
    }

    public String getCurrentusername() {
        return currentusername;
    }

    public void setCurrentusername(String currentusername) {
        this.currentusername = currentusername;
    }

    public String getCurrentdate() {
        return currentdate;
    }

    public void setCurrentdate(String currentdate) {
        this.currentdate = currentdate;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
