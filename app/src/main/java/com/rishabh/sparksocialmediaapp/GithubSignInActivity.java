package com.rishabh.sparksocialmediaapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.OAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GithubSignInActivity extends AppCompatActivity {

    private EditText inputEmail;
    private Button loginBtn;

    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference usersRef;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_github_sign_in);

        inputEmail = findViewById(R.id.email);
        loginBtn = findViewById(R.id.btnLogin);

        progressDialog = new ProgressDialog(GithubSignInActivity.this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        usersRef = FirebaseDatabase.getInstance().getReference();

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = inputEmail.getText().toString();
                if (!email.matches(emailPattern)) {
                    inputEmail.setError("Enter valid email");
                    inputEmail.requestFocus();
                } else {
                    progressDialog.show();
                    OAuthProvider.Builder provider = OAuthProvider.newBuilder("github.com");
                    provider.addCustomParameter("login", email);
                    List<String> scopes =
                            new ArrayList<String>() {
                                {
                                    add("user:email");
                                }
                            };
                    provider.setScopes(scopes);

                    Task<AuthResult> pendingResultTask = auth.getPendingAuthResult();
                    if (pendingResultTask != null) {
                        pendingResultTask
                                .addOnSuccessListener(
                                        new OnSuccessListener<AuthResult>() {
                                            @Override
                                            public void onSuccess(AuthResult authResult) {
                                                Toast.makeText(GithubSignInActivity.this, "User already signed In", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                .addOnFailureListener(
                                        new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(GithubSignInActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                progressDialog.dismiss();
                                                finish();
                                            }
                                        });
                    } else {
                        auth.startActivityForSignInWithProvider(GithubSignInActivity.this, provider.build())
                                .addOnSuccessListener(
                                        new OnSuccessListener<AuthResult>() {
                                            @Override
                                            public void onSuccess(AuthResult authResult) {
                                                user = auth.getCurrentUser();
                                                updateUI(user);
                                                progressDialog.dismiss();
                                                SendUserToMainActivity();
                                            }
                                        })
                                .addOnFailureListener(
                                        new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                progressDialog.dismiss();
                                                Toast.makeText(GithubSignInActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                    }
                }
            }
        });
    }

    private void updateUI(FirebaseUser user) {

        if (user != null) {
            for (UserInfo userInfo : user.getProviderData()){
                if (userInfo.getProviderId().equals("github.com")){
                    String name = userInfo.getDisplayName();
                    String email = userInfo.getEmail();
                    String image = userInfo.getPhotoUrl().toString();

                    HashMap map = new HashMap();
                    map.put("name", name);
                    map.put("email", email);
                    map.put("photo", image);
                    map.put("mode", "Github");

                    usersRef.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(map);
                }
            }
        }
    }

    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(GithubSignInActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
}