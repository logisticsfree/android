package com.example.logisticsfree.home;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
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
import android.widget.FrameLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.example.logisticsfree.Common.Common;
import com.example.logisticsfree.R;
import com.example.logisticsfree.models.Token;
import com.example.logisticsfree.services.TrackingService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

public class HomeActivity extends AppCompatActivity {
    private final String TAG = "HomeAcivity";
    private BottomNavigationView mMainNav;
    private FrameLayout mMainFrame;
    FirebaseFirestore mDatabase;
    FirebaseAuth mAuth;
    FirebaseUser mUser;

    private HistoryFragment historyFragment;
    private RattingFragment rattingFragment;
    private SettingFragment settingFragment;
    private HomeFragment homeFragment;

    private static final int PERMISSIONS_REQUEST = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mDatabase = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        if (mUser == null) finish();

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed",
                                    task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();
                        updateTokenToServer(token);
                    }
                });

        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        setTitle("Orders");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        startService(new Intent(this, TrackingService.class));

        mMainFrame = findViewById(R.id.main_frame);
        mMainNav = findViewById(R.id.main_nav);
        historyFragment = new HistoryFragment();
        rattingFragment = new RattingFragment();
        settingFragment = new SettingFragment();
        homeFragment = new HomeFragment();

        mMainNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.nav_home: {
                        setFragment(homeFragment);
                        return true;
                    }
                    case R.id.nav_history: {
                        setFragment(historyFragment);
                        return true;
                    }
                    case R.id.nav_rating: {
                        setFragment(rattingFragment);
                        return true;
                    }
                    case R.id.nav_setting: {
                        setFragment(settingFragment);
                        return true;
                    }
                    default:
                        return false;
                }
            }

            private void setFragment(Fragment fragment) {
                FragmentTransaction fragmentTransaction =
                        getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.main_frame, fragment);
                fragmentTransaction.commit();
            }
        });

        mMainNav.setSelectedItemId(R.id.nav_home);

    }

    private void updateTokenToServer(String refreshedToken) {

        Token token = new Token(refreshedToken);
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            mDatabase.collection("tokens").document(mUser.getUid()).set(token);
        }
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

        if (startTrackerService()) {
            availableSwitch.setChecked(true);
            Common.availabile = true;
        }

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

//            Toast.makeText(this, "GPS tracking enabled", Toast.LENGTH_LONG)
//            .show();
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
