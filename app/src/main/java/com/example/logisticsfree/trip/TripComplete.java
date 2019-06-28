package com.example.logisticsfree.trip;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.logisticsfree.Common.Common;
import com.example.logisticsfree.Common.Utils;
import com.example.logisticsfree.R;
import com.example.logisticsfree.home.HomeActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;

public class TripComplete extends AppCompatActivity {
    private FirebaseUser mUser;
    private FirebaseFirestore afs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_complete);
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        afs = FirebaseFirestore.getInstance();
        Button btnDissmissCompleteTrip =
                findViewById(R.id.btn_dismiss_complete_trip);

        btnDissmissCompleteTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                finish();
            }
        });

        finalizeTrip();
    }

    private void finalizeTrip() {
        String fromDriverTrips =
                "drivers/" + mUser.getUid() + "/trips/" + Common.currentTrip.getCompanyID();
        String toDriverTrips = "drivers/" + mUser.getUid() + "/completed" +
                "-trips/" + Common.currentTrip.getTripID();
        DocumentReference fromDriverTripsRef = afs.document(fromDriverTrips);
        DocumentReference toDriverTripsRef = afs.document(toDriverTrips);

        String fromTrips = "trips/" + Common.currentTrip.getTripID();
        String toTrips = "completed-trips/" + Common.currentTrip.getTripID();

        DocumentReference fromTripsRef = afs.document(fromTrips);
        DocumentReference toTripsRef = afs.document(toTrips);

        Utils.moveFirestoreDocument(fromDriverTripsRef, toDriverTripsRef);
        Utils.moveFirestoreDocument(fromTripsRef, toTripsRef);

        HashMap<String, Object> available = new HashMap<>();
        available.put("available", true);
        afs.document("drivers/" + mUser.getUid()).set(available, SetOptions.merge());
    }
}
