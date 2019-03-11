package com.example.newuber;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class ResetPasswordActivity extends AppCompatActivity {
    private View resetForm;
    private View resetFormProgress;
    private EditText resetEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        resetForm = findViewById(R.id.reset_form);
        resetFormProgress = findViewById(R.id.reset_progress);
        resetEmail = findViewById(R.id.reset_email);
        resetEmail.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptReset();
                    return true;
                }
                return false;
            }
        });

        Button resetButton = findViewById(R.id.reset_button);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptReset();
            }
        });
    }

    private void attemptReset() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String email = resetEmail.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            resetEmail.setError(getString(R.string.error_field_required));
            resetEmail.requestFocus();
        } else if (!isEmailValid(email)) {
            resetEmail.setError(getString(R.string.error_invalid_email));
            resetEmail.requestFocus();
        } else {
            showProgress(true);
            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(ResetPasswordActivity.this,
                                        "We have sent you instructions to reset your password!",
                                        Toast.LENGTH_LONG)
                                        .show();
                                finish();
                            } else {
                                Toast.makeText(ResetPasswordActivity.this,
                                        "Failed to send reset email! " + Objects.requireNonNull(task.getException()).getMessage(),
                                        Toast.LENGTH_LONG).show();

                            }
                            showProgress(false);
                        }
                    });
        }


    }
    private boolean isEmailValid(String email) {
        return (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches());
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

        resetForm.setVisibility(show ? View.GONE : View.VISIBLE);
        resetForm.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                resetForm.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        resetFormProgress.setVisibility(show ? View.VISIBLE : View.GONE);
        resetFormProgress.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                resetFormProgress.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }
}
