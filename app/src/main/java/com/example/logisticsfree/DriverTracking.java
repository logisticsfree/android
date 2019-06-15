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
import android.widget.Toast;

import com.example.logisticsfree.Common.Common;
import com.example.logisticsfree.models.Order;
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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class DriverTracking extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener {

    private final String TAG = "MapActivity";
    private final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 1;

    private GoogleMap mMap;
    private Order currentOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_tracking);
        currentOrder = Common.selectedOrder;

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);

        final DirectionsResult[] results = new DirectionsResult[1];
        Runnable r = new Runnable() {

            @Override
            public void run() {
                try {
                    DateTime now = new DateTime();
//                    String warehouse = (String) getCoordinates().get("warehouse");
                    String origin =  Common.mLastLocation.getLatitude() + ", " + Common.mLastLocation.getLongitude();
                    String destination = (String) getCoordinates().get("warehouse");
                    String[] waypoints = (String[]) getCoordinates().get("waypoints");
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
    }

    private HashMap<String, Serializable> getCoordinates() {
        HashMap warehouseMap = (HashMap) currentOrder.getOrdersJson().get("warehouse");
        String warehouse = warehouseMap.get("latitude") + ", " + warehouseMap.get("longitude");

        HashMap ordersMap = (HashMap) currentOrder.getOrdersJson().get("orders");
        JSONObject ordersJson = new JSONObject(ordersMap);

        int noOfOrders = Iterators.size(ordersJson.keys());
        String[] orders = new String[noOfOrders];

        for (Iterator<String> it = ordersJson.keys(); it.hasNext(); ) {
            String key = it.next();

            try {
                HashMap order = (HashMap) ordersJson.get(key);
                HashMap dist = (HashMap) order.get("distributor");
                orders[(int) (long) order.get("seqNo")] = dist.get("latitude") + "," + dist.get("longitude");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        HashMap<String, java.io.Serializable> coordinates = new HashMap<>();
        coordinates.put("warehouse", warehouse);
        coordinates.put("destination", orders[noOfOrders - 1]);
        coordinates.put("waypoints", Arrays.copyOf(orders, noOfOrders - 1));
        return coordinates;
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
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
                .setConnectTimeout(1, TimeUnit.SECONDS)
                .setReadTimeout(1, TimeUnit.SECONDS)
                .setWriteTimeout(1, TimeUnit.SECONDS);
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
