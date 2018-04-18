package com.yukianessa.ngoffee;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

public class NewPostActivity extends AppCompatActivity {


    private Toolbar newPostToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        newPostToolbar = findViewById(R.id.addStatusToolbars);
        setSupportActionBar(newPostToolbar);
        getSupportActionBar().setTitle("Add New Post");


    }
}
