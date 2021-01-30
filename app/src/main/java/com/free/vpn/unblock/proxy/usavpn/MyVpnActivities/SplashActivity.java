package com.free.vpn.unblock.proxy.usavpn.MyVpnActivities;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.free.vpn.unblock.proxy.usavpn.R;
import com.google.android.material.snackbar.Snackbar;



public class SplashActivity extends AppCompatActivity {
    protected ProgressBar mCircularProgressBar;
    RelativeLayout coordinatorLayout;
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        mContext = SplashActivity.this;


        coordinatorLayout = findViewById(R.id.cordi);
        mCircularProgressBar = findViewById(R.id.progress_circular);
        mCircularProgressBar = new ProgressBar(mContext);


        if (Build.VERSION.SDK_INT < 21) {
            mCircularProgressBar.getIndeterminateDrawable()
                    .setColorFilter(ContextCompat.getColor(this, R.color.blue), PorterDuff.Mode.SRC_IN);
        }


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
