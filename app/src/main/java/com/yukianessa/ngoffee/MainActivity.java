package com.yukianessa.ngoffee;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private Toolbar                 mainToToolbar;
    private FloatingActionButton    addPostBtn;
    private BottomNavigationView    mainBottomNav;

    private FirebaseAuth        mAuth;
    private FirebaseFirestore   firebaseFirestore;

    private String currentUserId;

    private HomeFragment            mHomeFragment;
    private NotificationFragment    mNotifyFragment;
    private AccountFragment         mAccFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        mainToToolbar = findViewById(R.id.mainToolbars);
        setSupportActionBar(mainToToolbar);
        getSupportActionBar().setTitle("ngoffee");

        if (mAuth.getCurrentUser() != null) {

            addPostBtn = findViewById(R.id.newPostBtn);
            mainBottomNav = findViewById(R.id.bottomNav);

            //Fragment
            mHomeFragment = new HomeFragment();
            mNotifyFragment = new NotificationFragment();
            mAccFragment = new AccountFragment();

            replaceFragment(mHomeFragment);

            mainBottomNav.setOnNavigationItemSelectedListener
                    (new BottomNavigationView.OnNavigationItemSelectedListener() {
                        @Override
                        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                            switch (item.getItemId()) {
                                case R.id.btmActionHome:
                                    replaceFragment(mHomeFragment);
                                    return true;
                                case R.id.btmActionNotify:
                                    replaceFragment(mNotifyFragment);
                                    return true;
                                case R.id.btmActionAcc:
                                    replaceFragment(mAccFragment);
                                    return true;
                                default:
                                    return false;
                            }
                        }
                    });

            addPostBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent goToAddNewPost = new Intent(MainActivity.this, NewPostActivity.class);
                    startActivity(goToAddNewPost);
                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser == null) {
            sendToLogIn();  // user was'nt sign-in
        } else {
            currentUserId = mAuth.getCurrentUser().getUid();
            firebaseFirestore.collection("users").document(currentUserId)
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        if (!task.getResult().exists()) { // user will be forced to put their name and photo before user it
                            settingSetUp(); // if user not yet put it it will moved to setup activity

                            //String err = task.getException().getMessage();
                            //Toast.makeText
                              //      (MainActivity.this,"Error: " + err, Toast.LENGTH_LONG).show();
                        }
                    } else {
                        String err = task.getException().getMessage();
                        Toast.makeText
                                (MainActivity.this,"Error: " + err, Toast.LENGTH_LONG).show();
                    }
                }
            });
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
        //finish();
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

    private void replaceFragment (Fragment fragment) {

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.mainContainer, fragment);
        fragmentTransaction.commit();

    }
}

