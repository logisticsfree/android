package com.example.logisticsfree;

import android.util.Log;
import android.util.Pair;

import com.example.logisticsfree.adapters.InvoiceOrdersRecyclerViewAdapter;
import com.example.logisticsfree.adapters.RecyclerViewBindingAdapter;
import com.example.logisticsfree.models.ItemModel;
import com.example.logisticsfree.presenters.ListItemsPresenter;

public class Utils {
    private static String TAG = "Utils";
    public static RecyclerViewBindingAdapter.AdapterDataItem convert(ItemModel itemModel, ListItemsPresenter presenter){
        Log.d(TAG, "convert: ");
        return new RecyclerViewBindingAdapter
                .AdapterDataItem(R.layout.layout_listitem, new Pair<Integer, Object>(BR.itemModel, itemModel),
                new Pair<Integer, Object>(BR.itemPresenter, presenter));
    }
    public static InvoiceOrdersRecyclerViewAdapter.AdapterDataItem convertToOrder(ItemModel itemModel, ListItemsPresenter presenter){
        Log.d(TAG, "convertToOrder: ");
        return new InvoiceOrdersRecyclerViewAdapter
                .AdapterDataItem(R.layout.processing_order_list_item, new Pair<Integer, Object>(BR.itemModel, itemModel),
                new Pair<Integer, Object>(BR.itemPresenter, presenter));
    }

//    public static RecyclerViewBindingAdapter.AdapterDataItem convert(SubItemModel subItemModel, ListItemsPresenter presenter){
//        return new RecyclerViewBindingAdapter.AdapterDataItem(R.layout.layout_sub_item, new Pair<Integer, Object>(BR.subItemModel, subItemModel),
//                new Pair<Integer, Object>(BR.subItemPresenter,presenter));
//    }
}
