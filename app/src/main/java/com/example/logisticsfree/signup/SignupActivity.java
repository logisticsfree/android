package com.example.logisticsfree.signup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.example.logisticsfree.R;

public class SignupActivity extends AppCompatActivity {
    private EditText signupPhone;
    public static Activity selfActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        selfActivity = this;

        signupPhone = findViewById(R.id.signup_phone_view);
    }

    public void gotoSignup2(View view) {
        signupPhone.setError(null);
        String phoneNo = signupPhone.getText().toString();


        if (TextUtils.isEmpty(phoneNo)) {
            signupPhone.setError(getString(R.string.error_field_required));
            signupPhone.requestFocus();

        } else if(!isPhoneNoValid(signupPhone.getText().toString())) {
            signupPhone.setError(getString(R.string.error_incorrect_phone));
            signupPhone.requestFocus();
        } else {
            startActivity(
                    new Intent(SignupActivity.this, SignupActivity2.class)
                    .putExtra("phone", phoneNo)
            );
        }
    }

    private boolean isPhoneNoValid(String phoneNo) {
        return phoneNo.length() > 9 && PhoneNumberUtils.isGlobalPhoneNumber(phoneNo);
    }
}
