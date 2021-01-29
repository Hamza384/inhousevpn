package com.free.vpn.unblock.proxy.usavpn.MyVpnUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

import com.free.vpn.unblock.proxy.usavpn.MyVpnActivities.SplashActivity;

public class VpnDailyNotification extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO Auto-generated method stub
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        int wordNo = getIntent().getIntExtra("INDEX", 0);
        Intent intent = new Intent(VpnDailyNotification.this, SplashActivity.class);
        intent.putExtra("INDEX", wordNo);
        intent.putExtra("FROM_NOTIF", true);
        startActivity(intent);
        finish();
    }
}