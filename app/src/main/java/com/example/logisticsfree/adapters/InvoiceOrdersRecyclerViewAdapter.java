package com.example.logisticsfree.adapters;

import android.app.Activity;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableList;
import android.databinding.ViewDataBinding;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.logisticsfree.R;
import com.example.logisticsfree.trip.InvoiceOrderListFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.support.constraint.Constraints.TAG;

public class InvoiceOrdersRecyclerViewAdapter extends RecyclerView.Adapter<InvoiceOrdersRecyclerViewAdapter.BindingViewHolder> {
    private ObservableList<AdapterDataItem> data;
    private AdapterDataItem mRecentlyDeletedItem;
    private int mRecentlyDeletedItemPosition;
    private Activity mActivity;
    private Context context;

    public InvoiceOrdersRecyclerViewAdapter(ObservableList<AdapterDataItem> data) {
        this.data = data;
        data.addOnListChangedCallback(new ObservableListCallback());
    }

    @Override
    public BindingViewHolder onCreateViewHolder(ViewGroup parent,
                                                int viewType) {
        Log.d(TAG, "onCreateViewHolder: " + "Processing");
        mActivity = (Activity) parent.getContext();
        context = parent.getContext().getApplicationContext();
        return new BindingViewHolder(
                DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), viewType, parent, false));
    }

    public void setContext(Context context) {
        this.context = context;
    }
    public Context getContext() {
        return this.context;
    }

    @Override
    public void onBindViewHolder(InvoiceOrdersRecyclerViewAdapter.BindingViewHolder holder, int position) {
        AdapterDataItem dataItem = data.get(position);
        for (Pair<Integer, Object> idObjectPair : dataItem.idModelPairs) {
            holder.bind(idObjectPair.first, idObjectPair.second);
        }
//        holder.bind();
        holder.binding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public int getItemViewType(int position) {
        return data.get(position).layoutId;
    }

    public void deleteItem(int position) {
        mRecentlyDeletedItem = data.get(position);
        mRecentlyDeletedItemPosition = position;
        data.remove(position);
        notifyItemRemoved(position);
        showUndoSnackbar();
    }

    private void showUndoSnackbar() {
        View view = mActivity.findViewById(R.id.recyclerView1);
        Snackbar snackbar = Snackbar.make(view, "snackBarText", Snackbar.LENGTH_LONG);
        snackbar.setAction("UndoAction", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InvoiceOrdersRecyclerViewAdapter.this.undoDelete();
            }
        });
        snackbar.show();
    }

    private void undoDelete() {
        data.add(mRecentlyDeletedItemPosition, mRecentlyDeletedItem);
        notifyItemInserted(mRecentlyDeletedItemPosition);
    }

    private class ObservableListCallback extends ObservableList.OnListChangedCallback<ObservableList<InvoiceOrdersRecyclerViewAdapter.AdapterDataItem>> {

        @Override
        public void onChanged(ObservableList<AdapterDataItem> sender) {
            notifyDataSetChanged();
        }

        @Override
        public void onItemRangeChanged(ObservableList<AdapterDataItem> sender, int positionStart, int itemCount) {
            notifyItemRangeChanged(positionStart, itemCount);
        }

        @Override
        public void onItemRangeInserted(ObservableList<AdapterDataItem> sender, int positionStart, int itemCount) {
            notifyItemRangeInserted(positionStart, itemCount);
        }

        @Override
        public void onItemRangeMoved(ObservableList<AdapterDataItem> sender, int fromPosition, int toPosition, int itemCount) {
            notifyDataSetChanged(); // not sure how to notify adapter of this
            // event
        }

        @Override
        public void onItemRangeRemoved(ObservableList<AdapterDataItem> sender
                , int positionStart, int itemCount) {
            notifyItemRangeRemoved(positionStart, itemCount);
        }
    }

    public class BindingViewHolder extends RecyclerView.ViewHolder {
        private ViewDataBinding binding;

        public BindingViewHolder(ViewDataBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(int varId, Object obj) {
            this.binding.setVariable(varId, obj);
        }
    }

    public static class AdapterDataItem {
        public int layoutId;
        public List<Pair<Integer, Object>> idModelPairs;

        public AdapterDataItem(int layoutId, int variableId, Object model) {
            this.layoutId = layoutId;
            this.idModelPairs = new ArrayList<>();
            this.idModelPairs.add(new Pair<>(variableId, model));
        }

        public AdapterDataItem(int layoutId, Pair<Integer, Object>... idModelPairs) {
            Log.d(TAG, "AdapterDataItem: " + layoutId);
            this.layoutId = layoutId;
            this.idModelPairs = Arrays.asList(idModelPairs);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            AdapterDataItem that = (AdapterDataItem) o;

            if (layoutId != that.layoutId) return false;
            return idModelPairs != null ?
                    idModelPairs.equals(that.idModelPairs) :
                    that.idModelPairs == null;

        }

        @Override
        public int hashCode() {
            int result = layoutId;
            result = 31 * result + (idModelPairs != null ?
                    idModelPairs.hashCode() : 0);
            return result;
        }
    }
}
