package com.example.logisticsfree.home;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.example.logisticsfree.R;

public class home extends AppCompatActivity {
    private final String TAG = "HomeAcivity";
    private BottomNavigationView mMainNav;
    private FrameLayout mMainFrame;

    private HistoryFragment historyFragment;
    private RattingFragment rattingFragment;
    private SettingFragment settingFragment;
    private HomeFragment homeFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

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
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.main_frame, fragment);
                fragmentTransaction.commit();
            }
        });

        mMainNav.setSelectedItemId(R.id.nav_home);

    }
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_actionbar, menu);

        Switch availableSwitch= menu.findItem(R.id.action_set_availability).getActionView().findViewById(R.id.switchForActionBar);

        assert availableSwitch != null;
        availableSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            }
        });

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_set_availability:
                Log.d(TAG, "onOptionsItemsSelected: asdfasfd");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
