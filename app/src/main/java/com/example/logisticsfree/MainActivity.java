package com.example.logisticsfree;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.example.logisticsfree.home.home;
import com.example.logisticsfree.setting.ProfileActivity;
import com.example.logisticsfree.signup.SignupActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "MainActivity";
    public static Activity selfActivity;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private View MainView;
    private View MainViewProgress;

    private Button joinUsButton;
    private Button loginButton;
    private Button showInstructionsButton;

    // not necessary
    private static final int REQUEST_GET_FIREBASE_AUTH = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "OnCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        selfActivity = this;

        joinUsButton = findViewById(R.id.btn_joinus);
        MainView = findViewById(R.id.main_view);
        MainViewProgress = findViewById(R.id.main_view_progress);
        loginButton = findViewById(R.id.btn_login);
        showInstructionsButton = findViewById(R.id.btn_show_instructions);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        mDatabase.setFirestoreSettings(settings);

        if (mUser != null) {
            joinUsButton.setVisibility(View.GONE);
            loginButton.setVisibility(View.GONE);

            showProgress(true);
            mDatabase.collection("drivers").document(mUser.getUid()).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                            Log.d(TAG, task.getResult().getBoolean("enabled") + " !");
                            if (task.getResult().getBoolean("enabled")) {
                                startActivity(new Intent(MainActivity.this, home.class));
                                finish();
                            } else {
                                showInstructionsButton.setVisibility(View.VISIBLE);
                            }
                            showProgress(false);
                        }
                    });
        }
    }

    public void gotoSignup(View view) {
        if (mUser == null)
            startActivity(new Intent(this, SignupActivity.class).putExtra("newMember", mUser == null));
    }

    public void gotoSignin(View view) {
        if (mUser == null)
            startActivityForResult(new Intent(this, LoginActivity.class), REQUEST_GET_FIREBASE_AUTH);
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
        startActivity(new Intent(this, MainActivity.class));
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

        MainView.setVisibility(show ? View.GONE : View.VISIBLE);
        MainView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                MainView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        MainViewProgress.setVisibility(show ? View.VISIBLE : View.GONE);
        MainViewProgress.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                MainViewProgress.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    public void gotoHome(View view) {
        //Toast.makeText(selfActivity, "you clicked", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, home.class));
    }

    public void showDialog(View view) {
        Log.d(TAG, "showDialog");
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Disabled");
        dialog.setMessage(R.string.msg_disabled);

        LayoutInflater inflater = LayoutInflater.from(this);
        View disabled_layout = inflater.inflate(R.layout.layout_disabled, null);

        dialog.setView(disabled_layout);

        dialog.setNeutralButton("Dismiss", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}
