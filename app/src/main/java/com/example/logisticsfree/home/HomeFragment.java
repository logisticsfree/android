package com.example.logisticsfree.home;

import android.databinding.DataBindingUtil;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;
import android.os.Bundle;
import android.os.Handler;
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
import com.example.logisticsfree.adapters.RecyclerViewBindingAdapter;
import com.example.logisticsfree.databinding.FragmentHomeBinding;
import com.example.logisticsfree.models.HeadingModel;
import com.example.logisticsfree.models.ItemModel;
import com.example.logisticsfree.models.Order;
import com.example.logisticsfree.presenters.ListItemsPresenter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements ListItemsPresenter {
    private static final String KEY_LAYOUT_MANAGER = "layoutManager";
    private final String TAG = "HomeFragment";
    protected List<Order> mDataset;
    List<RecyclerViewBindingAdapter.AdapterDataItem> list;

    private FragmentHomeBinding mBinding;
    private ObservableList<RecyclerViewBindingAdapter.AdapterDataItem> listItems;

    private FirebaseUser mUser;
    private FirebaseFirestore afs;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        afs = FirebaseFirestore.getInstance();
//        loadDataWithDelay(1500);
        loadFromFirestore();
    }

    @android.support.annotation.Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @android.support.annotation.Nullable ViewGroup container,
                             @android.support.annotation.Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        mBinding.setListLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mBinding.setModelList(initList());
        mBinding.setItemAnimator(new DefaultItemAnimator());

        return mBinding.getRoot();
    }

    private void loadFromFirestore() {
        afs.collection("drivers/" + mUser.getUid() + "/orders/").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snap, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }
                List<RecyclerViewBindingAdapter.AdapterDataItem> list = new ArrayList<>();
                for (QueryDocumentSnapshot doc : snap) {
                    addToList(list, doc);
                }
                listItems.addAll(listItems.size() -1, list);
            }
        });
    }

    private void loadDataWithDelay(int delayMilli) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                listItems.addAll(listItems.size() - 1, getItems());//insert before loading cell
                listItems.remove(listItems.size() - 1);//remove loading cell
                listItems.add(
                        new RecyclerViewBindingAdapter.AdapterDataItem(
                                R.layout.layout_listitem_load_more,
                                new Pair<Integer, Object>(BR.presenter, HomeFragment.this))
                );
            }
        }, delayMilli);
    }

    private ObservableList initList() {
        listItems = new ObservableArrayList<>();
        listItems.add(new RecyclerViewBindingAdapter.AdapterDataItem(R.layout.layout_listitem_heading,
                new Pair<Integer, Object>(BR.headingModel, new HeadingModel("Content Heading"))));

        listItems.add(new RecyclerViewBindingAdapter.AdapterDataItem(R.layout.layout_listitem_loading));
        return listItems;
    }

    private List<RecyclerViewBindingAdapter.AdapterDataItem> getItems() {
        list = new ArrayList<>();
        afs.collection("drivers/" + mUser.getUid() + "/orders/").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snap, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }
                for (QueryDocumentSnapshot doc : snap) {
                    addToList(list, doc);
                }
            }
        });
        return list;
    }
    private void addToList(List<RecyclerViewBindingAdapter.AdapterDataItem> list, QueryDocumentSnapshot doc) {
        if (doc.exists()) {
            Order order = doc.toObject(Order.class);
            list.add(Utils.convert(new ItemModel(order), this));
        }
    }

    @Override
    public void onClick(ItemModel itemModel) {
        Toast.makeText(getActivity(), "itemModel clicked", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDeleteClick(ItemModel itemModel) {
        Toast.makeText(getActivity(), "Delete clicked", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onExpandClick(ItemModel itemModel) {
        Toast.makeText(getActivity(), "Expand clicked", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLoadMoreClick() {
        Toast.makeText(getActivity(), "loadMore clicked", Toast.LENGTH_SHORT).show();
    }
}