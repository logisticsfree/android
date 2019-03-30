package com.example.logisticsfree;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private TextView nameBox;
    private TextView nameView;
    private TextView numberView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        nameBox = findViewById(R.id.nameBox);
        nameView=findViewById(R.id.nameView);
        numberView=findViewById(R.id.numberView);

        if (mUser == null) finish();

        nameBox.setText("Email: "+mUser.getEmail());
        nameView.setText("Name: ");
        numberView.setText("Mobile Number: ");
    }
}
