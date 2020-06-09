package com.amaizzzing.amaizingweather.fragments;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.amaizzzing.amaizingweather.Constants;
import com.amaizzzing.amaizingweather.R;
import com.amaizzzing.amaizingweather.dialogs.DialogShowWeatherFromMap;
import com.amaizzzing.amaizingweather.functions.OtherFunctions;
import com.amaizzzing.amaizingweather.functions.strategyWeather.ContextStrategyWeather;
import com.amaizzzing.amaizingweather.mappers.MapperWeatherRequest;
import com.amaizzzing.amaizingweather.models.CityRoom;
import com.amaizzzing.amaizingweather.repository.ContextCity;
import com.amaizzzing.amaizingweather.repository.HistoryChooseCitiesRepository;
import com.amaizzzing.amaizingweather.repository.Repository;
import com.amaizzzing.amaizingweather.repository.WeatherRequestRepositoryRoom;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class FragmentMaps extends Fragment implements OnMapReadyCallback, Constants {
    private static final int SIZE_MARKER = 80;

    private MapView mapView;
    private GoogleMap map;
    private Repository cityRepository;
    private WeatherRequestRepositoryRoom weatherRequestRepositoryRoom;
    private List<Marker> markers = new ArrayList<>();
    private List<Address> addresses = new ArrayList<>();
    private CompositeDisposable compositeDisposable;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_maps, container, false);

        mapView = v.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        initVariables();

        return v;
    }

    private void initVariables() {
        cityRepository = new ContextCity(requireContext()).getRepository();
        weatherRequestRepositoryRoom = new WeatherRequestRepositoryRoom();
        compositeDisposable = new CompositeDisposable();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.getUiSettings().setMyLocationButtonEnabled(false);
        map.setMyLocationEnabled(true);
        compositeDisposable.add(cityRepository.getCityList()
                .flatMapObservable(Observable::fromIterable)
                .doOnNext(city -> {
                    city.setForecast(new ArrayList<>());
                    compositeDisposable.add(weatherRequestRepositoryRoom.getWeatherForCity(city.getId_city())
                            .flatMapObservable(Observable::fromIterable)
                            .subscribe(
                                    weatherRequestRoom -> city.getForecast().add(MapperWeatherRequest.RoomToWeatherRequest(weatherRequestRoom)),
                                    Throwable::printStackTrace,
                                    () -> {
                                    }
                            ));

                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(cityRoom -> addMarkers(new LatLng(cityRoom.getLat(), cityRoom.getLng()), cityRoom)));
        compositeDisposable.add(cityRepository.getLastHistoryCity()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(cityRoom -> map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(cityRoom.getLat(), cityRoom.getLng()), 5))));

        map.setOnMapClickListener(this::getAddress);

        map.setOnMarkerClickListener(marker -> {
            if (marker != null) {
                CityRoom cityRoom = new CityRoom();
                cityRoom.setName(marker.getTitle());
                cityRoom.setId_city(Long.parseLong(marker.getSnippet()));
                HistoryChooseCitiesRepository historyChooseCitiesRepository = new HistoryChooseCitiesRepository();
                historyChooseCitiesRepository.addToHistory(cityRoom)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe();
                cityRepository.publisherNotify(cityRoom.getName());
            }
            return false;
        });
    }

    private void addMarkers(LatLng location, CityRoom city) {
        if(city.getForecast().size()!=0) {
            Drawable d = new ContextStrategyWeather(ContextStrategyWeather.getStrategyWeatherInterface(requireContext(), city.getForecast().get(0).getStrategyWeather())).getDrawable(requireContext());
            Bitmap bitmapToMarker = ((BitmapDrawable) d).getBitmap();
            bitmapToMarker = Bitmap.createScaledBitmap(bitmapToMarker, SIZE_MARKER, SIZE_MARKER, false);
            Marker marker = map.addMarker(new MarkerOptions()
                    .position(location)
                    .title(city.getName())
                    .snippet(String.valueOf(city.getId_city()))
                    .icon(BitmapDescriptorFactory.fromBitmap(bitmapToMarker)
                    )
            );
            markers.add(marker);
        }
    }

    private void getAddress(final LatLng location) {
        final Geocoder geocoder = new Geocoder(requireContext());
        addresses = new ArrayList<>();
        compositeDisposable.add(Completable.fromAction(() -> addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    DialogShowWeatherFromMap dialogShowWeatherFromMap = new DialogShowWeatherFromMap();
                    Bundle bundle = new Bundle();
                    bundle.putDouble(LAT, location.latitude);
                    bundle.putDouble(LNG, location.longitude);
                    bundle.putString(CITY_NAME, addresses.get(0).getAddressLine(0));
                    dialogShowWeatherFromMap.setArguments(bundle);
                    dialogShowWeatherFromMap.show(requireActivity().getSupportFragmentManager(), DIALOG_SHOW_WEATHER_FROM_MAP);
                }, throwable -> OtherFunctions.makeShortToast(getResources().getString(R.string.text_error_load_data), requireContext())));
    }


    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }


    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        compositeDisposable.dispose();
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

}
