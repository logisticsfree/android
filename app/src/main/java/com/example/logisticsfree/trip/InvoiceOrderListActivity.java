package com.example.logisticsfree.trip;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.example.logisticsfree.Common.Common;
import com.example.logisticsfree.R;
import com.example.logisticsfree.services.TrackingService;

public class InvoiceOrderListActivity extends AppCompatActivity {
    private static final int PERMISSIONS_REQUEST = 100;
    private static final String TAG = "InvoiceOrderListActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);

        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setFragment(new InvoiceOrderListFragment());

    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction =
                getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.processing_order_main_frame, fragment);
        fragmentTransaction.commit();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_actionbar, menu);

        final Switch availableSwitch =
                menu.findItem(R.id.action_set_availability).getActionView().findViewById(R.id.switchForActionBar);

        assert availableSwitch != null;
        availableSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
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

        return true;
    }

    //    not using
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_set_availability:
                Log.d(TAG, "onOptionsItemsSelected: asdfasfd");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void stopTrackerService() {
        stopService(new Intent(this, TrackingService.class));
        Toast.makeText(this, "GPS tracking disabled", Toast.LENGTH_LONG).show();
    }

    private boolean startTrackerService() {
        //Check whether GPS tracking is enabled//
        LocationManager lm =
                (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "Please enable Location Service",
                    Toast.LENGTH_LONG).show();
//            finish();
            return false;
        }

        //Check whether this app has access to the location permission//
        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        //If the location permission has been granted, then start the
        // TrackerService//

        if (permission == PackageManager.PERMISSION_GRANTED) {
            startService(new Intent(this, TrackingService.class));

            return true;
//            finish();
        } else {

//If the app doesn’t currently have access to the user’s location, then
// request access//
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST);
            return false;
        }
    }
}
