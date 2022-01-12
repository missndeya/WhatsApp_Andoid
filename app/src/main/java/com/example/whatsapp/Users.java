package com.example.whatsapp;

public class Users
{
    private String id;
    private String image;
    private  String nom;

    public Users(String image, String nom) {
        this.id = "";
        this.image = image;
        this.nom = nom;
    }

    public String getImage() {
        return image;
    }

    public String getNom() {
        return nom;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
