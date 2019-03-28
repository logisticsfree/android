package com.example.logisticsfree.signup;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.logisticsfree.LoginActivity;
import com.example.logisticsfree.MainActivity;
import com.example.logisticsfree.R;
import com.example.logisticsfree.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class SignupActivity4 extends AppCompatActivity {
    private final String TAG = "SignupActivity";

    private View mSignupView;
    private View mProgressView;
    private EditText mEmail;
    private EditText mPassword;
    private String phone, fname, lname;
    FirebaseFirestore mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup4);
        phone = getIntent().getStringExtra("phone");
        fname = getIntent().getStringExtra("fname");
        lname = getIntent().getStringExtra("lname");

//        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseFirestore.getInstance();

        mSignupView = findViewById(R.id.signup_form);
        mProgressView = findViewById(R.id.signup_progress);
        mEmail = findViewById(R.id.signup_email);
        mPassword = findViewById(R.id.signup_password);

        boolean newMember = getIntent().getBooleanExtra("newMember", false);
        if (newMember) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        Button nextButton = findViewById(R.id.btn_next1);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attamptSignup();
            }
        });
    }

    private void attamptSignup() {

        final FirebaseAuth mAuth = FirebaseAuth.getInstance();

        // Reset errors.
        mEmail.setError(null);
        mPassword.setError(null);

        String email = mEmail.getText().toString();
        String password = mPassword.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            mPassword.setError(getString(R.string.error_field_required));
            focusView = mPassword;
            cancel = true;
        } else if (!isPasswordValid(password)) {
            mPassword.setError(getString(R.string.error_invalid_password));
            focusView = mPassword;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmail.setError(getString(R.string.error_field_required));
            focusView = mEmail;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmail.setError(getString(R.string.error_invalid_email));
            focusView = mEmail;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(SignupActivity4.this, new OnCompleteListener<AuthResult>() {
                        @TargetApi(Build.VERSION_CODES.KITKAT)
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (!task.isSuccessful()) {
                                showProgress(false);

                                Toast.makeText(SignupActivity4.this,
                                        "Authentication failed! " +
                                                Objects.requireNonNull(task.getException()).getMessage(),
                                        Toast.LENGTH_LONG).show();
                                Log.e(TAG, "Auth Failed: " + task.getException());
                            } else {
                                Toast.makeText(SignupActivity4.this,
                                        "Signup successful!",
                                        Toast.LENGTH_LONG).show();

                                FirebaseUser mUser = mAuth.getCurrentUser();

                                assert mUser != null;
                                saveUser(mUser);

                                setResult(Activity.RESULT_OK,
                                        new Intent().putExtra("success", true));

                                startActivity(
                                        new Intent(SignupActivity4.this, MainActivity.class)
                                );

                                SignupActivity.selfActivity.finish();
                                SignupActivity2.selfActivity.finish();
                                MainActivity.selfActivity.finish();
                                finish();
                            }
                        }
                    });
        }
    }

    private void saveUser(FirebaseUser mUser) {
        User newUser = new User(fname, lname, phone, true, null, true, false);
//        mUserRef.child(mUser.getUid()).setValue(newUser);
        mDatabase.collection("drivers").document(mUser.getUid())
                .set(newUser)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        showProgress(false);

                        Log.d(TAG, "new User doc successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showProgress(false);
                        Log.w(TAG, "Error writing new user document", e);
                    }
                });
    }

    private boolean isEmailValid(String email) {
        return (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 6;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mSignupView.setVisibility(show ? View.GONE : View.VISIBLE);
        mSignupView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mSignupView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }
}












