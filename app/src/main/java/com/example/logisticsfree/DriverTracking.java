package com.example.logisticsfree;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.logisticsfree.Common.Common;
import com.example.logisticsfree.models.Order;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.gson.Gson;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.android.PolyUtil;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.TravelMode;

import org.joda.time.DateTime;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class DriverTracking extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener {

    private final String TAG = "MapActivity";
    private final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 1;

    private GoogleMap mMap;
    private Order currentOrder;

    Button arrivedButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_tracking);

        arrivedButton = findViewById(R.id.btn_arrived);
        currentOrder = Common.selectedOrder;

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        arrivedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAsArrived();
                displayWaitingScreen();

            }
        });
    }

    private void displayWaitingScreen() {
//        ProgressDialog dialog = new ProgressDialog(this);
//        dialog.setMessage("message");
//        dialog.setCancelable(false);
//        dialog.setInverseBackgroundForced(false);
//        dialog.show();
        Intent intent = new Intent(this, WaitingActivity.class);
        startActivity(intent);
        finish();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
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
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);

        final DirectionsResult[] results = new DirectionsResult[1];
        Runnable r = new Runnable() {

            @Override
            public void run() {
                try {
                    DateTime now = new DateTime();
                    String origin =
                            Common.mLastLocation.getLatitude() + ", " + Common.mLastLocation.getLongitude();
                    String destination = (String) getCoordinates().get("warehouse");
                    DirectionsResult result = DirectionsApi.newRequest(getGeoContext())
                            .mode(TravelMode.DRIVING)
                            .origin(origin)
                            .destination(destination)
                            .departureTime(now)
                            .await();
                    results[0] = result;
                    Gson gson = new Gson();
                    Log.d(TAG, gson.toJson(result));

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
        Log.d(TAG, String.valueOf(results[0].routes.length));

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

//        when driver arrived (close: 500m) to warehouse display 'arrived' button
        GeoFire geoFire = new GeoFire(FirebaseDatabase.getInstance().getReference("/driver" +
                "-locations"));
        HashMap warehouseMap = (HashMap) currentOrder.getOrdersJson().get("warehouse");

        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation((double) warehouseMap.get(
                "latitude"), (double) warehouseMap.get("longitude")), 0.5f);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                arrivedButton.setVisibility(View.VISIBLE);
//                setAsArrived();
            }

            @Override
            public void onKeyExited(String key) {
                arrivedButton.setVisibility(View.GONE);
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
            }

            @Override
            public void onGeoQueryReady() {
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {
            }
        });
    }

    private void setAsArrived() {
        FirebaseFirestore fs = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String driverOrderPath =
                "drivers/" + user.getUid() + "/orders/" + currentOrder.getCompanyID();
        String orderedTruckPath = "ordered-trucks/" + currentOrder.getCompanyID() + "/ordered" +
                "-trucks/" + user.getUid();

        Map<String, Object> data = new HashMap<>();
        data.put("arrived", true);

//      set drivers/truckID/orders/companyID->arrived: true
//        and ordered-trucks/companyID/ordered-trucks/truckID
//        not using firebase function inverseSync, bc it causes infinite cycles
//        ng app should handle it from their
        fs.document(driverOrderPath).set(data, SetOptions.merge());
        fs.document(orderedTruckPath).set(data, SetOptions.merge());

    }

    private HashMap<String, Serializable> getCoordinates() {
        HashMap warehouseMap = (HashMap) currentOrder.getOrdersJson().get("warehouse");
        String warehouse = warehouseMap.get("latitude") + ", " + warehouseMap.get("longitude");

        HashMap<String, java.io.Serializable> coordinates = new HashMap<>();
        coordinates.put("warehouse", warehouse);
        return coordinates;
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startActivity(new Intent(this, this.getClass()));
                } else {
                    finish();
                }
            }
        }
    }

    private GeoApiContext getGeoContext() {
        GeoApiContext geoApiContext = new GeoApiContext();
        return geoApiContext.setQueryRateLimit(3)
                .setApiKey(getString(R.string.directionsApiKey))
                .setConnectTimeout(5, TimeUnit.SECONDS)
                .setReadTimeout(5, TimeUnit.SECONDS)
                .setWriteTimeout(5, TimeUnit.SECONDS);
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
        List<LatLng> decodedPath =
                PolyUtil.decode(result.routes[0].overviewPolyline.getEncodedPath());
        mMap.addPolyline(new PolylineOptions().addAll(decodedPath));
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "Current Location", Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
    }
}
