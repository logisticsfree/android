package com.example.logisticsfree;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.logisticsfree.Common.Common;
import com.example.logisticsfree.services.TrackingService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

public class WaitingActivity extends AppCompatActivity {

    private static final String TAG = "WaitingActivity";
    private ListenerRegistration registration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting);
        final TextView txtBayName = findViewById(R.id.txtBayName);
        final TextView txtInstructions = findViewById(R.id.txtWaitForInstructions);
        final Button btnStartLoading = findViewById(R.id.btnStartLoading);
        final TextView txtProceedToBay = findViewById(R.id.txtProceedToBay);

        Glide.with(this)
//                .load("https://cdn.dribbble.com/users/891352/screenshots/3675943/lego-loader.gif")
                .load(R.raw.lego_loader)
                .into( (ImageView) findViewById(R.id.wait_for_instructions) );

        Intent intent2 = new Intent("finish_fragment_home");
        sendBroadcast(intent2);

        startService(new Intent(this, TrackingService.class));

        FirebaseFirestore fs = FirebaseFirestore.getInstance();
        String mUID = FirebaseAuth.getInstance().getUid();
        String orderPath = "/ordered-trucks/" + Common.selectedOrder.getCompanyID() + "/ordered-trucks/" + mUID;
        registration = fs.document(orderPath).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null || !documentSnapshot.exists() ) {
                    return;
                }
                Map<String, Object> order = documentSnapshot.getData();
                if (order.containsKey("bay")) {
                    Map bay = (HashMap) order.get("bay");
                    String bayName = (String) bay.get("name");

                    txtBayName.setText("# " + bayName);
                    txtInstructions.setVisibility(View.GONE);
                    btnStartLoading.setVisibility(View.VISIBLE);
                    txtProceedToBay.setVisibility(View.VISIBLE);
                    txtBayName.setVisibility(View.VISIBLE);
                }
            }
        });

        btnStartLoading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoadingBay.class);
                startActivity(intent);
                finish();
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(this, TrackingService.class));
        registration.remove();
    }
}
