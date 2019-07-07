package com.example.logisticsfree.models;

import java.util.Map;

public class Trip {
    String date, time, companyID, driverID, tripID;
    int status;
    Warehouse warehouse;
    Map<String, Object> orders;
    Boolean active;

    public Trip(String date, String time, String companyID, String driverID, String tripID,
                int status, Warehouse warehouse, Map<String, Object> orders, Boolean active) {
        this.date = date;
        this.time = time;
        this.companyID = companyID;
        this.driverID = driverID;
        this.tripID = tripID;
        this.status = status;
        this.warehouse = warehouse;
        this.orders = orders;
        this.active = active;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getTripID() {
        return tripID;
    }

    public void setTripID(String tripID) {
        this.tripID = tripID;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Trip() {
    }

    public Map<String, Object> getOrders() {
        return orders;
    }

    public String getDriverID() {
        return driverID;
    }

    public void setDriverID(String driverID) {
        this.driverID = driverID;
    }

    public void setOrders(Map<String, Object> orders) {
        this.orders = orders;
    }

    public String getCompanyID() {
        return companyID;
    }

    public void setCompanyID(String companyID) {
        this.companyID = companyID;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Warehouse getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(Warehouse warehouse) {
        this.warehouse = warehouse;
    }
}
