package com.example.logisticsfree.presenters;


import com.example.logisticsfree.models.ItemModel;

public interface ListItemsPresenter {
    void onClick(ItemModel itemModel);
    void onDeleteClick(ItemModel itemModel);
    void onExpandClick(ItemModel itemModel);
    void onLoadMoreClick();
}
