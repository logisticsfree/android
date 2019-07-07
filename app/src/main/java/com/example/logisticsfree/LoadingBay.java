package com.example.logisticsfree;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.example.logisticsfree.Common.Common;
import com.example.logisticsfree.Common.Utils;
import com.example.logisticsfree.models.Trip;
import com.example.logisticsfree.trip.TripProcessing;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
        setTitle("Invoices");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        myToolbar.showOverflowMenu();

        Button btnStartTrip = findViewById(R.id.btnStartTrip);

        String[][] myData = new String[][]{};

        final TableDataAdapter<String[]> myDataAdapter = new SimpleTableDataAdapter(this, myData);
        TableHeaderAdapter myHeaderAdapter = new SimpleTableHeaderAdapter(this, "Invoice No.", "Address");

        TableView<String[]> table = findViewById(R.id.tableOrders);
        table.setColumnCount(2);
        table.setDataAdapter(myDataAdapter);
        table.setHeaderAdapter(myHeaderAdapter);

        TableColumnWeightModel columnModel = new TableColumnWeightModel(2);
        columnModel.setColumnWeight(1, 1);
        columnModel.setColumnWeight(2, 2);
        table.setColumnModel(columnModel);

        FirebaseFirestore fs = FirebaseFirestore.getInstance();
        String path = "/trips/" + Common.selectedOrder.getTripID();

        fs.document(path).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                Map<String, Object> snap = task.getResult().getData();
                Map orders = (Map) snap.get("orders");

                for (Object order : orders.values()) {
                    String invoiceNo = (String) ((Map) order).get("invoice");
                    Map distributor = (Map) ((Map) order).get("distributor");
                    String address = (String) distributor.get("name");    //
                    // TODO: change to actually address (should add a field
                    //  on Firebase)
                    myDataAdapter.getData().add(new String[]{invoiceNo,
                            address});
                }

                myDataAdapter.notifyDataSetChanged();
            }
        });

        btnStartTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final FirebaseFirestore fs = FirebaseFirestore.getInstance();
                final String path = "trips/" + Common.selectedOrder.getTripID();

                final Map<String, Object> data = new HashMap<>();
                data.put("status", 3);

                fs.document(path).set(data, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful()) return;

                        fs.document(path).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                Common.currentTrip = documentSnapshot.toObject(Trip.class);
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
