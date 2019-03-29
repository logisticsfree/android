package com.example.logisticsfree.models;

public class Truck {
    String vid;
    double volume;
    double weight;
    String type;

    public Truck(String vid, double volume, double weight, String type) {
        this.vid = vid;
        this.volume = volume;
        this.weight = weight;
        this.type = type;
    }

    public String getVid() {
        return vid;
    }

    public void setVid(String vid) {
        this.vid = vid;
    }

    public double getVolume() {
        return volume;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
