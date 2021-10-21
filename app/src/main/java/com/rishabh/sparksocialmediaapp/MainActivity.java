package com.rishabh.sparksocialmediaapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;
    private FirebaseUser user;
    private Button logOut;
    private TextView name, email;
    private CircleImageView image;

    ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        usersRef = FirebaseDatabase.getInstance().getReference();
        logOut = findViewById(R.id.logout);

        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        image = findViewById(R.id.profile_pic);

        pDialog = new ProgressDialog(MainActivity.this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);
        pDialog.setCanceledOnTouchOutside(false);
        pDialog.show();

        logOut.setOnClickListener(v -> {
            mAuth.signOut();
            LoginManager.getInstance().logOut();
            SendUserToLoginActivity();
        });

        usersRef.child("Users").child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if (snapshot.hasChild("name")) {
                        String namep = snapshot.child("name").getValue().toString();
                        name.setText(namep);
                    }
                    if (snapshot.hasChild("email")) {
                        String emailp = snapshot.child("email").getValue().toString();
                        email.setText(emailp);
                    }
                    if (snapshot.hasChild("photo")) {
                        String photo = snapshot.child("photo").getValue().toString();

                        Glide.with(MainActivity.this).load(photo).placeholder(R.drawable.profile).into(image);
                    }
                    pDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (user == null){
            SendUserToLoginActivity();
        }
    }

    private void SendUserToLoginActivity() {
        Intent mainIntent = new Intent(MainActivity.this, LoginActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
}