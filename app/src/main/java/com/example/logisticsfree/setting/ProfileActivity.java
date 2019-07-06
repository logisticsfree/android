package com.example.logisticsfree.setting;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.logisticsfree.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private EditText txt_fName, txt_lName, txt_contact;
    private Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        setTitle("My Details");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore fs = FirebaseFirestore.getInstance();
        final DocumentReference mDriver = fs.document("drivers/" + mUser.getUid());

        txt_fName = findViewById(R.id.txt_profile_fname);
        txt_lName = findViewById(R.id.txt_profile_lname);
        txt_contact = findViewById(R.id.txt_profile_contact);
        btnSave = findViewById(R.id.btn_profile_save);

        enableEditText(txt_contact, false);
        enableEditText(txt_fName, false);
        enableEditText(txt_lName, false);

        mDriver.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                txt_fName.setText(documentSnapshot.getString("fName"));
                txt_lName.setText(documentSnapshot.getString("lName"));
                txt_contact.setText(documentSnapshot.getString("phone"));

                enableEditText(txt_fName, true);
                enableEditText(txt_lName, true);
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, Object> data = new HashMap<>();
                data.put("fName", txt_fName.getText().toString());
                data.put("lName", txt_lName.getText().toString());
                mDriver.set(data, SetOptions.merge());
            }
        });
    }

    private void enableEditText(EditText editText, boolean enable) {
        editText.setEnabled(enable);
        editText.setCursorVisible(enable);
        if (enable) editText.setFocusableInTouchMode(true);
        else editText.setFocusable(enable);
//        editText.setKeyListener(null);
//        editText.setBackgroundColor(Color.TRANSPARENT);
    }

}
