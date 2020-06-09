package com.amaizzzing.amaizingweather.dialogs;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amaizzzing.amaizingweather.Constants;
import com.amaizzzing.amaizingweather.R;
import com.amaizzzing.amaizingweather.WeatherRequest;
import com.amaizzzing.amaizingweather.adapters.HourlyForecastAdapter;
import com.amaizzzing.amaizingweather.functions.DateFunctions;
import com.amaizzzing.amaizingweather.mappers.MapperWeatherRequest;
import com.amaizzzing.amaizingweather.models.CityRoom;
import com.amaizzzing.amaizingweather.repository.ContextCity;
import com.amaizzzing.amaizingweather.repository.Repository;
import com.amaizzzing.amaizingweather.repository.WeatherRequestRepositoryRoom;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class DialogHourlyForecast extends DialogFragment implements Constants {
    private RecyclerView rvDialogHourlyForecast;

    private CityRoom currentCity;
    private long chosenDate;
    private CompositeDisposable compositeDisposable;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_hourly_forecast, container, false);

        initViews(v);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        compositeDisposable = new CompositeDisposable();
        showData();

        return v;
    }

    private void showData() {
        Repository cityRepository = new ContextCity(getContext()).getRepository();
        WeatherRequestRepositoryRoom weatherRequestRepositoryRoom = new WeatherRequestRepositoryRoom();
        Bundle args = getArguments();
        if (args != null) {
            chosenDate = args.getLong(getResources().getString(R.string.current_date));
            String nameChosenCity = args.getString(CITY_NAME);
            compositeDisposable.add(cityRepository.getCityByName(nameChosenCity)
                    .flatMap(city -> {
                        currentCity = city;
                        currentCity.setForecast(new ArrayList<>());
                        return weatherRequestRepositoryRoom.getWeatherForCity(city.getId_city());
                    })
                    .flatMapObservable(Observable::fromIterable)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            weatherRequestRoom -> currentCity.getForecast().add(MapperWeatherRequest.RoomToWeatherRequest(weatherRequestRoom)), Throwable::printStackTrace,
                            () -> {
                                currentCity.setDailyForecast();
                                initRecycler();
                            }
                    )
            );
        }
    }

    private void initRecycler() {
        rvDialogHourlyForecast.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        HourlyForecastAdapter hourlyForecastAdapter = new HourlyForecastAdapter(getHourlyForecastsInDay());
        rvDialogHourlyForecast.setAdapter(hourlyForecastAdapter);
        hourlyForecastAdapter.notifyDataSetChanged();
    }

    private ArrayList<WeatherRequest> getHourlyForecastsInDay() {
        ArrayList<WeatherRequest> forecastsForAdapter = new ArrayList<>();
        long startDay = DateFunctions.atStartOfDay(chosenDate);
        long endDay = DateFunctions.atEndOfDay(chosenDate);
        for (WeatherRequest wf : currentCity.getForecast()) {
            if (wf.getDt() >= startDay && wf.getDt() <= endDay) {
                forecastsForAdapter.add(wf);
            }
        }
        return forecastsForAdapter;
    }

    private void initViews(View v) {
        rvDialogHourlyForecast = v.findViewById(R.id.rvDialogHourlyForecast);
    }

    @Override
    public void onResume() {
        super.onResume();
        WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = LinearLayout.LayoutParams.MATCH_PARENT;
        params.height = LinearLayout.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes(params);
    }

    @Override
    public void onDestroyView() {
        compositeDisposable.dispose();
        super.onDestroyView();
    }
}
