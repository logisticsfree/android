package com.example.logisticsfree.trip;

import android.databinding.DataBindingUtil;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.logisticsfree.BR;
import com.example.logisticsfree.R;
import com.example.logisticsfree.Utils;
import com.example.logisticsfree.adapters.ProcessingOrdersRecyclerViewAdapter;
import com.example.logisticsfree.databinding.FragmentProcessingOrderRecyclerViewBinding;
import com.example.logisticsfree.models.HeadingModel;
import com.example.logisticsfree.models.Invoice;
import com.example.logisticsfree.models.ItemModel;
import com.example.logisticsfree.presenters.ListItemsPresenter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProcessingOrderListFragment extends Fragment implements ListItemsPresenter {
    private final String TAG = "ProcessingFragment";

    private ObservableList<ProcessingOrdersRecyclerViewAdapter.AdapterDataItem> listItems;
    private FirebaseUser mUser;
    private FirebaseFirestore afs;

    public ProcessingOrderListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        afs = FirebaseFirestore.getInstance();
//        loadFromFirestore();

        getActivity().setTitle("Orders List");

    }

    @android.support.annotation.Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @android.support.annotation.Nullable ViewGroup container,
                             @android.support.annotation.Nullable Bundle savedInstanceState) {
        FragmentProcessingOrderRecyclerViewBinding mBinding = DataBindingUtil
                .inflate(inflater, R.layout.fragment_processing_order_recycler_view, container, false);
        mBinding.setListLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mBinding.setModelList(initList());
        mBinding.setItemAnimator(new DefaultItemAnimator());
        Log.d(TAG, "onCreateView: ");

        return mBinding.getRoot();
    }

    private void loadFromFirestore() {
        afs.collection("drivers/" + mUser.getUid() + "/trips/").whereEqualTo("active", true).limit(1)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snap, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }
                listItems.clear();
                listItems.add(new ProcessingOrdersRecyclerViewAdapter.AdapterDataItem(R.layout.layout_listitem_heading,
                        new Pair<Integer, Object>(BR.headingModel, new HeadingModel("Remaining Invoices"))));

                List<ProcessingOrdersRecyclerViewAdapter.AdapterDataItem> list = new ArrayList<>();

                Map<String, Object> fulDoc = snap.getDocuments().get(0).getData();
                Map orders = (Map) fulDoc.get("orders");
                Collection<HashMap> ordersList = orders.values();

                for ( HashMap orderMap : ordersList) {
                    Gson gson = new Gson();
                    JsonElement jsonElement = gson.toJsonTree(orderMap);
                    Invoice order = gson.fromJson(jsonElement, Invoice.class);

                    Log.d(TAG, "onEvent: " + order.getDistributor().getName());
                    if (order.isCompleted() != null) { // if arrived is not null and
                        if (!order.isCompleted()) // it's set to false
                            addToList(list, order);
                    } else {    // or arrived is not set
                        addToList(list, order);
                    }
                }
                listItems.addAll(list);
            }
        });
    }

    //    needed because `this` couldn't get properly inside firebase Listener
    private void addToList(List<ProcessingOrdersRecyclerViewAdapter.AdapterDataItem> list, Invoice order) {
        if (order != null) {
            list.add(Utils.convertToOrder(new ItemModel(order), this));
        }
    }

    private ObservableList initList() {
        listItems = new ObservableArrayList<>();
        listItems.add(new ProcessingOrdersRecyclerViewAdapter.AdapterDataItem(R.layout.layout_listitem_heading,
                new Pair<Integer, Object>(BR.headingModel, new HeadingModel("Your Orders"))));
        Log.d(TAG, "initList: " + R.layout.layout_listitem_heading);
        return listItems;
    }

    @Override
    public void onClick(ItemModel itemModel) {  // open map & show directions to warehouse
//        Common.selectedOrder = itemModel.order;
//        if (Common.availabile && Common.mLastLocation != null) {
//            Intent intent = new Intent(getActivity(), DriverTracking.class);
//            startActivity(intent);
//
//        } else {
//            Toast.makeText(getActivity(), "Please enable the availability switch", Toast.LENGTH_SHORT).show();
//        }
    }

    @Override
    public void onDeleteClick(ItemModel itemModel) { // used: only for testing purposes
//        Toast.makeText(getActivity(), "Delete clicked", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onDeleteClick: " + itemModel);

        HashMap<String, Object> data = new HashMap<>();
        data.put("available", true);

        FirebaseFirestore fs = FirebaseFirestore.getInstance();
        FirebaseAuth user = FirebaseAuth.getInstance();
        fs.document("/drivers/" + user.getUid()).set(data, SetOptions.merge());
        fs.document("/ordered-trucks/" + itemModel.order.getCompanyID() + "/ordered-trucks/" + user.getUid())
                .delete();
        fs.document("/drivers/" + user.getUid() + "/orders/" + itemModel.order.getCompanyID()).delete();
    }

    @Override
    public void onExpandClick(ItemModel itemModel) { // not used
        Toast.makeText(getActivity(), "Expand clicked", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLoadMoreClick() { // not used
        Toast.makeText(getActivity(), "loadMore clicked", Toast.LENGTH_SHORT).show();
    }
}