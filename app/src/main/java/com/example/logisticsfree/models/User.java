package com.example.logisticsfree.models;

import java.util.HashMap;

public class User {
    private String fName;
    private String lName;
    private String phone;
    private boolean onlineStatus;
    private HashMap<String, Object> location;
    private boolean availability;
    private boolean enabled;
    private Truck truck;

    public User() {}

    public User(String fName, String lName, String phone, boolean onlineStatus, HashMap<String, Object> location, boolean availability, boolean enabled, Truck truck) {
        this.fName = fName;
        this.lName = lName;
        this.phone = phone;
        this.onlineStatus = onlineStatus;
        this.location = location;
        this.availability = availability;
        this.enabled = enabled;
        this.truck = truck;
    }

    public String getfName() {
        return fName;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public String getlName() {
        return lName;
    }

    public void setlName(String lName) {
        this.lName = lName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isOnlineStatus() {
        return onlineStatus;
    }

    public void setOnlineStatus(boolean onlineStatus) {
        this.onlineStatus = onlineStatus;
    }

    public HashMap<String, Object> getLocation() {
        return location;
    }

    public void setLocation(HashMap<String, Object> location) {
        this.location = location;
    }

    public boolean isAvailability() {
        return availability;
    }

    public void setAvailability(boolean availability) {
        this.availability = availability;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Truck getTruck() {
        return truck;
    }

    public void setTruck(Truck truck) {
        this.truck = truck;
    }
}
