package com.example.logisticsfree.services;

import android.content.Intent;
import android.util.Log;

import com.example.logisticsfree.CustomerCall;
import com.example.logisticsfree.models.Token;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class FirebaseMsgService extends FirebaseMessagingService {
    private final String TAG = "FirebaseMessaging";

    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);
        updateTokenToServer(token);
    }

    private void updateTokenToServer(String refreshedToken) {
        FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();

        Token token = new Token(refreshedToken);
        if(FirebaseAuth.getInstance().getCurrentUser()!= null){
            mDatabase.collection("tokens").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).set(token);
        }
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        if (remoteMessage.getData() != null) {
            Map<String, String> data = remoteMessage.getData();
            String customerId = data.get("customerId");
            String lat = data.get("lat");
            String lng = data.get("lng");
            System.out.println("Debug lat" + lat + "lng" + lng);

            Intent intent = new Intent(getBaseContext(), CustomerCall.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("lat", lat);
            intent.putExtra("lng", lng);
            intent.putExtra("customerId", customerId);
            System.out.println("CustomerID: : " + customerId);

            startActivity(intent);
            Log.d(TAG, "onMessageReceived: " + lat + " " + lng);
        }
    }
}
