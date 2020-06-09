package com.amaizzzing.amaizingweather.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.amaizzzing.amaizingweather.AllDataFromServer;
import com.amaizzzing.amaizingweather.BuildConfig;
import com.amaizzzing.amaizingweather.Constants;
import com.amaizzzing.amaizingweather.R;
import com.amaizzzing.amaizingweather.functions.OtherFunctions;
import com.amaizzzing.amaizingweather.mappers.CityMapper;
import com.amaizzzing.amaizingweather.mappers.UpdatedDataForActivity;
import com.amaizzzing.amaizingweather.models.City;

import java.util.ArrayList;

import io.reactivex.disposables.CompositeDisposable;

public class DialogShowWeatherFromMap extends DialogFragment implements Constants {
    private ImageView weatherImage_DialogFromMap;
    private TextView typeWeither_DialogFromMap;
    private TextView temp_DialogFromMap;
    private TextView feels_DialogFromMap;
    private TextView humidity_DialogFromMap;
    private TextView wind_DialogFromMap;
    private TextView sunrice_DialogFromMap;
    private TextView sunset_DialogFromMap;
    private TextView nameLocation_DialogFromMap;
    private ProgressBar pb_DialogFromMap;
    private LinearLayout llMainLayout_DialogFromMap;

    private CompositeDisposable compositeDisposable;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_show_weather_from_map, container, false);

        initViews(v);
        compositeDisposable = new CompositeDisposable();
        Bundle args = getArguments();
        if (args != null) {
            double lat = args.getDouble(LAT);
            double lng = args.getDouble(LNG);
            String nameLocation = args.getString(CITY_NAME);
            pb_DialogFromMap.setVisibility(View.VISIBLE);
            llMainLayout_DialogFromMap.setVisibility(View.GONE);
            City city = prepareLocationForSearch(lat, lng, nameLocation);
            loadInfoLocationFromServer(city);
        }

        return v;
    }

    private City prepareLocationForSearch(double lat, double lng, String nameLocation) {
        City city = new City();
        city.setForecast(new ArrayList<>());
        city.setName(nameLocation);
        city.setLat(lat);
        city.setLng(lng);
        return city;
    }

    private void loadInfoLocationFromServer(City city) {
        compositeDisposable.add(AllDataFromServer.getWeatherRetrofit(city, TYPE_RETROFIT_UNITS, BuildConfig.WEATHER_API_KEY)
                .subscribe(
                        weatherRequest -> {
                        },
                        throwable -> {
                            pb_DialogFromMap.setVisibility(View.GONE);
                            llMainLayout_DialogFromMap.setVisibility(View.VISIBLE);
                            OtherFunctions.makeShortToast(getResources().getString(R.string.text_error_load_data), requireContext());
                        },
                        () -> {
                            UpdatedDataForActivity updForAct = new UpdatedDataForActivity(CityMapper.CityToRoom(city), 0, getContext());
                            fillViews(updForAct, city.getName());
                            pb_DialogFromMap.setVisibility(View.GONE);
                            llMainLayout_DialogFromMap.setVisibility(View.VISIBLE);
                        }));
    }

    private void initViews(View v) {
        weatherImage_DialogFromMap = v.findViewById(R.id.weatherImage_DialogFromMap);
        typeWeither_DialogFromMap = v.findViewById(R.id.typeWeither_DialogFromMap);
        temp_DialogFromMap = v.findViewById(R.id.temp_DialogFromMap);
        feels_DialogFromMap = v.findViewById(R.id.feels_DialogFromMap);
        humidity_DialogFromMap = v.findViewById(R.id.humidity_DialogFromMap);
        wind_DialogFromMap = v.findViewById(R.id.wind_DialogFromMap);
        sunrice_DialogFromMap = v.findViewById(R.id.sunrice_DialogFromMap);
        sunset_DialogFromMap = v.findViewById(R.id.sunset_DialogFromMap);
        nameLocation_DialogFromMap = v.findViewById(R.id.nameLocation_DialogFromMap);
        pb_DialogFromMap = v.findViewById(R.id.pb_DialogFromMap);
        llMainLayout_DialogFromMap = v.findViewById(R.id.llMainLayout_DialogFromMap);
    }

    private void fillViews(UpdatedDataForActivity updatedDataForActivity, String cityName) {
        nameLocation_DialogFromMap.setText(cityName);
        temp_DialogFromMap.setText(updatedDataForActivity.getOriginalTemp());
        feels_DialogFromMap.setText(updatedDataForActivity.getOriginalFeels());
        humidity_DialogFromMap.setText(updatedDataForActivity.getOriginalHumidity());
        wind_DialogFromMap.setText(updatedDataForActivity.getOriginalWind());
        typeWeither_DialogFromMap.setText(updatedDataForActivity.getOriginalTypeWeather());
        sunrice_DialogFromMap.setText(updatedDataForActivity.getOriginalSunrice());
        sunset_DialogFromMap.setText(updatedDataForActivity.getOriginalSunset());
        weatherImage_DialogFromMap.setImageDrawable(updatedDataForActivity.getWeatherImage());
    }

    @Override
    public void onResume() {
        super.onResume();
        WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = LinearLayout.LayoutParams.MATCH_PARENT;
        params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        getDialog().getWindow().setAttributes(params);
    }

    @Override
    public void onDestroyView() {
        compositeDisposable.dispose();
        super.onDestroyView();
    }
}
