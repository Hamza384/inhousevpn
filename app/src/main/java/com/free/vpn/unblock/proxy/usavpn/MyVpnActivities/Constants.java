package com.free.vpn.unblock.proxy.usavpn.MyVpnActivities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import com.free.vpn.unblock.proxy.usavpn.MyVpnUtils.VpnDailyNotificationReceiver;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.Calendar;


public class Constants {

    public static final String CHANNEL_ID = "vpn_channel";
    public static final String CHANNEL_NAME = "VPN";
    public static final String CHANNEL_DESCRIPTION = "VPN";
    private static final String TAG = "Constants";
    public static String NotifMessage = "Easy to use, One click connect to QuickVPN.";
    public static String shareSubject = "QuickVPN";
    public static String shareMessage = "QuickVPN\nDownload Free Now\nhttps://play.google.com/store/apps/details?id=com.free.vpn.unblock.proxy.usavpn";
    public static String NotifTitle = "QuickVPN";
    public static int Alarm_Id = 1234;
    public static int Notif_Id = 1214;
    public static String KEY_AD_COUNT = "count";
    public static boolean isFirstTime = true;



    public static boolean isOnline(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo nInfo = cm.getActiveNetworkInfo();
            return nInfo != null && nInfo.isConnected();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void setAlarmEveryDay(Context context) {
        Calendar targetCal = setAlarmTime();
        Intent intent = new Intent(context, VpnDailyNotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, Constants.Alarm_Id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, targetCal.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    public static Calendar setAlarmTime() {
        Calendar time = Calendar.getInstance();
        //time.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
        time.set(Calendar.HOUR, 9);
        time.set(Calendar.MINUTE, 0);
        time.set(Calendar.SECOND, 0);
        time.set(Calendar.MILLISECOND, 0);
        time.set(Calendar.AM_PM, Calendar.AM);
        if (System.currentTimeMillis() > time.getTimeInMillis()) {
            long add = AlarmManager.INTERVAL_DAY;
            long oldTime = time.getTimeInMillis();
            time.setTimeInMillis(oldTime + add);
        }
        return time;
    }

    public static void sendAnalytics(FirebaseAnalytics analytics, String message) {
        Bundle bundle = new Bundle();
        bundle.putString("Activity_Name", message);
        analytics.logEvent("Activity_Name", bundle);
    }

}


