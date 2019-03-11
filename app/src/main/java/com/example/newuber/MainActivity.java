package com.example.newuber;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.newuber.signup.SignupActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "MainActivity";
    public static Activity selfActivity;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    private Button joinUsButton;

    private static final int REQUEST_GET_FIREBASE_AUTH = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "OnCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        selfActivity = this;

        joinUsButton = findViewById(R.id.btn_joinus);
//
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        if (mUser != null) joinUsButton.setVisibility(View.GONE);

    }
    public void gotoSignup(View view) {
        if (mUser == null) startActivity(new Intent(this, SignupActivity.class).putExtra("newMember", mUser==null));
    }

    public void gotoSignin(View view) {
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
            joinUsButton.setVisibility(View.VISIBLE);
            mUser = null;
        }
    }
    public void gotoHome(View view){
        //Toast.makeText(selfActivity, "you clicked", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, home.class));
    }
}
