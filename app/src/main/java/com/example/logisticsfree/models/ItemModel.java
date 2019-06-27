package com.example.logisticsfree.models;

import android.util.Log;

import java.util.Map;

public class ItemModel {
    private static String TAG = "ItemModel";
    public String item;
    public boolean expanded;
    public Order order;
    public Invoice invoice;

    public ItemModel(Order order) {
        this.order = order;
    }
    public ItemModel(Invoice invoice) {
        this.invoice = invoice;
    }

    private Order getOrder() {
        return this.order;
    }
    private Invoice getInvoice() { return this.invoice; }

    @Override
    public boolean equals(Object o) {
        Log.d(TAG, "equals: ");
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ItemModel itemModel = (ItemModel) o;

        if (expanded != itemModel.expanded) return false;
        return item != null ? item.equals(itemModel.item) : itemModel.item == null;

    }

    @Override
    public int hashCode() {
        int result = item != null ? item.hashCode() : 0;
        result = 31 * result + (expanded ? 1 : 0);
        return result;
    }
}
