package com.amaizzzing.amaizingweather;

import androidx.recyclerview.widget.DiffUtil;

import com.amaizzzing.amaizingweather.functions.OtherFunctions;
import com.amaizzzing.amaizingweather.models.CityRoom;

import java.util.List;

public class DailyWeatherDiffUtilCallback extends DiffUtil.Callback {
    private final List<WeatherRequest> oldList;
    private final List<WeatherRequest> newList;
    private final CityRoom cityOld;
    private final CityRoom cityNew;

    public DailyWeatherDiffUtilCallback(List<WeatherRequest> newList, CityRoom city) {
        this.oldList = city.getDailyForecast();
        this.cityOld = city;
        cityNew = new CityRoom();
        cityNew.setForecast(newList);
        cityNew.setDailyForecast();
        this.newList = cityNew.getDailyForecast();
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        WeatherRequest weatherRequestOld = oldList.get(oldItemPosition);
        WeatherRequest weatherRequestNew = newList.get(newItemPosition);

        return weatherRequestOld.getDt() == weatherRequestNew.getDt();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        WeatherRequest weatherRequestOld = oldList.get(oldItemPosition);
        WeatherRequest weatherRequestNew = newList.get(newItemPosition);
        int oldCount = cityOld.getCountHourlyForecastInDay(weatherRequestOld.getDt());
        int newCount = cityNew.getCountHourlyForecastInDay(weatherRequestNew.getDt());

        return oldCount == newCount;
    }
}
