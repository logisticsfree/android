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
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.example.logisticsfree.R;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import org.imperiumlabs.geofirestore.GeoFirestore;

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

//                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("path/to/geofire");
                CollectionReference geoFirestoreRef = FirebaseFirestore.getInstance().collection("driver-location");

                GeoFirestore geoFirestore = new GeoFirestore(geoFirestoreRef);
                Location location = locationResult.getLastLocation();
                if (location != null) {
                    geoFirestore.setLocation(mUser.getUid(), new GeoPoint(location.getLatitude(), location.getLongitude()),
                            new GeoFirestore.CompletionListener() {
                                @Override
                                public void onComplete(Exception exception) {
                                    if (exception == null) {
                                        System.out.println("Location saved on server successfully!");

                                    } else {
                                        Log.d(TAG, "onComplete: " + exception.getMessage());

                                    }
                                }
                            });
                }

//
//                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users").child(mUser.getUid()).child("location");
//                    Location location = locationResult.getLastLocation();
//                    if (location != null) {
//
////Save the location data to the database//
//
//                        ref.setValue(location);
//                    }
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

//Stop the Service//

            stopSelf();
        }
    };

//    private void loginToFirebase() {
//
////Authenticate with Firebase, using the email and password we created earlier//
//
//        String email = getString(R.string.test_email);
//        String password = getString(R.string.test_password);
//
////Call OnCompleteListener if the user is signed in successfully//
//
//        FirebaseAuth.getInstance().signInWithEmailAndPassword(
//                email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//            @Override
//            public void onComplete(Task<AuthResult> task) {
//
////If the user has been authenticated...//
//
//                if (task.isSuccessful()) {
//
////...then call requestLocationUpdates//
//
//                    requestLocationUpdates();
//                } else {
//
////If sign in fails, then log the error//
//
//                    Log.d(TAG, "Firebase authentication failed");
//
//                }
//            }
//        });
//    }

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

            client.requestLocationUpdates(request, locationCallback, null);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        client.removeLocationUpdates(locationCallback);
    }
}
