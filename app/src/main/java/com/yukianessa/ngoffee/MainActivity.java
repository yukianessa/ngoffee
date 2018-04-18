package com.yukianessa.ngoffee;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private Toolbar mainToToolbar;
    private FloatingActionButton addPostBtn;

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        mainToToolbar = findViewById(R.id.mainToolbars);
        setSupportActionBar(mainToToolbar);
        getSupportActionBar().setTitle("ngoffee");

        addPostBtn = findViewById(R.id.newPostBtn);

        addPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent goToAddNewPost = new Intent(MainActivity.this, NewPostActivity.class);
                startActivity(goToAddNewPost);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser == null) {
            sendToLogIn();  // user was'nt sign-in
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.actionLogoutBtn:
                logOut();
                return true; //go to LogOut statement
            case R.id.actionAccSettBtn:
                settingSetUp();
                return true;
            default:
                return false; //nothing happen
        }
    }

    private void settingSetUp() { // got to settings profile the current user
        Intent settingIntent = new Intent(MainActivity.this,  SetupActivity.class);
        startActivity(settingIntent);
        finish();
    }

    private void logOut() { // when user pick LogOout Action it will arrange to login Page
     mAuth.signOut(); // make sure to make it logout status to database
     sendToLogIn();
    }

    private void sendToLogIn() {
        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }
}

