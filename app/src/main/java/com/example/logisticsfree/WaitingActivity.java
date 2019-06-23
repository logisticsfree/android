package com.example.logisticsfree;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.logisticsfree.services.TrackingService;

public class WaitingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting);

        Glide.with(this)
//                .load("https://cdn.dribbble.com/users/891352/screenshots/3675943/lego-loader.gif")
                .load(R.raw.lego_loader)
                .into( (ImageView) findViewById(R.id.wait_for_instructions) );

        Intent intent2 = new Intent("finish_fragment_home");
        sendBroadcast(intent2);

        startService(new Intent(this, TrackingService.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(this, TrackingService.class));
    }
}
