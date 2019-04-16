package com.example.logisticsfree;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.logisticsfree.Common.Common;
import com.example.logisticsfree.Remote.IFCMService;
import com.example.logisticsfree.Remote.IGoogleAPI;
import com.example.logisticsfree.models.FCMResponse;
import com.example.logisticsfree.models.Token;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomerCall extends AppCompatActivity {

    TextView txtTime, txtAddress, txtDistance;
    MediaPlayer mediaPlayer;
    Button btnCancel, btnAccept;

    IGoogleAPI mService;
    IFCMService mFCMService;

    String customerId;
    double lat, lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_call);

        mService = Common.getGoogleAPI();
        mFCMService = Common.getFCMService();

        txtTime = findViewById(R.id.txtTime);
        txtAddress = findViewById(R.id.txtAddress);
        txtDistance = findViewById(R.id.txtDistance);

        btnAccept = findViewById(R.id.btnAccept);
        btnCancel = findViewById(R.id.btnDecline);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(customerId))
                    cancelBooking(customerId);
            }
        });

        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CustomerCall.this, DriverTracking.class);
                intent.putExtra("lat", lat);
                intent.putExtra("lng", lng);
                intent.putExtra("customerId", customerId);
                startActivity(intent);
                finish();
            }
        });

        mediaPlayer = MediaPlayer.create(this, R.raw.ringtone);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

        if (getIntent() != null) {
            lat = Double.parseDouble(getIntent().getStringExtra("lat"));
            lng = Double.parseDouble(getIntent().getStringExtra("lng"));
            customerId = getIntent().getStringExtra("customer");

            getDirection(lat, lng);
        }
    }

    private void cancelBooking(String customerId) {
        Token token = new Token(customerId);

//        Notification notification = new Notification("Cancel","Driver has cancelled your request");
//        Sender sender = new Sender(token.getToken(),notification);

        Map<String, String> content = new HashMap<>();
        content.put("title", "Cancel");
        content.put("message", "Driver has cancelled your request");
//        DataMessage dataMessage = new DataMessage(token.getToken(), content);
//
//        mFCMService.sendMessage(dataMessage)
//                .enqueue(new Callback<FCMResponse>() {
//                    @Override
//                    public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {
//                        if (response.body().success == 1) {
//                            Toast.makeText(CustomerCall.this, "Cancelled", Toast.LENGTH_SHORT).show();
//                            finish();
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<FCMResponse> call, Throwable t) {
//
//                    }
//                });
    }

    private void getDirection(double lat, double lng) {
        System.out.println("Debug1 " + lat + "," + lng);
        String requestApi = null;
        try {
            requestApi = "https://maps.googleapis.com/maps/api/directions/json?" +
                    "mode=driving&" +
                    "transit_routing_preferences=less_driving&" +
                    "origin=" + Common.mLastLocation.getLatitude() + "," + Common.mLastLocation.getLongitude() + "&" +
                    "destination=" + lat + "," + lng + "&" +
                    "key=" + getResources().getString(R.string.google_direction_api);
            Log.d("checkLog", requestApi);
            mService.getPath(requestApi)
                    .enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response.body());

                                JSONArray routes = jsonObject.getJSONArray("routes");
                                JSONObject object = routes.getJSONObject(0);
                                JSONArray legs = object.getJSONArray("legs");
                                JSONObject legsObject = legs.getJSONObject(0);
                                JSONObject distance = legsObject.getJSONObject("distance");
                                txtDistance.setText(distance.getString("text"));

                                JSONObject time = legsObject.getJSONObject("duration");
                                txtTime.setText(time.getString("text"));

                                String address = legsObject.getString("end_address");
                                txtAddress.setText(address);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            Toast.makeText(CustomerCall.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        mediaPlayer.release();
        super.onStop();
    }

    @Override
    protected void onPause() {
        mediaPlayer.pause();
        super.onPause();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        mediaPlayer.start();
    }
}
