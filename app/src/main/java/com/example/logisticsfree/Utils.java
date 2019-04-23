package com.example.logisticsfree;

import android.util.Pair;

import com.example.logisticsfree.adapters.RecyclerViewBindingAdapter;
import com.example.logisticsfree.models.ItemModel;
import com.example.logisticsfree.presenters.ListItemsPresenter;

public class Utils {
    public static RecyclerViewBindingAdapter.AdapterDataItem convert(ItemModel itemModel, ListItemsPresenter presenter){
        return new RecyclerViewBindingAdapter.AdapterDataItem(R.layout.layout_listitem, new Pair<Integer, Object>(BR.itemModel,itemModel),
                new Pair<Integer, Object>(BR.itemPresenter,presenter));
    }

//    public static RecyclerViewBindingAdapter.AdapterDataItem convert(SubItemModel subItemModel, ListItemsPresenter presenter){
//        return new RecyclerViewBindingAdapter.AdapterDataItem(R.layout.layout_sub_item, new Pair<Integer, Object>(BR.subItemModel, subItemModel),
//                new Pair<Integer, Object>(BR.subItemPresenter,presenter));
//    }
}
