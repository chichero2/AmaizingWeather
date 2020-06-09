package com.amaizzzing.amaizingweather.services;

import android.app.NotificationManager;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.amaizzzing.amaizingweather.MyApplication;
import com.amaizzzing.amaizingweather.R;
import com.amaizzzing.amaizingweather.WeatherDB;
import com.amaizzzing.amaizingweather.models.WeatherRequestRoom;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String MESSAGE_SHTORM = "Штормовое предупреждение!";
    private static final String TYPICAL_WEATHER = "Обычная погода";
    private static final String CHANNEL_ID = "2";
    private static final String DEFAULT_TITLE_MESSAGE = "Push Message";

    private String textMessage;
    private int messageId = 33;

    public MyFirebaseMessagingService() {
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        WeatherDB weatherDB = MyApplication.getInstance().getDatabase();
        weatherDB.CityDao().getLastHistoryCity()
                .flatMap(cityRoom -> weatherDB.weatherRequestRoomDao().getWeatherForCity(cityRoom.getId_city()))
                .flatMapObservable(Observable::fromIterable)
                .lastElement()
                .doOnSuccess(weatherRequestRoom -> {
                    if (weatherRequestRoom.getWind() > 10) {
                        textMessage = MESSAGE_SHTORM;
                    } else {
                        textMessage = TYPICAL_WEATHER;
                    }
                    String title = remoteMessage.getNotification().getTitle();
                    if (title == null) {
                        title = DEFAULT_TITLE_MESSAGE;
                    }
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                            .setSmallIcon(R.drawable.bottom_nav_weather)
                            .setContentTitle(title)
                            .setContentText(textMessage);
                    NotificationManager notificationManager =
                            (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.notify(messageId, builder.build());
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
    }
}
