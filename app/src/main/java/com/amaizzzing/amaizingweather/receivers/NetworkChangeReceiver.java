package com.amaizzzing.amaizingweather.receivers;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.core.app.NotificationCompat;

import com.amaizzzing.amaizingweather.AllDataFromServer;
import com.amaizzzing.amaizingweather.Constants;
import com.amaizzzing.amaizingweather.MyApplication;
import com.amaizzzing.amaizingweather.R;

public class NetworkChangeReceiver extends BroadcastReceiver implements Constants {
    public static final String CHANNEL_ID = "2";
    public static final int MESSAGE_ID = 30;

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        boolean isConnected = wifi != null && wifi.isConnectedOrConnecting() ||
                mobile != null && mobile.isConnectedOrConnecting();
        if (isConnected) {
            AllDataFromServer.updateCitiesDbFromNetwork();
            MyApplication.isNetwork = true;
        } else {
            MyApplication.isNetwork = false;
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.bottom_nav_weather)
                    .setContentTitle(TITLE_NETWORK_NOTIFICATION)
                    .setContentText(TEXT_NETWORK_NOTIFICATION);
            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(MESSAGE_ID, builder.build());

        }
    }
}
