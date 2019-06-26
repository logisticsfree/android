package com.example.logisticsfree;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.logisticsfree.Common.Common;
import com.example.logisticsfree.Common.Utils;
import com.example.logisticsfree.models.Trip;
import com.example.logisticsfree.trip.TripProcessing;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

import de.codecrafters.tableview.TableDataAdapter;
import de.codecrafters.tableview.TableHeaderAdapter;
import de.codecrafters.tableview.TableView;
import de.codecrafters.tableview.model.TableColumnWeightModel;
import de.codecrafters.tableview.toolkit.SimpleTableDataAdapter;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;

public class LoadingBay extends AppCompatActivity {

    private static final String TAG = "LoadingBay";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_bay);
        Toolbar myToolbar = findViewById(R.id.toolbarLoadingBay);
        setSupportActionBar(myToolbar);
        myToolbar.setTitle("Orders");
        myToolbar.showOverflowMenu();
        Log.d(TAG, "onCreate: " + myToolbar.getTitle());

        Button btnStartTrip = findViewById(R.id.btnStartTrip);

        String[][] myData = new String[][] {};

        final TableDataAdapter<String[]> myDataAdapter =
                new SimpleTableDataAdapter(this, myData);
        TableHeaderAdapter myHeaderAdapter =
                new SimpleTableHeaderAdapter(this, "Invoice No.", "Address");

        TableView<String[]> table = findViewById(R.id.tableOrders);
        table.setColumnCount(2);
        table.setDataAdapter(myDataAdapter);
        table.setHeaderAdapter(myHeaderAdapter);

        TableColumnWeightModel columnModel = new TableColumnWeightModel(2);
        columnModel.setColumnWeight(1, 1);
        columnModel.setColumnWeight(2, 2);
        table.setColumnModel(columnModel);

        FirebaseFirestore fs = FirebaseFirestore.getInstance();
        String mUID = FirebaseAuth.getInstance().getUid();
        String path = "/ordered-trucks/" + Common.selectedOrder.getCompanyID() + "/ordered-trucks/" + mUID;
        fs.document(path).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                Map<String, Object> snap = task.getResult().getData();
                Map orders = (Map) snap.get("orders");

                for ( Object order : orders.values()) {
                    String invoiceNo = (String) ((Map) order).get("invoice");
                    Map distributor = (Map) ((Map) order).get("distributor");
                    String address = (String) distributor.get("name");    //  TODO: change to actually address (should add it in Firebase)
                    myDataAdapter.getData().add(new String[]{invoiceNo, address});
                }

                myDataAdapter.notifyDataSetChanged();
            }
        });

        btnStartTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseFirestore fs = FirebaseFirestore.getInstance();
                String mUID = FirebaseAuth.getInstance().getUid();
                String from = "/ordered-trucks/" + Common.selectedOrder.getCompanyID() + "/ordered-trucks/" + mUID;
                String to = "/trips/";

                final DocumentReference fromDoc = fs.document(from);
                final DocumentReference toDoc = fs.collection(to).document();
                final DocumentReference fromDocDriver = fs.document("/drivers/" + mUID + "/orders/" + Common.selectedOrder.getCompanyID());
                final DocumentReference toDocDriver = fs.document("/drivers/" + mUID + "/trips/" + Common.selectedOrder.getCompanyID());

                Map<String, Object> data = new HashMap<>();
                data.put("driverID", mUID);
                fromDoc.set(data, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful()) return;

                        fromDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                Common.currentTrip = task.getResult().toObject(Trip.class);
                                Utils.moveFirestoreDocument(fromDoc , toDoc);
                                Utils.moveFirestoreDocument(fromDocDriver, toDocDriver);
                                startActivity(new Intent(getApplicationContext(), TripProcessing.class));
                                finish();
                            }
                        });
                    }
                });
            }
        });
    }

}
