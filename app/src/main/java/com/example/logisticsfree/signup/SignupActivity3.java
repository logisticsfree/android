package com.example.logisticsfree.signup;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.example.logisticsfree.R;

public class SignupActivity3 extends AppCompatActivity {
    private final String TAG = "SignupActivity3";
    public static Activity selfActivity;
    private EditText signupTruckNo, signupTruckWeight, signupTruckVolume;
    private Spinner signupTruckType;
    private String phoneNo, fname, lname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup3);
        selfActivity = this;

        phoneNo = getIntent().getStringExtra("phone");
        fname = getIntent().getStringExtra("fname");
        lname = getIntent().getStringExtra("lname");

        signupTruckNo = findViewById(R.id.signup_truck_no);
        signupTruckVolume = findViewById(R.id.signup_truck_volume);
        signupTruckWeight = findViewById(R.id.signup_truck_weight);
        signupTruckType = findViewById(R.id.signup_truck_type);

        String[] truckTypes = new String[]{"Please Select", "A/C", "Non A/C"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_item, truckTypes){
            @Override
            public boolean isEnabled(int position){
                return position != 0;
            }
            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position == 0){
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY);
                }
                else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }};
        signupTruckType.setAdapter(adapter);

    }

    public void gotoSignup3(View view) {
        signupTruckVolume.setError(null);
        signupTruckWeight.setError(null);
        signupTruckNo.setError(null);

        String truckNo = signupTruckNo.getText().toString();
        double truckWeight = Double.parseDouble(signupTruckWeight.getText().toString());
        double truckVolume = Double.parseDouble(signupTruckVolume.getText().toString());
        String truckType = signupTruckType.getSelectedItem().toString();
        Log.d(TAG, "gotoSignup3: " + truckType);

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(truckNo)) {
            signupTruckNo.setError(getString(R.string.error_field_required));
            focusView = signupTruckNo;
            cancel = true;
        }

        if (truckWeight == 0) {
            signupTruckWeight.setError(getString(R.string.error_field_required));
            focusView = signupTruckWeight;
            cancel = true;
        }
        if (truckVolume == 0) {
            signupTruckVolume.setError(getString(R.string.error_field_required));
            focusView = signupTruckVolume;
            cancel = true;
        }
        if (signupTruckType.getSelectedItemPosition() == 0) {
            TextView errorText = (TextView)signupTruckType.getSelectedView();
            errorText.setError("");
            errorText.setTextColor(Color.RED);//just to highlight that this is an error
            errorText.setText(R.string.error_field_required);
            focusView = signupTruckType;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            startActivity(new Intent(SignupActivity3.this, SignupActivity4.class)
                    .putExtra("phone", phoneNo)
                    .putExtra("fname", fname)
                    .putExtra("lname", lname)
                    .putExtra("truckNo", truckNo)
                    .putExtra("truckVolume", truckVolume)
                    .putExtra("truckWeight", truckWeight)
                    .putExtra("truckType", truckType))
            ;
        }
    }
}

