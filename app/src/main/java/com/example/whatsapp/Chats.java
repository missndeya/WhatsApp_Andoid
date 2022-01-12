package com.example.whatsapp;

public class Chats {
    private String idsender,idreceiver,message;

    public Chats(String idsender, String idreceiver, String message) {
        this.idsender = idsender;
        this.idreceiver = idreceiver;
        this.message = message;
    }

    public String getIdsender() {
        return idsender;
    }

    public void setIdsender(String idsender) {
        this.idsender = idsender;
    }

    public String getIdreceiver() {
        return idreceiver;
    }

    public void setIdreceiver(String idreceiver) {
        this.idreceiver = idreceiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
