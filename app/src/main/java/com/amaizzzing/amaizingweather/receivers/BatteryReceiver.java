package com.amaizzzing.amaizingweather.receivers;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import com.amaizzzing.amaizingweather.Constants;
import com.amaizzzing.amaizingweather.R;

public class BatteryReceiver extends BroadcastReceiver implements Constants {
    public static final String CHANNEL_ID = "2";
    public static final int MESSAGE_ID = 31;
    public static final int MIN_LEVEL_BATTERY = 15;
    public static final String NAME_LEVEL = "level";

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            int level = intent.getIntExtra(NAME_LEVEL, 0);
            if (level <= MIN_LEVEL_BATTERY) {
                NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.drawable.bottom_nav_weather)
                        .setContentTitle(TITLE_NETWORK_NOTIFICATION)
                        .setContentText(LOW_LEVEL_BATTERY_TEXT_NOTIFICATION);
                NotificationManager notificationManager =
                        (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(MESSAGE_ID, builder.build());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
