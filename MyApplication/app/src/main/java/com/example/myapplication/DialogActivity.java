package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;

/**
 * Created by Илья on 10.10.2017.
 */

public class DialogActivity extends AppCompatActivity {
    LinearLayout lw;
    String login, friend;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);
        Intent intent = getIntent();
        login = intent.getStringExtra("login");
        friend = intent.getStringExtra("friend");
        lw = (LinearLayout) findViewById(R.id.lw);
        getDialog();
    }
    private void getDialog(){

    }
}
