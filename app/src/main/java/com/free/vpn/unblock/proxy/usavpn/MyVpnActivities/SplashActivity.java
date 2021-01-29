package com.free.vpn.unblock.proxy.usavpn.MyVpnActivities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.snackbar.Snackbar;
import com.free.vpn.R;

public class SplashActivity extends AppCompatActivity {
    CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        coordinatorLayout = findViewById(R.id.cordi);

        if (!Constants.isOnline(getApplicationContext())) {


            Snackbar snackbar = Snackbar
                    .make(coordinatorLayout, "Internet Connection Not Available", Snackbar.LENGTH_LONG);
            snackbar.show();


        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(SplashActivity.this, WelcomeActivity.class));
                    finish();
                }
            }, 1000);

        }

    }
}
