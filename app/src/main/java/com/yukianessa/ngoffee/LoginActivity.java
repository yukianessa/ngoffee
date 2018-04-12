package com.yukianessa.ngoffee;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private EditText loginToEmail;
    private EditText loginToPswd;
    private FirebaseAuth mAuth;
    private ProgressBar loginToLoading;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        loginToEmail    = findViewById(R.id.loginEmail);
        loginToPswd     = findViewById(R.id.loginPswd);
        Button loginToBtn    = findViewById(R.id.loginBtn);
        Button loginToRegBtn = findViewById(R.id.toLoginRegBtn);
        loginToLoading  = findViewById(R.id.loginLoadingProgress);

        loginToRegBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent regIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(regIntent);
                finish();
            }
        });

        loginToBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String LoginEmails  = loginToEmail.getText().toString();
                String LoginPswds   = loginToPswd.getText().toString();

                if(!TextUtils.isEmpty(LoginEmails) && !TextUtils.isEmpty(LoginPswds)){
                    loginToLoading.setVisibility(View.VISIBLE);

                    mAuth.signInWithEmailAndPassword(LoginEmails, LoginPswds).addOnCompleteListener
                            (new OnCompleteListener<AuthResult>() {

                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if(task.isSuccessful()){
                                //login success
                                sendToMain();
                            } else {
                                String err = Objects.requireNonNull(task.getException()).getMessage(); //err == error message
                                Toast.makeText(LoginActivity.this, "Error: " + err, Toast.LENGTH_LONG).show();
                            }
                            loginToLoading.setVisibility(View.INVISIBLE);
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null){

            sendToMain();
        }
    }

    private void sendToMain() {
        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }
}
