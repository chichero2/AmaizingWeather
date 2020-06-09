package com.amaizzzing.amaizingweather.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amaizzzing.amaizingweather.R;
import com.amaizzzing.amaizingweather.WeatherRequest;
import com.amaizzzing.amaizingweather.functions.strategyWeather.ContextStrategyWeather;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class HourlyForecastAdapter extends RecyclerView.Adapter<HourlyForecastAdapter.ViewHolder> {
    private ArrayList<WeatherRequest> workList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView time_hourlyForecastItem;
        ImageView imgTypeWeather_hourlyForecastItem;
        TextView themp_hourlyForecastItem;
        TextView wind_hourlyForecastItem;

        ViewHolder(View v) {
            super(v);
            time_hourlyForecastItem = v.findViewById(R.id.time_hourlyForecastItem);
            imgTypeWeather_hourlyForecastItem = v.findViewById(R.id.imgTypeWeather_hourlyForecastItem);
            themp_hourlyForecastItem = v.findViewById(R.id.themp_hourlyForecastItem);
            wind_hourlyForecastItem = v.findViewById(R.id.wind_hourlyForecastItem);
        }
    }

    public HourlyForecastAdapter(ArrayList<WeatherRequest> workList) {
        this.workList = workList;
    }

    @NonNull
    @Override
    public HourlyForecastAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.hourly_forecast_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final HourlyForecastAdapter.ViewHolder holder, final int position) {
        holder.time_hourlyForecastItem.setText((new SimpleDateFormat("HH.mm", Locale.ENGLISH)).format(workList.get(position).getDt()));
        holder.themp_hourlyForecastItem.setText(String.format(holder.themp_hourlyForecastItem.getContext().getResources().getString(R.string.temp_cities_item), (int)workList.get(position).getMain().getTemp()));
        holder.wind_hourlyForecastItem.setText(String.format(holder.wind_hourlyForecastItem.getResources().getString(R.string.wind_cities_item), (int)workList.get(position).getWind().getSpeed()));
        holder.imgTypeWeather_hourlyForecastItem.setImageDrawable(new ContextStrategyWeather(ContextStrategyWeather.getStrategyWeatherInterface(holder.imgTypeWeather_hourlyForecastItem.getContext(), workList.get(position).getStrategyWeather())).getDrawable(holder.imgTypeWeather_hourlyForecastItem.getContext()));
    }

    @Override
    public int getItemCount() {
        return workList.size();
    }
}
