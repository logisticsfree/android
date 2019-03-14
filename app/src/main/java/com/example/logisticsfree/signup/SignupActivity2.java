package com.example.logisticsfree.signup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.example.logisticsfree.R;

public class SignupActivity2 extends AppCompatActivity {
    public static Activity selfActivity;
    private EditText signupFname, signupLname;
    private String phoneNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup2);
        selfActivity = this;
        phoneNo = getIntent().getStringExtra("phone");

        signupFname = findViewById(R.id.signup_fname);
        signupLname = findViewById(R.id.signup_lname);

    }

    public void gotoSignup3 (View view) {
        signupFname.setError(null);
        signupLname.setError(null);

        String fname= signupFname.getText().toString();
        String lname = signupLname.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(fname)) {
            signupFname.setError(getString(R.string.error_field_required));
            focusView = signupFname;
            cancel = true;
        }

        if (TextUtils.isEmpty(lname)) {
            signupLname.setError(getString(R.string.error_field_required));
            focusView = signupLname;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            startActivity(new Intent(SignupActivity2.this, SignupActivity3.class)
            .putExtra("phone", phoneNo)
            .putExtra("fname", fname)
            .putExtra("lname", lname));
        }
    }
}

