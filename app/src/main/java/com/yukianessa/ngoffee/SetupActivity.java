package com.yukianessa.ngoffee;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.yukianessa.ngoffee.R.layout.activity_setup;

public class SetupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_setup);

        Toolbar setupToolbars = findViewById(R.id.setupToolbar);
        setSupportActionBar(setupToolbars);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Account Set Up");
        CircleImageView setupImageIn = findViewById(R.id.setupImage);

        setupImageIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission
                            (SetupActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED){
                        Toast.makeText(SetupActivity.this, "Permission Denied!",
                                Toast.LENGTH_LONG).show();
                        ActivityCompat.requestPermissions
                                (SetupActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
                    } else {
                        //Toast.makeText(SetupActivity.this, "You're already have permission, Permission Granted!", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

    }
}
