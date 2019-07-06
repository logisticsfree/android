package com.example.logisticsfree.adapters;

import android.databinding.BindingAdapter;
import android.databinding.ObservableList;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.example.logisticsfree.SwipeToDeleteCallback;

public class AttributeBindingsAddapter {
    @BindingAdapter({"list", "layoutManager", "itemAnimator"})
    public static void setList(RecyclerView rv, ObservableList dataItems,
                               RecyclerView.LayoutManager layoutManager,
                               RecyclerView.ItemAnimator itemAnimator) {
        if (rv.getLayoutManager() == null)
            rv.setLayoutManager(layoutManager);
        if (rv.getAdapter() == null) {
            String recyclerClass = RecyclerViewBindingAdapter.AdapterDataItem.class.toString();
            String currentDataItemClass = dataItems.get(0).getClass().toString();

            if (recyclerClass.equals(currentDataItemClass)) {
                rv.setAdapter(new RecyclerViewBindingAdapter(dataItems));
            } else {
                InvoiceOrdersRecyclerViewAdapter mAdapter = new InvoiceOrdersRecyclerViewAdapter(dataItems);
                mAdapter.setContext(rv.getContext());

                rv.setAdapter(mAdapter);
                ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCallback(mAdapter));
                itemTouchHelper.attachToRecyclerView(rv);
            }
        }
        if (rv.getItemAnimator() == null)
            rv.setItemAnimator(itemAnimator);
    }
}
