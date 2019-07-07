package com.example.logisticsfree.models;

import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Map;

public class Order {
    String date, time, companyID, tripID;
    int status;
    Warehouse warehouse;
    Map<String, Object> ordersJson;

    public Order(String date, String time, String companyID, String tripID, int status,
                 Warehouse warehouse, Map<String, Object> ordersJson) {
        this.date = date;
        this.time = time;
        this.companyID = companyID;
        this.tripID = tripID;
        this.status = status;
        this.warehouse = warehouse;
        this.ordersJson = ordersJson;
    }

    public Order() {
    }

    public String getTripID() {
        return tripID;
    }

    public void setTripID(String tripID) {
        this.tripID = tripID;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Map<String, Object> getOrdersJson() {
        return ordersJson;
    }

    public void setOrdersJson(Map<String, Object> ordersJson) {
        this.ordersJson = ordersJson;
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
