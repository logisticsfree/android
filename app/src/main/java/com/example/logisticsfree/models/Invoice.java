package com.example.logisticsfree.models;

import java.util.Map;

public class Invoice {
    Distributor distributor;
    String invoice, value, volume, weight;
    Integer status;
    Map<String, SKU> skus;
    Boolean completed;

    public Invoice(Distributor distributor, String invoice, String value, String volume, String weight, Integer status, Map<String, SKU> skus, Boolean completed) {
        this.distributor = distributor;
        this.invoice = invoice;
        this.value = value;
        this.volume = volume;
        this.weight = weight;
        this.status = status;
        this.skus = skus;
        this.completed = completed;
    }

    public Invoice() {
    }

    public Boolean isCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public Distributor getDistributor() {
        return distributor;
    }

    public void setDistributor(Distributor distributor) {
        this.distributor = distributor;
    }

    public String getInvoice() {
        return invoice;
    }

    public void setInvoice(String invoice) {
        this.invoice = invoice;
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Map<String, SKU> getSkus() {
        return skus;
    }

    public void setSkus(Map<String, SKU> skus) {
        this.skus = skus;
    }
}

