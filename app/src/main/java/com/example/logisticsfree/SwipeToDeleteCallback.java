package com.example.logisticsfree;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.example.logisticsfree.adapters.InvoiceOrdersRecyclerViewAdapter;

public class SwipeToDeleteCallback extends ItemTouchHelper.SimpleCallback {
    private InvoiceOrdersRecyclerViewAdapter mAdapter;

    public SwipeToDeleteCallback(InvoiceOrdersRecyclerViewAdapter mAdapter) {
        super(0, ItemTouchHelper.LEFT);
        this.mAdapter = mAdapter;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();
//        mAdapter.deleteItem(position);
    }
}
