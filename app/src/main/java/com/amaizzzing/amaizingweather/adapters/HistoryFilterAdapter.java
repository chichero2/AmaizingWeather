package com.amaizzzing.amaizingweather.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amaizzzing.amaizingweather.Constants;
import com.amaizzzing.amaizingweather.R;
import com.amaizzzing.amaizingweather.WeatherRequest;
import com.amaizzzing.amaizingweather.functions.strategyWeather.ContextStrategyWeather;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class HistoryFilterAdapter extends RecyclerView.Adapter<HistoryFilterAdapter.ViewHolder> implements Constants {

    private ArrayList<WeatherRequest> workList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView date_HistoryFilterItem;
        private TextView time_HistoryFilterItem;
        private ImageView imgTypeWeather_HistoryFilterItem;
        private TextView themp_HistoryFilterItem;

        ViewHolder(View v) {
            super(v);

            date_HistoryFilterItem = v.findViewById(R.id.date_HistoryFilterItem);
            time_HistoryFilterItem = v.findViewById(R.id.time_HistoryFilterItem);
            imgTypeWeather_HistoryFilterItem = v.findViewById(R.id.imgTypeWeather_HistoryFilterItem);
            themp_HistoryFilterItem = v.findViewById(R.id.themp_HistoryFilterItem);
        }
    }

    public HistoryFilterAdapter(ArrayList<WeatherRequest> workList) {
        this.workList = workList;
    }

    @NonNull
    @Override
    public HistoryFilterAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_filter_item, parent, false);
        return new HistoryFilterAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final HistoryFilterAdapter.ViewHolder holder, final int position) {
        holder.date_HistoryFilterItem.setText((new SimpleDateFormat(DD_MM_YY, Locale.ENGLISH)).format(workList.get(position).getDt()));
        holder.time_HistoryFilterItem.setText((new SimpleDateFormat(HH_MM, Locale.ENGLISH)).format(workList.get(position).getDt()));
        holder.themp_HistoryFilterItem.setText(String.format(holder.themp_HistoryFilterItem.getContext().getResources().getString(R.string.temp_cities_item), (int) workList.get(position).getMain().getTemp()));
        holder.imgTypeWeather_HistoryFilterItem.setImageDrawable(new ContextStrategyWeather(ContextStrategyWeather.getStrategyWeatherInterface(holder.imgTypeWeather_HistoryFilterItem.getContext(), workList.get(position).getStrategyWeather())).getDrawable(holder.imgTypeWeather_HistoryFilterItem.getContext()));
    }

    @Override
    public int getItemCount() {
        return workList.size();
    }
}
