package com.example.mymessage;

public class User {
    private String user;
    private  String PhoneNumber;
    private  String Uid;
    private  String ImageURL;
    private  String about;
    private  String lastseen;

    public User(String Uid,String name, String number) {
        this.Uid=Uid;
        this.user=name;
        this.PhoneNumber=number;

    }

    public User(String Uid,String name, String number,String ImageUrL) {
        this.Uid=Uid;
        this.user=name;
        this.PhoneNumber=number;
        this.ImageURL=ImageUrL;
    }

    public User(String Uid,String name, String number,String ImageUrL,String about,String lastseen) {
        this.Uid=Uid;
        this.user=name;
        this.PhoneNumber=number;
        this.ImageURL=ImageUrL;
        this.about=about;
        this.lastseen=lastseen;
    }

    public String getLastseen() {
        return lastseen;
    }

    public String getAbout() {
        return about;
    }
    public String getImageURL() { return ImageURL; }

    public String getPhoneNumber() {return PhoneNumber; }

    public String getUser() { return user; }

    public void setPhoneNumber(String phoneNumber) {
        PhoneNumber = phoneNumber;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getUid() {
        return  Uid;
    }
}
