package com.example.logisticsfree.models;

public class Order {
    String date, time;
    Warehouse warehouse;

    public Order(String date, String time, Warehouse warehouse) {
        this.date = date;
        this.time = time;
        this.warehouse = warehouse;
    }

    public Order() {
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
