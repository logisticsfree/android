package com.example.logisticsfree;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.logisticsfree.Common.Common;
import com.example.logisticsfree.models.Order;

public class DriverTracking extends AppCompatActivity {
    private static final String TAG = "DriverTracking";
    private Order currentOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_tracking);

        currentOrder = Common.selectedOrder;

        Log.d(TAG, "onCreate: " + currentOrder.getWarehouse().getName());
    }
}
