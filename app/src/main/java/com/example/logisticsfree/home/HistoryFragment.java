package com.example.logisticsfree.home;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.logisticsfree.BR;
import com.example.logisticsfree.Common.Common;
import com.example.logisticsfree.R;
import com.example.logisticsfree.Utils;
import com.example.logisticsfree.WaitingActivity;
import com.example.logisticsfree.adapters.RecyclerViewBindingAdapter;
import com.example.logisticsfree.databinding.FragmentHistoryBinding;
import com.example.logisticsfree.models.HeadingModel;
import com.example.logisticsfree.models.ItemModel;
import com.example.logisticsfree.models.Order;
import com.example.logisticsfree.models.Trip;
import com.example.logisticsfree.presenters.ListItemsPresenter;
import com.example.logisticsfree.trip.TripProcessing;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends Fragment implements ListItemsPresenter {

    private static final String TAG = "HistoryFragment";
    private ObservableList<RecyclerViewBindingAdapter.AdapterDataItem> listItems;
    private FirebaseUser mUser;
    private FirebaseFirestore fs;

    public HistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        fs = FirebaseFirestore.getInstance();

        getActivity().setTitle("History");
        loadFromFirestore();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentHistoryBinding mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_history, container, false);
        mBinding.setListLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mBinding.setModelList(initList());
        mBinding.setItemAnimator(new DefaultItemAnimator());

        return mBinding.getRoot();
    }

    private void loadFromFirestore() {
        fs.collection("drivers/" + mUser.getUid() + "/completed-trips/").get()
            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                listItems.clear();

                List<RecyclerViewBindingAdapter.AdapterDataItem> list = new ArrayList<>();
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    addToList(list, doc);
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
            list.add(Utils.convert(new ItemModel(order), this));
        }
    }

    private ObservableList initList() {
        listItems = new ObservableArrayList<>();
        listItems.add(new RecyclerViewBindingAdapter.AdapterDataItem(R.layout.layout_listitem_heading,
                new Pair<Integer, Object>(BR.headingModel, new HeadingModel("History"))));
        return listItems;
    }

    @Override
    public void onClick(ItemModel itemModel) {

    }

    @Override
    public void onDeleteClick(ItemModel itemModel) {

    }

    @Override
    public void onExpandClick(ItemModel itemModel) {

    }

    @Override
    public void onLoadMoreClick() {

    }
}
