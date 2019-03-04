package com.example.newuber.models;

import java.util.HashMap;

public class User {
    public String fName;
    public String lName;
    public String phone;
    public boolean onlineStatus;
    public HashMap<String, Object> location;
    public boolean availability;

    public User() {}

    public User(String fName, String lName, String phone, boolean onlineStatus, HashMap<String, Object> location, boolean availability) {
        this.fName = fName;
        this.lName = lName;
        this.phone = phone;
        this.onlineStatus = onlineStatus;
        this.location = location;
        this.availability = availability;
    }
}
