package com.example.logisticsfree.services;

import android.Manifest;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.IBinder;
import android.os.Looper;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.example.logisticsfree.Common.Common;
import com.example.logisticsfree.R;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;


public class TrackingService extends Service {
    private final String TAG = "TrackingService";
    LocationCallback locationCallback;
    LocationRequest request;
    FusedLocationProviderClient client;

    public TrackingService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
//        buildNotification();
//        loginToFirebase();

        buildLocationCallback();
        requestLocationUpdates();

    }

    private void buildLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(final LocationResult locationResult) {

//Get a reference to the database, so your app can perform read and write operations//

                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                FirebaseUser mUser = mAuth.getCurrentUser();
                if (mUser == null) return;

                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("driver-locations");
                GeoFire geoFire = new GeoFire(ref);

                Location location = locationResult.getLastLocation();
                if (location != null) {
                    Common.mLastLocation = location;

                    geoFire.setLocation(mUser.getUid(), new GeoLocation(location.getLatitude(), location.getLongitude()),
                            new GeoFire.CompletionListener() {
                                @Override
                                public void onComplete(String key, DatabaseError exception) {
                                    if (exception == null) {
                                        System.out.println("Location saved on server successfully!");

                                    } else {
                                        Log.d(TAG, "onComplete: " + exception.getMessage());
                                    }
                                }
                            });
                }
            }
        };
    }

    //Create the persistent notification//

//    private void buildNotification() {
//        String stop = "stop";
//        registerReceiver(stopReceiver, new IntentFilter(stop));
//        PendingIntent broadcastIntent = PendingIntent.getBroadcast(
//                this, 0, new Intent(stop), PendingIntent.FLAG_UPDATE_CURRENT);
//
//// Create the persistent notification
//        Notification.Builder builder = new Notification.Builder(this)
//                .setContentTitle(getString(R.string.app_name))
//                .setContentText(getString(R.string.tracking_enabled_notif))
//
////Make this notification ongoing so it can’t be dismissed by the user//
//
//                .setOngoing(true)
//                .setContentIntent(broadcastIntent);
//        startForeground(1, builder.build());
//
////        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "M_CH_ID");
////
////        notificationBuilder.setAutoCancel(true)
////                .setOngoing(true)
////                .setContentIntent(broadcastIntent)
////                .setContentTitle(getString(R.string.app_name))
////                .setSmallIcon(R.drawable.ic_launcher_background)
////                .setContentText(getString(R.string.tracking_enabled_notif));
////
//////                .setContentInfo("Info");
////        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
////        notificationManager.notify(1, notificationBuilder.build());
//    }

    protected BroadcastReceiver stopReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
//Unregister the BroadcastReceiver when the notification is tapped//
            unregisterReceiver(stopReceiver);
            stopSelf();
        }
    };

    //Initiate the request to track the device's location//

    private void requestLocationUpdates() {
        request = new LocationRequest();

//Specify how often your app should request the device’s location//

        request.setInterval(5000);

//Get the most accurate location data available//

        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        client = LocationServices.getFusedLocationProviderClient(this);
//        final String path = getString(R.string.firebase_path);
        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);

//If the app currently has access to the location permission...//

        if (permission == PackageManager.PERMISSION_GRANTED) {

//...then request location updates//

            client.requestLocationUpdates(request, locationCallback, Looper.getMainLooper());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        client.removeLocationUpdates(locationCallback);
        stopSelf();
    }
}
