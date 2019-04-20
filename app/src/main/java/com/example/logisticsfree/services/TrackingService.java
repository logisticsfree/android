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

public class TrackingService extends Service {
    private final String TAG = "TrackingService";

    public TrackingService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        buildNotification();
//        loginToFirebase();
        requestLocationUpdates();

    }

    //Create the persistent notification//

    private void buildNotification() {
        String stop = "stop";
        registerReceiver(stopReceiver, new IntentFilter(stop));
        PendingIntent broadcastIntent = PendingIntent.getBroadcast(
                this, 0, new Intent(stop), PendingIntent.FLAG_UPDATE_CURRENT);

// Create the persistent notification
        Notification.Builder builder = new Notification.Builder(this)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.tracking_enabled_notif))

//Make this notification ongoing so it can’t be dismissed by the user//

                .setOngoing(true)
                .setContentIntent(broadcastIntent);
        startForeground(1, builder.build());

//        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "M_CH_ID");
//
//        notificationBuilder.setAutoCancel(true)
//                .setOngoing(true)
//                .setContentIntent(broadcastIntent)
//                .setContentTitle(getString(R.string.app_name))
//                .setSmallIcon(R.drawable.ic_launcher_background)
//                .setContentText(getString(R.string.tracking_enabled_notif));
//
////                .setContentInfo("Info");
//        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
//        notificationManager.notify(1, notificationBuilder.build());
    }

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
        LocationRequest request = new LocationRequest();

//Specify how often your app should request the device’s location//

        request.setInterval(10000);

//Get the most accurate location data available//

        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);
//        final String path = getString(R.string.firebase_path);
        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);

//If the app currently has access to the location permission...//

        if (permission == PackageManager.PERMISSION_GRANTED) {

//...then request location updates//

            client.requestLocationUpdates(request, new LocationCallback() {
                @Override
                public void onLocationResult(final LocationResult locationResult) {

//Get a reference to the database, so your app can perform read and write operations//

                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    FirebaseUser mUser = mAuth.getCurrentUser();
                    if (mUser == null) return;

                   DatabaseReference geoFireRef = FirebaseDatabase.getInstance().getReference("driver-locations");


                    GeoFire geoFire = new GeoFire(geoFireRef);
                    Location location = locationResult.getLastLocation();
                    if (location != null) {
//                        geoFirestore.setLocation(mUser.getUid(), new GeoPoint(location.getLatitude(), location.getLongitude()),
//                                new GeoFirestore.CompletionListener() {
//                                    @Override
//                                    public void onComplete(Exception exception) {
//                                        if (exception == null) {
//                                            System.out.println("Location saved on server successfully!");
//
//                                        } else {
//                                            Log.d(TAG, "onComplete: " + exception.getMessage());
//
//                                        }
//                                    }
//                                });
                        geoFire.setLocation(mUser.getUid(), new GeoLocation(location.getLatitude(), location.getLongitude()),  new GeoFire.CompletionListener() {
                            @Override
                            public void onComplete(String key, DatabaseError error) {
                                if (error != null) {
                                    System.err.println("There was an error saving the location to GeoFire: " + error);
                                } else {
                                    System.out.println("Location saved on server successfully!");
                                }
                            }
                        });
                    }

                }
            }, null);
        }
    }
}
