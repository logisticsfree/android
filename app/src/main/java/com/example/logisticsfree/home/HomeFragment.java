package com.example.logisticsfree.home;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import com.example.logisticsfree.Common.Common;
import com.example.logisticsfree.DriverTracking;
import com.example.logisticsfree.R;
import com.example.logisticsfree.trip.TripProcessing;
import com.example.logisticsfree.Utils;
import com.example.logisticsfree.WaitingActivity;
import com.example.logisticsfree.adapters.RecyclerViewBindingAdapter;
import com.example.logisticsfree.databinding.FragmentHomeBinding;
import com.example.logisticsfree.models.HeadingModel;
import com.example.logisticsfree.models.ItemModel;
import com.example.logisticsfree.models.Order;
import com.example.logisticsfree.models.Trip;
import com.example.logisticsfree.presenters.ListItemsPresenter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HomeFragment extends Fragment implements ListItemsPresenter {
    private final String TAG = "HomeFragment";

    private ObservableList<RecyclerViewBindingAdapter.AdapterDataItem> listItems;
    private FirebaseUser mUser;
    private FirebaseFirestore afs;

    private BroadcastReceiver broadcastReceiver;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        afs = FirebaseFirestore.getInstance();
        loadFromFirestore();
        checkOrderProcessingStatus();

        getActivity().setTitle("Orders");

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent intent) {
                String action = intent.getAction();
                if (action.equals("finish_fragment_home")) {
                    getActivity().finish();
                }
            }
        };
        getActivity().registerReceiver(broadcastReceiver, new IntentFilter("finish_fragment_home"));
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(broadcastReceiver);
    }

    @android.support.annotation.Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @android.support.annotation.Nullable ViewGroup container,
                             @android.support.annotation.Nullable Bundle savedInstanceState) {
        FragmentHomeBinding mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        mBinding.setListLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mBinding.setModelList(initList());
        mBinding.setItemAnimator(new DefaultItemAnimator());

        return mBinding.getRoot();
    }

    private void checkOrderProcessingStatus() {
        afs.collection("drivers/" + mUser.getUid() + "/orders/")
                .whereEqualTo("arrived", true).limit(1).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.size() < 1) {
                            checkForProcessingTrip();
                        } else {
                            Common.selectedOrder = queryDocumentSnapshots.getDocuments().get(0).toObject(Order.class);
                            Intent intent = new Intent(getContext(), WaitingActivity.class);
                            startActivity(intent);
                        }
                    }
                });
    }

    private void checkForProcessingTrip() {
        afs.collection("trips")
                .whereEqualTo("driverID", mUser.getUid()).limit(1).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        Log.d(TAG, "onSuccess: " + queryDocumentSnapshots.size());
                        if (queryDocumentSnapshots.size() < 1) {
                            return;
                        } else {
                            Common.currentTrip = queryDocumentSnapshots.getDocuments().get(0).toObject(Trip.class);
                            Log.d(TAG, "onSuccess: " + queryDocumentSnapshots.getDocuments());

                            Intent intent = new Intent(getContext(), TripProcessing.class);
                            startActivity(intent);
                            getActivity().finish();
                        }
                    }
                });
    }

    private void loadFromFirestore() {
        afs.collection("drivers/" + mUser.getUid() + "/orders/").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snap, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }
                listItems.clear();
                listItems.add(new RecyclerViewBindingAdapter.AdapterDataItem(R.layout.layout_listitem_heading,
                        new Pair<Integer, Object>(BR.headingModel, new HeadingModel("Your Orders"))));

                List<RecyclerViewBindingAdapter.AdapterDataItem> list = new ArrayList<>();
                for (QueryDocumentSnapshot doc : snap) {
                    if (doc.getBoolean("arrived") != null) { // if arrived is not null and
                        if (!doc.getBoolean("arrived")) // it's set to false
                            addToList(list, doc);
                    } else {    // or arrived is not set
                        addToList(list, doc);
                    }
                }
                listItems.addAll(list);
            }
        });
    }

    //    needed because `this` couldn't get properly inside firebase Listener
    private void addToList(List<RecyclerViewBindingAdapter.AdapterDataItem> list, QueryDocumentSnapshot doc) {
        if (doc.exists()) {
            Order order = doc.toObject(Order.class);
            order.setOrdersJson(doc.getData());
            Log.d(TAG, "addToList: " + order.getOrdersJson());
            list.add(Utils.convert(new ItemModel(order), this));
        }
    }

    private ObservableList initList() {
        listItems = new ObservableArrayList<>();
        listItems.add(new RecyclerViewBindingAdapter.AdapterDataItem(R.layout.layout_listitem_heading,
                new Pair<Integer, Object>(BR.headingModel, new HeadingModel("Your Orders"))));
        return listItems;
    }

    @Override
    public void onClick(ItemModel itemModel) {  // open map & show directions to warehouse
        Common.selectedOrder = itemModel.order;
        if (Common.availabile && Common.mLastLocation != null) {
            Intent intent = new Intent(getActivity(), DriverTracking.class);
            startActivity(intent);

        } else {
            Toast.makeText(getActivity(), "Please enable the availability switch", Toast.LENGTH_SHORT).show();
        }
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