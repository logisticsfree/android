package com.example.logisticsfree.models;

public class ItemModel {

    public String item;
    public boolean expanded;
    public Order order;

    public ItemModel(Order order) {
        this.order = order;
    }

    private Order getOrder() {
        return this.order;
    }

    @Override
    public boolean equals(Object o) {
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
