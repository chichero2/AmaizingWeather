package com.amaizzzing.amaizingweather.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.amaizzzing.amaizingweather.Constants;
import com.amaizzzing.amaizingweather.R;
import com.amaizzzing.amaizingweather.WeatherRequest;
import com.amaizzzing.amaizingweather.functions.strategyWeather.ContextStrategyWeather;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class SomeDaysAdapter extends RecyclerView.Adapter<SomeDaysAdapter.ViewHolder> implements Constants {
    public interface OnItemClickListener {
        void onItemClick(int pos);

        void onHourlyForecastClick(int pos);
    }

    private static SomeDaysAdapter.OnItemClickListener mListener;

    public void setOnItemClickListener(SomeDaysAdapter.OnItemClickListener listener) {
        mListener = listener;
    }

    private ArrayList<WeatherRequest> workList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tempSomeDaysItem;
        private ImageView imageSomeDaysItem;
        private TextView dateSomeDaysItem;
        private CardView cvSomeDaysItem;

        ViewHolder(View v) {
            super(v);

            imageSomeDaysItem = v.findViewById(R.id.imageSomeDaysItem);
            ImageView imageHourlyForecastSomeDaysItem = v.findViewById(R.id.imageHourlyForecastSomeDaysItem);
            tempSomeDaysItem = v.findViewById(R.id.tempSomeDaysItem);
            dateSomeDaysItem = v.findViewById(R.id.dateSomeDaysItem);
            cvSomeDaysItem = v.findViewById(R.id.cvSomeDaysItem);

            imageSomeDaysItem.setOnClickListener(v1 -> mListener.onItemClick(getAdapterPosition()));
            imageHourlyForecastSomeDaysItem.setOnClickListener(v12 -> mListener.onHourlyForecastClick(getAdapterPosition()));
        }
    }

    public SomeDaysAdapter(ArrayList<WeatherRequest> workList) {
        this.workList = workList;
    }

    @NonNull
    @Override
    public SomeDaysAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.some_days_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final SomeDaysAdapter.ViewHolder holder, final int position) {
        holder.tempSomeDaysItem.setText(String.format(((int) workList.get(position).getMain().getTemp() > 0 ? holder.tempSomeDaysItem.getContext().getResources().getString(R.string.plus_info11) : holder.tempSomeDaysItem.getContext().getResources().getString(R.string.info11)), (int) workList.get(position).getMain().getTemp()));
        holder.imageSomeDaysItem.setImageDrawable(new ContextStrategyWeather(ContextStrategyWeather.getStrategyWeatherInterface(holder.imageSomeDaysItem.getContext(), workList.get(position).getStrategyWeather())).getDrawable(holder.imageSomeDaysItem.getContext()));
        holder.dateSomeDaysItem.setText((new SimpleDateFormat(DD_MM_YY, Locale.ENGLISH)).format(workList.get(position).getDt()));
        if (workList.get(position).isChecked()) {
            holder.cvSomeDaysItem.setCardBackgroundColor(holder.cvSomeDaysItem.getResources().getColor(R.color.yellow));
        } else {
            holder.cvSomeDaysItem.setCardBackgroundColor(holder.cvSomeDaysItem.getResources().getColor(R.color.invisible_color));
        }
        holder.imageSomeDaysItem.setOnClickListener(v -> mListener.onItemClick(position));
    }

    @Override
    public int getItemCount() {
        return workList.size();
    }
}
