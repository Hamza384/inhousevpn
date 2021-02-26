package com.free.vpn.unblock.proxy.usavpn.MyVpnUtils;

import android.content.Context;
import android.content.SharedPreferences;

public class MySharePrefs {
    public static String PREFERENCE = "quickvpn";
    private static MySharePrefs instance;
    SharedPreferences.Editor editor;
    int PRIVATE_MODE = 0;
    private final Context ctx;
    private final SharedPreferences sharedPreferences;

    public MySharePrefs(Context context) {
        ctx = context;
        sharedPreferences = context.getSharedPreferences(PREFERENCE, PRIVATE_MODE);
        editor = sharedPreferences.edit();
    }

    public static MySharePrefs getInstance(Context ctx) {
        if (instance == null) {
            instance = new MySharePrefs(ctx);
        }
        return instance;
    }

    public void putString(String key, String val) {
        sharedPreferences.edit().putString(key, val).apply();
    }

    public String getString(String key) {
        return sharedPreferences.getString(key, "");
    }

    public void putInt(String key, Integer val) {
        sharedPreferences.edit().putInt(key, val).apply();
    }

    public void putBoolean(String key, Boolean val) {
        sharedPreferences.edit().putBoolean(key, val).apply();
    }

    public Boolean getBoolean(String key) {
        return sharedPreferences.getBoolean(key, false);
    }

    public int getInt(String key) {
        return sharedPreferences.getInt(key, 0);
    }

    public int getIntPref(String key, int defaultVal) {
        return sharedPreferences.getInt(key, defaultVal);
    }

    public void clearSharePrefs() {
        sharedPreferences.edit().clear().apply();
    }

    public void savePref(String key, int value) {
        editor.putInt(key, value);
        editor.commit();
    }
}
