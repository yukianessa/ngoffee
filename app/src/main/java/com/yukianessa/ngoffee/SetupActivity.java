package com.yukianessa.ngoffee;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.yukianessa.ngoffee.R.layout.activity_setup;

public class SetupActivity extends AppCompatActivity {

    private Toolbar          setupToolbars;         // declare toolbar of Setup
    private CircleImageView  setupImageIn;          // images picker
    private Uri              mainImageURI = null;   // image url link <crop>
    private EditText         setupName;             // input name text
    private Button           setupSaveBtn;          // button for save settings
    private ProgressBar      setupToLoading;        // progress bar status

    private String userId;
    //private Bitmap compressedImageFile;
    private boolean profileChanged = false;

    private StorageReference  storageReference;     // Firebase References
    private FirebaseAuth      firebaseAuth;         // Firebase Authentications
    private FirebaseFirestore firebaseFirestore;    // Firebase Firestore <Storage Database>

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_setup);

        firebaseAuth       = FirebaseAuth.getInstance();
        firebaseFirestore  = FirebaseFirestore.getInstance();
        storageReference   = FirebaseStorage.getInstance().getReference();

        userId = firebaseAuth.getCurrentUser().getUid();

        //calling each declaration by reference id's
        setupImageIn    = findViewById(R.id.setupImage);
        setupName       = findViewById(R.id.setupNameIn);
        setupSaveBtn    = findViewById(R.id.setupSaveBtn);
        setupToLoading  = findViewById(R.id.setupLoading);

        setupToolbars   = findViewById(R.id.setupToolbar);
        setSupportActionBar(setupToolbars);
        getSupportActionBar().setTitle("Account Set-Up");

        setupToLoading.setVisibility(View.VISIBLE);     // loading bar for Database
        setupSaveBtn.setEnabled(false);                 // save button in disable mode

        firebaseFirestore.collection("users").document(userId).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()){

                    if (task.getResult().exists()) {
                        String name     = task.getResult().getString("name");
                        String image    = task.getResult().getString("image");

                        mainImageURI = Uri.parse(image);
                        setupName.setText(name);

                        setupName.setText(name);

                        RequestOptions placeholderRequest = new RequestOptions();
                        placeholderRequest.placeholder(R.drawable.default_image);

                        Glide.with(SetupActivity.this)
                                .setDefaultRequestOptions(placeholderRequest)
                                .load(image)
                                .into(setupImageIn);

                        //Toast.makeText(SetupActivity.this,"Data Exist ! ", Toast.LENGTH_LONG).show();
                    }

                } else {

                    String err = task.getException().getMessage();
                    Toast.makeText
                            (SetupActivity.this,"Firebase Retrieve Firestore: \n" + err, Toast.LENGTH_LONG).show();
                }

                setupToLoading.setVisibility(View.INVISIBLE);
                setupSaveBtn.setEnabled(true);
            }
        });

        setupSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String user_name = setupName.getText().toString();

                if (TextUtils.isEmpty(user_name) && mainImageURI == null){
                    Toast.makeText
                            (SetupActivity.this,
                                    "User Name and Profile Image cant be Empty!", Toast.LENGTH_LONG).show();
                }
                else if (TextUtils.isEmpty(user_name)){
                    Toast.makeText
                            (SetupActivity.this,
                                    "User Name cant be Empty!", Toast.LENGTH_LONG).show();
                } else if (mainImageURI == null) {
                    Toast.makeText
                            (SetupActivity.this,
                                    "Profile Image cant be Empty!", Toast.LENGTH_LONG).show();
                }

                if (!TextUtils.isEmpty(user_name) && mainImageURI != null) {
                setupToLoading.setVisibility(View.VISIBLE);

                    if (profileChanged) {
                        setupToLoading.setVisibility(View.VISIBLE); // progress bar showing

                        final String userId = firebaseAuth.getCurrentUser().getUid();

                        StorageReference imagePath = storageReference.child("profile_image").child(userId + ".png");
                        imagePath.putFile(mainImageURI).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {

                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                                Intent loginIntent = new Intent(SetupActivity.this, LoginActivity.class);
                                startActivity(loginIntent);
                                finish();

                                if (task.isSuccessful()) {
                                    storeFirestore(task, user_name);

                                } else {

                                    String err = task.getException().getMessage();
                                    Toast.makeText
                                            (SetupActivity.this, "Image Error: " + err, Toast.LENGTH_LONG).show();

                                    setupToLoading.setVisibility(View.INVISIBLE);
                                }
                            }
                        });
                    } else {
                        storeFirestore(null, user_name);
                    }
                }
            }
        });

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
                        imgPicker();
                    }
                } else {
                    imgPicker();
                }
            }
        });
    }

    private void storeFirestore(@NonNull Task<UploadTask.TaskSnapshot>task, String user_name) {

        Uri download_uri;

        if (task != null) {

            download_uri = task.getResult().getDownloadUrl();

        } else {

            download_uri = mainImageURI;

        }
        Map<String, String> userMap = new HashMap<>();
        userMap.put("name", user_name);
        userMap.put("image", download_uri.toString());

        firebaseFirestore.collection("users").document(userId)
                .set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {

            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()){

                    Toast.makeText
                            (SetupActivity.this, "Settings Successfully Updated!", Toast.LENGTH_LONG).show();

                    Intent goToMain = new Intent
                            (SetupActivity.this, MainActivity.class);
                    startActivity(goToMain);

                } else {
                    String err = task.getException().getMessage();
                    Toast.makeText
                            (SetupActivity.this,"Firebase Firestore: " + err, Toast.LENGTH_LONG).show();
                }
                setupToLoading.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void imgPicker() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1,1)
                .start(SetupActivity.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                mainImageURI = result.getUri();
                setupImageIn.setImageURI(mainImageURI);

                profileChanged = true;

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}