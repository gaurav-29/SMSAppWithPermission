package com.example.smsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final int ANY_UNIQUE_NUMBER = 100;
    EditText txtmobile, txtmessage;
    TextView lblsize;
    Button btnsend;
    SmsManager manager;
    private Context ctx = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        btnsend.setEnabled(true);
        CheckPermission();
        HandleEvent();
    }

    private void CheckPermission() {
        //check operating system version
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            //check whether user has already granted the permission or not
            if(checkSelfPermission(Manifest.permission.SEND_SMS)!=PackageManager.PERMISSION_GRANTED)
            {
                //user has not given permission then request permission from user
                String permissions[] = {Manifest.permission.SEND_SMS};
                requestPermissions(permissions, ANY_UNIQUE_NUMBER);
            }
            else
            {
                //user has already granted permission
                btnsend.setEnabled(true);
            }
        }
        else
        {
            // version is < Build.VERSION_CODES.M
            btnsend.setEnabled(true);
        }
    }

    private void HandleEvent() {

        btnsend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               int size = txtmessage.getText().toString().length();   // One SMS allows maximum 160 characters.
                if(size<=160)
                {
                    manager.sendTextMessage(txtmobile.getText().toString(),"", txtmessage.getText().toString(), null, null);

                    // if user has 2 simcards, then we have to put 2 radiobuttons-mentioning mobile nummbers
                    // in TextViews. Then pass the second argument on the base of selection of radio buttons
                    // & TextViews (i.e. lbltext1.getText().toString()) in senTextMessage method and use if else.

                    Toast.makeText(ctx,"Message Sent", Toast.LENGTH_LONG).show();
                }
                else
                {
                    ArrayList<String> messages = manager.divideMessage(txtmessage.getText().toString());
                    manager.sendMultipartTextMessage(txtmobile.getText().toString(), "", messages, null, null);
                    Toast.makeText(ctx,"Multipart Messages Sent",Toast.LENGTH_LONG).show();
                }
            }
        });

        txtmessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                lblsize.setText("Count " + txtmessage.getText().toString().length());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void init() {
        btnsend = findViewById(R.id.btnsend);
        btnsend.setEnabled(false);
        txtmobile = findViewById(R.id.txtmobile);
        txtmessage = findViewById(R.id.txtmessage);
        lblsize = findViewById(R.id.lblsize);
        manager = SmsManager.getDefault();
    }

    //this is callback method and it will execute only first time to show whether user has given or rejected requested permission.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
        {
            Toast.makeText(ctx, "Permission Granted", Toast.LENGTH_LONG).show();
            btnsend.setEnabled(true);
        }
        else
        {
            Toast.makeText(ctx, "Permission not granted", Toast.LENGTH_LONG).show();
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }
}
