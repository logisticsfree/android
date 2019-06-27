package com.example.logisticsfree.models;

public class SKU {
    String code, name, qty, value, volume, weight;

    public SKU(String code, String name, String qty, String value, String volume, String weight) {
        this.code = code;
        this.name = name;
        this.qty = qty;
        this.value = value;
        this.volume = volume;
        this.weight = weight;
    }

    public SKU() {
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }
}
