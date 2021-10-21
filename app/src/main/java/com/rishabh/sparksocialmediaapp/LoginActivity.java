package com.rishabh.sparksocialmediaapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private Button googleBtn, facebookBtn, githubBtn;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        googleBtn = findViewById(R.id.google);
        facebookBtn = findViewById(R.id.facebook);
        githubBtn = findViewById(R.id.github);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        googleBtn.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, GoogleSignInActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        });

        facebookBtn.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, FacebookSignInActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        });

        githubBtn.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, GithubSignInActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (user != null){
            SendUserToMainActivity();
        }
    }

    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
}