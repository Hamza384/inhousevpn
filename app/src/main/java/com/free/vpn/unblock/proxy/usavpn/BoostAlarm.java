package com.free.vpn.unblock.proxy.usavpn;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class BoostAlarm extends BroadcastReceiver {

    public final static String PREFERENCES_RES_BOOSTER = "digicom";
    SharedPreferences.Editor editor;
    SharedPreferences sharedpreferences;

    @Override
    public void onReceive(Context context, Intent intent) {
        sharedpreferences = context.getSharedPreferences(PREFERENCES_RES_BOOSTER, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();
        editor.putString("booster", "1");
        editor.commit();
    }
}
