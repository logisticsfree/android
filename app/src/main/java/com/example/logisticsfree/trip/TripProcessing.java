package com.example.logisticsfree.trip;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.example.logisticsfree.Common.Common;
import com.example.logisticsfree.R;
import com.example.logisticsfree.services.TrackingService;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.common.collect.Iterators;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.android.PolyUtil;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.TravelMode;

import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class TripProcessing extends AppCompatActivity implements OnMapReadyCallback {
    private static final String TAG = "TripProcessing";
    private GoogleMap mMap;
    private final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 1;
    private static final int PERMISSIONS_REQUEST = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_processing);

        Button btnUnload = findViewById(R.id.btn_unload);

        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        setTitle("Processing");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        startService(new Intent(this, TrackingService.class));
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        btnUnload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: ");
                startActivity(new Intent(getApplicationContext(), OrderList.class));
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST_FINE_LOCATION);
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            finish();
        }
        mMap.setMyLocationEnabled(true);

        final DirectionsResult[] results = new DirectionsResult[1];
        final double [] lastLocation = new double[2];
        String mUID = FirebaseAuth.getInstance().getUid();
        FirebaseDatabase fb = FirebaseDatabase.getInstance();
        fb.getReference("driver-locations").child(mUID).child("l").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<Double> snap = (ArrayList<Double>) dataSnapshot.getValue();
                lastLocation[0] = snap.get(0);
                lastLocation[1] = snap.get(1);

                Runnable r = new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "run: " + "Start");
                        try {
                            DateTime now = new DateTime();
                            String origin =  lastLocation[0] + ", " + lastLocation[1];
                            String destination = getCoordinates()[0] + ", " + getCoordinates()[1];
                            DirectionsResult result = DirectionsApi.newRequest(getGeoContext())
                                    .mode(TravelMode.DRIVING)
                                    .origin(origin)
                                    .destination(destination)
                                    .departureTime(now)
                                    .await();
                            results[0] = result;
                            Gson gson = new Gson();
                            Log.d(TAG, "run result" + gson.toJson(result));

                        } catch (ApiException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                };

                Thread thread = new Thread(r);
                thread.start();
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Log.d(TAG, "getReference result" + results[0]);

                addPolyline(results[0], mMap);

                final List<Marker> markers = addMarkersToMap(results[0], mMap);

                mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                    @Override
                    public void onMapLoaded() {
                        LatLngBounds.Builder builder = new LatLngBounds.Builder();
                        for (Marker marker : markers) {
                            builder.include(marker.getPosition());
                        }
                        LatLngBounds bounds = builder.build();

                        int padding = 50; // offset from edges of the map in pixels
                        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                        mMap.animateCamera(cu);
//                putMyLocation(mMap);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: " + "I'm F*cked");
            }
        });

    }

    private double[] getCoordinates() {
        HashMap ordersMap = (HashMap) Common.currentTrip.getOrders();
        JSONObject ordersJson = new JSONObject(ordersMap);

        int noOfOrders = Iterators.size(ordersJson.keys());
        double[][] dists = new double[2][noOfOrders];
        int nextDistIndex = 0;
        Log.d(TAG, "getCoordinates: " + ordersJson.keys());
        for (Iterator<String> it = ordersJson.keys(); it.hasNext(); ) {
            String key = it.next();

            try {
                HashMap order = (HashMap) ordersJson.get(key);
                HashMap dist = (HashMap) order.get("distributor");
                int seqNo = (int) (long) order.get("seqNo");
                if (order.get("completed") != null)
                    if (! (boolean) order.get("completed") && seqNo >= nextDistIndex) {
                        nextDistIndex = seqNo;
                    }
                dists[seqNo] = new double[] {(double) dist.get("latitude"), (double) dist.get("longitude")};
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return dists[nextDistIndex];
    }

    private List<Marker> addMarkersToMap(DirectionsResult results, GoogleMap mMap) {
        List<Marker> markers = new ArrayList<>();

        markers.add(mMap.addMarker(new MarkerOptions().position(
                new LatLng(results.routes[0].legs[0].startLocation.lat,
                        results.routes[0].legs[0].startLocation.lng))
                .title(results.routes[0].legs[0].startAddress)
        ));
        for (DirectionsLeg leg : results.routes[0].legs) {
            markers.add(mMap.addMarker(new MarkerOptions().position(
                    new LatLng(leg.endLocation.lat, leg.endLocation.lng))
                    .title(leg.startAddress).snippet(getEndLocationTitle(leg))));
        }
        return markers;
    }

    private String getEndLocationTitle(DirectionsLeg leg) {
        return "Time :" + leg.duration.humanReadable + " Distance :" + leg.distance.humanReadable;
    }

    private void addPolyline(DirectionsResult result, GoogleMap mMap) {
        List<LatLng> decodedPath = PolyUtil.decode(result.routes[0].overviewPolyline.getEncodedPath());
        mMap.addPolyline(new PolylineOptions().addAll(decodedPath));
    }

    private GeoApiContext getGeoContext() {
        GeoApiContext geoApiContext = new GeoApiContext();
        return geoApiContext.setQueryRateLimit(3)
                .setApiKey(getString(R.string.directionsApiKey))
                .setConnectTimeout(5, TimeUnit.SECONDS)
                .setReadTimeout(5, TimeUnit.SECONDS)
                .setWriteTimeout(5, TimeUnit.SECONDS);
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_actionbar, menu);

        final Switch availableSwitch = menu.findItem(R.id.action_set_availability).getActionView().findViewById(R.id.switchForActionBar);

        assert availableSwitch != null;
        availableSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (!startTrackerService()) {
                        availableSwitch.setChecked(false);
                    }
                    Common.availabile = true;
                } else {
                    stopTrackerService();
                    Common.availabile = false;
                }
            }
        });

        if (startTrackerService()) {
            availableSwitch.setChecked(true);
            Common.availabile = true;
        }

        return true;
    }
    private boolean startTrackerService() {
        //Check whether GPS tracking is enabled//
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "Please enable Location Service", Toast.LENGTH_LONG).show();
//            finish();
            return false;
        }

        //Check whether this app has access to the location permission//
        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        //If the location permission has been granted, then start the TrackerService//

        if (permission == PackageManager.PERMISSION_GRANTED) {
            startService(new Intent(this, TrackingService.class));

            Toast.makeText(this, "GPS tracking enabled", Toast.LENGTH_LONG).show();
            return true;
//            finish();
        } else {

//If the app doesn’t currently have access to the user’s location, then request access//
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST);
            return false;
        }
    }
    private void stopTrackerService() {
        stopService(new Intent(this, TrackingService.class));
        Toast.makeText(this, "GPS tracking disabled", Toast.LENGTH_LONG).show();
    }

}
