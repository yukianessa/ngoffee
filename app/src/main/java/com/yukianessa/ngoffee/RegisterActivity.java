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

public class RegisterActivity extends AppCompatActivity {

    private EditText regEmail;
    private EditText regPswd;
    private EditText regCfPswd;

    private ProgressBar signUpLoading;

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        regEmail    = (EditText) findViewById(R.id.regEmail);
        regPswd     = (EditText) findViewById(R.id.regPswd);
        regCfPswd   = (EditText) findViewById(R.id.regPswdCnfrm);
        Button goToSignUp = (Button) findViewById(R.id.toSignUpBtn);
        Button goToLogin  = (Button) findViewById(R.id.toLoginRegBtn);
        signUpLoading = (ProgressBar) findViewById(R.id.loginLoadingProgress);

        goToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(loginIntent);
                finish();
            }
        });

        goToSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email    = regEmail.getText().toString(); //input email and so on
                String pswd     = regPswd.getText().toString();
                String cpswd    = regCfPswd.getText().toString();

                if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pswd) && !TextUtils.isEmpty(cpswd)){

                    if (pswd.equals(cpswd)){
                        signUpLoading.setVisibility(View.VISIBLE);
                        mAuth.createUserWithEmailAndPassword(email, pswd).addOnCompleteListener
                                (new OnCompleteListener<AuthResult>(){

                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()){
                                    sendToMain();
                                } else {
                                    String err = task.getException().getMessage();
                                    Toast.makeText
                                            (RegisterActivity.this, "Error: " +err, Toast.LENGTH_LONG).show();
                                }
                                signUpLoading.setVisibility(View.INVISIBLE);
                            }
                        });

                    } else {
                        Toast.makeText
                                (RegisterActivity.this, "Password aren't match", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currUser = mAuth.getCurrentUser();
        if(currUser != null) {
            sendToMain();
        }
    }

    private void sendToMain() {
        Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }
}
