package com.example.newuber;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "MainActivity";
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    private static final int REQUEST_GET_FIREBASE_AUTH = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "OnCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

    }

    public void gotoSignup(View view) {
        if (mUser == null) startActivityForResult(new Intent(this, LoginActivity.class), REQUEST_GET_FIREBASE_AUTH);
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_GET_FIREBASE_AUTH && resultCode == Activity.RESULT_OK) {
            if (data.getBooleanExtra("success", false))
                this.mUser = mAuth.getCurrentUser();
        }
    }
    public void gotoProfile(View view) {
        if (mUser != null) startActivity(new Intent(this, ProfileActivity.class));
    }
    public void logout(View view) {
        if (mUser != null) {
            mAuth.signOut();
            mUser = null;
        }
    }
}
