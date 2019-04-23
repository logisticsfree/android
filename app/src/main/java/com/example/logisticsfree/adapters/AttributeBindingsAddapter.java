package com.example.logisticsfree.adapters;

import android.databinding.BindingAdapter;
import android.databinding.ObservableList;
import android.support.v7.widget.RecyclerView;

public class AttributeBindingsAddapter {
    @BindingAdapter({"list", "layoutManager", "itemAnimator"})
    public static void setList(RecyclerView rv, ObservableList dataItems, RecyclerView.LayoutManager layoutManager, RecyclerView.ItemAnimator itemAnimator) {
        if (rv.getLayoutManager() == null)
            rv.setLayoutManager(layoutManager);
        if (rv.getAdapter() == null)
            rv.setAdapter(new RecyclerViewBindingAdapter(dataItems));
        if (rv.getItemAnimator() == null)
            rv.setItemAnimator(itemAnimator);
    }
}
