package com.free.vpn.unblock.proxy.usavpn.MyVpnUtils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.free.vpn.unblock.proxy.usavpn.MyVpnActivities.Constants;
import com.free.vpn.unblock.proxy.usavpn.R;


import static com.free.vpn.unblock.proxy.usavpn.MyVpnActivities.Constants.CHANNEL_DESCRIPTION;
import static com.free.vpn.unblock.proxy.usavpn.MyVpnActivities.Constants.CHANNEL_ID;
import static com.free.vpn.unblock.proxy.usavpn.MyVpnActivities.Constants.CHANNEL_NAME;


public class VpnDailyNotificationReceiver extends BroadcastReceiver {
    Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel(context);
        }
        showNotification();
    }

    private void showNotification() {
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Intent intent = new Intent(context, VpnDailyNotification.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, Constants.Notif_Id, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);

        Notification notificationBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle(Constants.NotifTitle)
                .setContentText(Constants.NotifMessage)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setSound(defaultSoundUri)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent).build();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(Constants.Notif_Id, notificationBuilder);
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private void createChannel(Context context) {
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
        mChannel.setDescription(CHANNEL_DESCRIPTION);
        mChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        if (mNotificationManager != null) {
            mNotificationManager.createNotificationChannel(mChannel);
        }
    }
}