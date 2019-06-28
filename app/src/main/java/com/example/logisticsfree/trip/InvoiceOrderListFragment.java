package com.example.logisticsfree.trip;

import android.content.Intent;
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
import com.example.logisticsfree.R;
import com.example.logisticsfree.Utils;
import com.example.logisticsfree.adapters.InvoiceOrdersRecyclerViewAdapter;
import com.example.logisticsfree.databinding.FragmentProcessingOrderRecyclerViewBinding;
import com.example.logisticsfree.home.HomeActivity;
import com.example.logisticsfree.models.HeadingModel;
import com.example.logisticsfree.models.Invoice;
import com.example.logisticsfree.models.ItemModel;
import com.example.logisticsfree.models.Trip;
import com.example.logisticsfree.presenters.ListItemsPresenter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InvoiceOrderListFragment extends Fragment implements ListItemsPresenter {
    private final String TAG = "ProcessingFragment";

    private ObservableList<InvoiceOrdersRecyclerViewAdapter.AdapterDataItem> listItems;
    private FirebaseUser mUser;
    private FirebaseFirestore afs;
    private boolean finalInvoice = false;

    public InvoiceOrderListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        afs = FirebaseFirestore.getInstance();
        loadFromFirestore();

        getActivity().setTitle("Orders List");

    }

    @android.support.annotation.Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @android.support.annotation.Nullable ViewGroup container,
                             @android.support.annotation.Nullable Bundle savedInstanceState) {
        FragmentProcessingOrderRecyclerViewBinding mBinding = DataBindingUtil
                .inflate(inflater,
                        R.layout.fragment_processing_order_recycler_view,
                        container, false);
        mBinding.setListLayoutManager(new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false));
        mBinding.setModelList(initList());
        mBinding.setItemAnimator(new DefaultItemAnimator());

        return mBinding.getRoot();
    }

    private void loadFromFirestore() {
        afs.collection("drivers/" + mUser.getUid() + "/trips/")
                .whereEqualTo("active", true).limit(1).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        listItems.clear();
                        listItems.add(new InvoiceOrdersRecyclerViewAdapter.AdapterDataItem(R.layout.layout_listitem_heading,
                                new Pair<Integer, Object>(BR.headingModel,
                                        new HeadingModel("Remaining Invoices"))));

                        List<InvoiceOrdersRecyclerViewAdapter.AdapterDataItem> list = new ArrayList<>();

                        if (queryDocumentSnapshots.size() < 1) {
                            return;
                        }
                        String tripID = Common.currentTrip.getTripID();
                        Common.currentTrip =
                                queryDocumentSnapshots.getDocuments().get(0).toObject(Trip.class);
                        Common.currentTrip.setTripID(tripID);
                        Log.d(TAG,
                                "onSuccess: " + Common.currentTrip.getTripID() + " " + Common.currentTrip.getOrders().values());
                        Map<String, Object> fulDoc =
                                queryDocumentSnapshots.getDocuments().get(0).getData();
                        Map orders = (Map) fulDoc.get("orders");
                        Collection<HashMap> ordersList = orders.values();

                        for (HashMap orderMap : ordersList) {
                            Gson gson = new Gson();
                            JsonElement jsonElement = gson.toJsonTree(orderMap);
                            Invoice order = gson.fromJson(jsonElement,
                                    Invoice.class);

                            if (order.isCompleted() != null) { // if arrived
                                // is not null and
                                if (!order.isCompleted()) { // it's set to false
                                    addToList(list, order);
                                }
                            } else {    // or arrived is not set
                                addToList(list, order);
                            }
                        }

                        Log.d(TAG, "onEvent: InvoicesLeft" + list.size());
                        if (list.size() <= 1) {
                            finalInvoice = true;
                        }

                        listItems.addAll(list);
                    }
                });
    }

    //    needed because `this` couldn't get properly inside firebase Listener
    private void addToList(List<InvoiceOrdersRecyclerViewAdapter.AdapterDataItem> list, Invoice order) {
        if (order != null) {
            if (order.isCompleted() != null) {
                if (!order.isCompleted()) {
                    list.add(Utils.convertToOrder(new ItemModel(order), this));
                }
            } else {
                list.add(Utils.convertToOrder(new ItemModel(order), this));
            }
        }
    }

    private ObservableList initList() {
        listItems = new ObservableArrayList<>();
        listItems.add(new InvoiceOrdersRecyclerViewAdapter.AdapterDataItem(R.layout.layout_listitem_heading,
                new Pair<Integer, Object>(BR.headingModel, new HeadingModel(
                        "Remaining Invoices"))));
        Log.d(TAG, "initList: " + R.layout.layout_listitem_heading);
        return listItems;
    }

    @Override
    public void onClick(ItemModel itemModel) {  // open map & show directions
        // to warehouse
//        Common.selectedOrder = itemModel.order;
//        if (Common.availabile && Common.mLastLocation != null) {
//            Intent intent = new Intent(getActivity(), DriverTracking.class);
//            startActivity(intent);
//
//        } else {
//            Toast.makeText(getActivity(), "Please enable the availability
//            switch", Toast.LENGTH_SHORT).show();
//        }
    }

    @Override
    public void onDeleteClick(ItemModel itemModel) { // used: only for
        // testing purposes

        final FirebaseFirestore fs = FirebaseFirestore.getInstance();
        final String path = "/trips/" + Common.currentTrip.getTripID();

        HashMap<String, Object> completed = new HashMap<>();
        completed.put("completed", true);
        HashMap<String, Object> invoice = new HashMap<>();
        invoice.put(itemModel.invoice.getInvoice(), completed);
        HashMap<String, Object> orders = new HashMap<>();
        orders.put("orders", invoice);
        if (finalInvoice) {
            orders.put("active", false);
        }

//      this will listen by the HomeFragment
        fs.document(path).set(orders, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                fs.document(path).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Common.currentTrip =
                                documentSnapshot.toObject(Trip.class);
                        Common.currentTrip.setTripID(documentSnapshot.getId());
                        if (finalInvoice) {
                            startActivity(new Intent(getActivity(),
                                    TripComplete.class));
                            getActivity().finish();
                        } else {
                            startActivity(new Intent(getActivity(),
                                    TripProcessing.class));
                            getActivity().finish();
                        }
                    }
                });
            }
        });
    }

    @Override
    public void onExpandClick(ItemModel itemModel) { // not used
        Toast.makeText(getActivity(), "Expand clicked", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLoadMoreClick() { // not used
        Toast.makeText(getActivity(), "loadMore clicked", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}