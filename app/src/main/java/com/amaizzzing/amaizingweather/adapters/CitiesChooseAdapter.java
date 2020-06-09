package com.amaizzzing.amaizingweather.adapters;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.amaizzzing.amaizingweather.R;
import com.amaizzzing.amaizingweather.functions.strategyWeather.ContextStrategyWeather;
import com.amaizzzing.amaizingweather.models.City;
import com.amaizzzing.amaizingweather.models.CityRoom;

import java.util.ArrayList;

public class CitiesChooseAdapter extends RecyclerView.Adapter<CitiesChooseAdapter.ViewHolder> {
    public interface OnItemClickListener {
        void onItemClick(int pos);
    }

    private static OnItemClickListener mListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    private ArrayList<CityRoom> workList;
    private boolean isCheckedInfo;

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView nameCity_CitiesItem;
        private TextView temp_CitiesItem;
        private TextView humidity_CitiesItem;
        private TextView wind_CitiesItem;
        private TextView placeInfo_CitiesItem;
        private ImageView imgWeather_CitiesItem;
        private CardView card_view;

        ViewHolder(View v) {
            super(v);

            initViews(v);
            initListeners();
        }

        private void initListeners() {
            nameCity_CitiesItem.setOnClickListener(v -> mListener.onItemClick(getAdapterPosition()));
        }

        private void initViews(View v) {
            nameCity_CitiesItem = v.findViewById(R.id.nameCity_CitiesItem);
            temp_CitiesItem = v.findViewById(R.id.temp_CitiesItem);
            imgWeather_CitiesItem = v.findViewById(R.id.imgWeather_CitiesItem);
            humidity_CitiesItem = v.findViewById(R.id.humidity_CitiesItem);
            wind_CitiesItem = v.findViewById(R.id.wind_CitiesItem);
            card_view = v.findViewById(R.id.card_view);
            placeInfo_CitiesItem = v.findViewById(R.id.placeInfo_CitiesItem);
        }

    }

    public CitiesChooseAdapter(ArrayList<CityRoom> workList, boolean isCheckedInfo) {
        this.workList = workList;
        this.isCheckedInfo = isCheckedInfo;
    }

    @NonNull
    @Override
    public CitiesChooseAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cities_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final CitiesChooseAdapter.ViewHolder holder, final int position) {
        holder.nameCity_CitiesItem.setText(workList.get(position).getName());
        fillTextViewItems(holder, position);
        setVisibleHumidityAndWind(holder);
    }

    /*private void setColorForCardView(ViewHolder holder, int position) {
        if (workList.get(position).getIsSelect() == 1) {
            holder.card_view.setCardBackgroundColor(holder.card_view.getContext().getResources().getColor(R.color.yellow));
        } else {
            holder.card_view.setCardBackgroundColor(holder.card_view.getContext().getResources().getColor(R.color.invisible_color));
        }
    }*/

    private void setVisibleHumidityAndWind(ViewHolder holder) {
        if (isCheckedInfo) {
            holder.humidity_CitiesItem.setVisibility(View.VISIBLE);
            holder.wind_CitiesItem.setVisibility(View.VISIBLE);
        } else {
            holder.humidity_CitiesItem.setVisibility(View.GONE);
            holder.wind_CitiesItem.setVisibility(View.GONE);
        }
    }

    private void fillTextViewItems(ViewHolder holder, int position) {
        if (workList.get(position).getForecast().size() == 0) {
            fillFields(
                    holder,
                    "-",
                    "-",
                    "-",
                    String.format("%s,%s", workList.get(position).getAdminName(), workList.get(position).getCountryName()),
                    null
            );
        } else {
            fillFields(
                    holder,
                    String.format(holder.imgWeather_CitiesItem.getContext().getResources().getString(R.string.temp_cities_item), (int) workList.get(position).getForecast().get(0).getMain().getTemp()),
                    String.format(holder.humidity_CitiesItem.getResources().getString(R.string.humidity_cities_item), workList.get(position).getForecast().get(0).getMain().getHumidity()),
                    String.format(holder.wind_CitiesItem.getResources().getString(R.string.wind_cities_item), (int) workList.get(position).getForecast().get(0).getWind().getSpeed()),
                    String.format("%s,%s", workList.get(position).getAdminName(), workList.get(position).getCountryName()),
                    new ContextStrategyWeather(ContextStrategyWeather.getStrategyWeatherInterface(holder.humidity_CitiesItem.getContext(), workList.get(position).getForecast().get(0).getStrategyWeather())).getDrawable(holder.imgWeather_CitiesItem.getContext())
            );
        }
    }

    @Override
    public int getItemCount() {
        return workList.size();
    }

    private void fillFields(ViewHolder holder, String temp_CitiesItem, String humidity_CitiesItem, String wind_CitiesItem, String countryAadminName, Drawable imgWeather_CitiesItem) {
        holder.temp_CitiesItem.setText(temp_CitiesItem);
        holder.humidity_CitiesItem.setText(humidity_CitiesItem);
        holder.wind_CitiesItem.setText(wind_CitiesItem);
        holder.imgWeather_CitiesItem.setImageDrawable(imgWeather_CitiesItem);
        holder.placeInfo_CitiesItem.setText(countryAadminName);
    }
}
