package com.amaizzzing.amaizingweather.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amaizzzing.amaizingweather.AllDataFromServer;
import com.amaizzzing.amaizingweather.Constants;
import com.amaizzzing.amaizingweather.DailyWeatherDiffUtilCallback;
import com.amaizzzing.amaizingweather.R;
import com.amaizzzing.amaizingweather.WeatherRequest;
import com.amaizzzing.amaizingweather.adapters.SomeDaysAdapter;
import com.amaizzzing.amaizingweather.custom_view.ThermometerView;
import com.amaizzzing.amaizingweather.dialogs.DialogHourlyForecast;
import com.amaizzzing.amaizingweather.dialogs.DialogLoadData;
import com.amaizzzing.amaizingweather.functions.OtherFunctions;
import com.amaizzzing.amaizingweather.functions.SpacesItemDecoration;
import com.amaizzzing.amaizingweather.functions.strategyWeather.ContextStrategyWeather;
import com.amaizzzing.amaizingweather.functions.strategyWeather.DefaultBackStrategy;
import com.amaizzzing.amaizingweather.mappers.CityMapper;
import com.amaizzzing.amaizingweather.mappers.MapperWeatherRequest;
import com.amaizzzing.amaizingweather.mappers.UpdatedDataForActivity;
import com.amaizzzing.amaizingweather.models.CityRoom;
import com.amaizzzing.amaizingweather.models.WeatherRequestRoom;
import com.amaizzzing.amaizingweather.repository.ContextCity;
import com.amaizzzing.amaizingweather.repository.HistoryChooseCitiesRepository;
import com.amaizzzing.amaizingweather.repository.Repository;
import com.amaizzzing.amaizingweather.repository.WeatherRequestRepositoryRoom;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.maps.model.LatLng;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static android.content.Context.LOCATION_SERVICE;

public class MainFragment extends Fragment implements Constants, SomeDaysAdapter.OnItemClickListener {
    public interface OnHistoryClickListener {
        void onItemClick();
    }

    private static OnHistoryClickListener mListener;

    private static final int PERMISSION_REQUEST_CODE = 10;
    private TextView temp_MainActivity;
    private TextView typeWeither_MainActivity;
    private TextView currentCity_MainActivity;
    private TextView feels_MainActivity;
    private TextView humidity_MainActivity;
    private TextView wind_MainActivity;
    private TextView sunrice_MainActivity;
    private TextView sunset_MainActivity;
    private TextView textCurrentCity_MainActivity;
    private ImageView weatherImage_MainActivity;
    private ConstraintLayout clMainLayout;
    private RecyclerView rvSomeDays_MainFragment;
    private ThermometerView thermometerView;
    private CardView cvHistory_MainFragment;

    private CityRoom currentCity;
    private SomeDaysAdapter someDaysAdapter;
    private int oldClickPosition = -1;
    private UpdatedDataForActivity updForAct;
    private CompositeDisposable compositeDisposable;
    private Repository cityRepository;
    private WeatherRequestRepositoryRoom weatherRequestRepositoryRoom;
    private List<WeatherRequest> newList;

    private LocationManager locationManager;
    private List<Address> addresses = new ArrayList<>();
    private HistoryChooseCitiesRepository historyChooseCitiesRepository;
    private DialogLoadData dialogLoadData;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main, container, false);

        AdView mainAdView = v.findViewById(R.id.mainAdView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mainAdView.loadAd(adRequest);


        initViews(v);
        initListeners();
        initVariables();
        prepareCurrentCityToShow();

        return v;
    }

    private void prepareCurrentCityToShow() {
        compositeDisposable.add(cityRepository.getLastHistoryCity()
                .flatMap(city -> {
                    currentCity = city;
                    currentCity.setForecast(new ArrayList<>());
                    currentCity.setDailyForecast();
                    return weatherRequestRepositoryRoom.getWeatherForCity(city.getId_city());
                })
                .flatMapObservable(Observable::fromIterable)
                .doOnNext(weatherRequestRoom -> currentCity.getForecast().add(MapperWeatherRequest.RoomToWeatherRequest(weatherRequestRoom)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        weatherRequestRoom -> {
                        },
                        Throwable::printStackTrace,
                        () -> {
                            if (currentCity != null) {
                                currentCity.setDailyForecast();
                                initRecycler();
                                if (requireActivity().getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
                                    rvSomeDays_MainFragment.addItemDecoration(new SpacesItemDecoration(-6, 0));
                                }
                                showCityFromServer();
                                createConstraintsForLayout();
                                updateViewsAfterUpdateDB();
                            }
                        }));
    }

    private void updateViewsAfterUpdateDB() {
        compositeDisposable.add(weatherRequestRepositoryRoom.getWeatherForCityFlow()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(weatherRequestRooms -> Observable.fromIterable(weatherRequestRooms)
                        .filter(weatherRequestRoom -> weatherRequestRoom.getId_city() == currentCity.getId_city())
                        .doOnNext(weatherRequestRoom -> newList.add(MapperWeatherRequest.RoomToWeatherRequest(weatherRequestRoom)))
                        .doOnError(Throwable::printStackTrace)
                        .doOnComplete(() -> {
                            if (currentCity.getForecast() != null && currentCity.getForecast().size() == 0) {
                                currentCity.setForecast(newList);
                                currentCity.setDailyForecast();
                                initRecycler();
                                fillActivityViews(currentCity,0);
                            } else {
                                DailyWeatherDiffUtilCallback diffUtilCallback = new DailyWeatherDiffUtilCallback(newList, currentCity);
                                DiffUtil.DiffResult dailyWeatherResult = DiffUtil.calculateDiff(diffUtilCallback);
                                dailyWeatherResult.dispatchUpdatesTo(someDaysAdapter);
                                addNewDaysInRecycler();
                            }
                            newList = new ArrayList<>();
                        })
                        .subscribe()
                ));
    }

    private void addNewDaysInRecycler() {
        CityRoom cityRoom=new CityRoom();
        cityRoom.setForecast(newList);
        cityRoom.setDailyForecast();
        if(cityRoom.getDailyForecast().size()!=currentCity.getDailyForecast().size()){
            for(int i=currentCity.getDailyForecast().size();i<cityRoom.getDailyForecast().size();i++){
                currentCity.getDailyForecast().add(cityRoom.getDailyForecast().get(i));
                someDaysAdapter.notifyItemInserted(i);
            }

        }
    }

    private void loadPicasso(int pos) {
        ImageView img = new ImageView(getContext());
        String typeWeather = getResources().getString(R.string.weather_clear);
        if (currentCity.getDailyForecast() != null && currentCity.getDailyForecast().size() != 0) {
            typeWeather = currentCity.getDailyForecast().get(pos).getWeather()[0].getMain();
        }
        Picasso.get().load(ContextStrategyWeather.getUrlForPicasso(requireContext(), typeWeather)).into(img, new Callback() {
            @Override
            public void onSuccess() {
                clMainLayout.setBackground(img.getDrawable());
            }

            @Override
            public void onError(Exception e) {
                clMainLayout.setBackground(new ContextStrategyWeather(new DefaultBackStrategy()).getDrawable(getContext()));
            }
        });
    }

    private void initVariables() {
        compositeDisposable = new CompositeDisposable();
        cityRepository = new ContextCity(getContext()).getRepository();
        weatherRequestRepositoryRoom = new WeatherRequestRepositoryRoom();
        mListener = (OnHistoryClickListener) requireActivity();
        currentCity = new CityRoom();
        newList = new ArrayList<>();
        historyChooseCitiesRepository = new HistoryChooseCitiesRepository();
        setHasOptionsMenu(true);
    }

    private void createConstraintsForLayout() {
        if ((getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)) {
            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(clMainLayout);
            constraintSet.connect(R.id.currentCity_MainActivity, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0);
            constraintSet.applyTo(clMainLayout);
            textCurrentCity_MainActivity.setVisibility(View.GONE);
            currentCity_MainActivity.setVisibility(View.GONE);
            typeWeither_MainActivity.setTextSize(18);
            weatherImage_MainActivity.setMinimumWidth(45);
            weatherImage_MainActivity.setMinimumHeight(45);
        }
    }

    private void initViews(View v) {
        clMainLayout = v.findViewById(R.id.clMainLayout);
        temp_MainActivity = v.findViewById(R.id.temp_MainActivity);
        typeWeither_MainActivity = v.findViewById(R.id.typeWeither_MainActivity);
        weatherImage_MainActivity = v.findViewById(R.id.weatherImage_MainActivity);
        currentCity_MainActivity = v.findViewById(R.id.currentCity_MainActivity);
        feels_MainActivity = v.findViewById(R.id.feels_MainActivity);
        humidity_MainActivity = v.findViewById(R.id.humidity_MainActivity);
        wind_MainActivity = v.findViewById(R.id.wind_MainActivity);
        sunrice_MainActivity = v.findViewById(R.id.sunrice_MainActivity);
        sunset_MainActivity = v.findViewById(R.id.sunset_MainActivity);
        rvSomeDays_MainFragment = v.findViewById(R.id.rvSomeDays_MainFragment);
        textCurrentCity_MainActivity = v.findViewById(R.id.textCurrentCity_MainActivity);
        thermometerView = v.findViewById(R.id.thermometrView_MainFragment);
        cvHistory_MainFragment = v.findViewById(R.id.cvHistory_MainFragment);
    }

    private void initListeners() {
        currentCity_MainActivity.setOnClickListener(v -> {
            currentCity.clearWeatherForecast();
            if (requireActivity().getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
                requireActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.containerFragment_MainActivity, new MyFragmentFactory().getFragment(FragmentTypes.FRAGMENT_CITIES_CHOOSE), FRAGMENT_CITIES_CHOOSE)
                        .addToBackStack(FRAGMENT_CITIES_CHOOSE)
                        .commit();

            }
        });
        rvSomeDays_MainFragment.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    showOrHideCvHistory();
                }
            }
        });
        cvHistory_MainFragment.setOnClickListener(v -> mListener.onItemClick());
    }

    private void showOrHideCvHistory() {
        int review_position = ((LinearLayoutManager) rvSomeDays_MainFragment.getLayoutManager()).findFirstVisibleItemPosition();
        if (review_position == 0) {
            cvHistory_MainFragment.setVisibility(View.VISIBLE);
        } else {
            cvHistory_MainFragment.setVisibility(View.GONE);
        }
    }

    private void showCityFromServer() {
        fillActivityViews(currentCity, currentCity.getChosenForecastPosition());
        loadPicasso(currentCity.getChosenForecastPosition());
    }

    private void fillViews(UpdatedDataForActivity updatedDataForActivity) {
        temp_MainActivity.setText(updatedDataForActivity.getOriginalTemp());
        feels_MainActivity.setText(updatedDataForActivity.getOriginalFeels());
        humidity_MainActivity.setText(updatedDataForActivity.getOriginalHumidity());
        wind_MainActivity.setText(updatedDataForActivity.getOriginalWind());
        typeWeither_MainActivity.setText(updatedDataForActivity.getOriginalTypeWeather());
        sunrice_MainActivity.setText(updatedDataForActivity.getOriginalSunrice());
        sunset_MainActivity.setText(updatedDataForActivity.getOriginalSunset());
        weatherImage_MainActivity.setImageDrawable(updatedDataForActivity.getWeatherImage());
    }

    private void fillActivityViews(final CityRoom city, final int forecastPosition) {
        Disposable disposable = Completable.fromAction(() -> {
            if (city.getDailyForecast() != null) {
                updForAct = new UpdatedDataForActivity(city, forecastPosition, getContext());
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    currentCity_MainActivity.setText(city.getName());
                    if (city.getDailyForecast() != null) {
                        showThermometrDegrees(city);
                        fillViews(updForAct);
                    } else {
                        fillViews(new UpdatedDataForActivity(null, -1, getContext()));
                    }
                });
        compositeDisposable.add(disposable);
    }

    private void showThermometrDegrees(CityRoom city) {
        if (city.getDailyForecast() != null && city.getDailyForecast().size() != 0) {
            if (oldClickPosition == -1) {
                thermometerView.setDegrees((int) city.getDailyForecast().get(0).getMain().getTemp());
            } else {
                thermometerView.setDegrees((int) city.getDailyForecast().get(oldClickPosition).getMain().getTemp());
            }
        }
    }

    private void initRecycler() {
        if (currentCity.getDailyForecast() != null) {
            rvSomeDays_MainFragment.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

            someDaysAdapter = new SomeDaysAdapter((ArrayList<WeatherRequest>) currentCity.getDailyForecast());
            someDaysAdapter.setOnItemClickListener(this);
            rvSomeDays_MainFragment.setAdapter(someDaysAdapter);
            someDaysAdapter.notifyDataSetChanged();
            oldClickPosition = currentCity.getChosenForecastPosition();
            rvSomeDays_MainFragment.scrollToPosition(oldClickPosition);
        }
    }

    @Override
    public void onItemClick(int pos) {
        if (oldClickPosition != -1) {
            currentCity.getDailyForecast().get(oldClickPosition).setChecked(false);
            someDaysAdapter.notifyItemChanged(oldClickPosition);
        }
        currentCity.getDailyForecast().get(pos).setChecked(true);
        someDaysAdapter.notifyItemChanged(pos);
        oldClickPosition = pos;
        fillActivityViews(currentCity, pos);
        loadPicasso(pos);
    }

    @Override
    public void onHourlyForecastClick(int pos) {
        DialogHourlyForecast dialogHourlyForecast = new DialogHourlyForecast();
        Bundle bundle = new Bundle();
        bundle.putLong(getString(R.string.current_date), currentCity.getDailyForecast().get(pos).getDt());
        bundle.putString(CITY_NAME, currentCity.getName());
        dialogHourlyForecast.setArguments(bundle);
        dialogHourlyForecast.show(requireActivity().getSupportFragmentManager(), DIALOG_HOURLY_FORECAST);
        onItemClick(pos);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.app_bar_main_fragment_menu, menu);

        MenuItem searchLocation = menu.findItem(R.id.gps_search_main_fragment_menu);
        searchLocation.setOnMenuItemClickListener(item -> {
            dialogLoadData = new DialogLoadData();
            dialogLoadData.show(requireActivity().getSupportFragmentManager(), DIALOG_LOAD_DATA);
            requestPemissions();
            return false;
        });
    }

    private void requestPemissions() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            requestLocation();
        } else {
            requestLocationPermissions();
        }
    }

    private void requestLocationPermissions() {
        if (!ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION
                    },
                    PERMISSION_REQUEST_CODE);
        }
    }

    private void requestLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            return;
        locationManager = (LocationManager) requireActivity().getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        String provider = locationManager.getBestProvider(criteria, true);
        if (provider != null) {
            locationManager.requestLocationUpdates(provider, 1, 1, locationListener);
        }
    }

    private void getAddress(final LatLng location) {
        final Geocoder geocoder = new Geocoder(requireContext());
        addresses = new ArrayList<>();
        compositeDisposable.add(Completable.fromAction(() -> addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    currentCity.setName(addresses.get(0).getLocality());
                    AllDataFromServer allDataFromServer = new AllDataFromServer(currentCity);
                    compositeDisposable.add(allDataFromServer.loadDataFromServer(1)
                            .subscribe(
                                    weatherRequest -> compositeDisposable.add(weatherRequestRepositoryRoom.getWeatherByIdCityAndDate(allDataFromServer.getCityToLoadData().getId(), weatherRequest.getDt())
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe(
                                                    weatherRequestRoom -> {
                                                    },
                                                    Throwable::printStackTrace,
                                                    () -> compositeDisposable.add(weatherRequestRepositoryRoom.insert(MapperWeatherRequest.WeatherRequestToRoom(allDataFromServer.getCityToLoadData().getId(), weatherRequest))
                                                            .subscribeOn(Schedulers.io())
                                                            .observeOn(AndroidSchedulers.mainThread())
                                                            .subscribe()
                                                    )
                                            )
                                    ),
                                    throwable -> {
                                        dialogLoadData.dismiss();
                                        throwable.printStackTrace();
                                    },
                                    () -> {
                                        currentCity = CityMapper.CityToRoom(allDataFromServer.getCityToLoadData());
                                        cityRepository.getCityByName(currentCity.getName())
                                                .doOnComplete(() -> cityRepository.insert(currentCity)
                                                        .subscribeOn(Schedulers.io())
                                                        .observeOn(AndroidSchedulers.mainThread())
                                                        .subscribe())
                                                .subscribeOn(Schedulers.io())
                                                .observeOn(AndroidSchedulers.mainThread())
                                                .subscribe();
                                        historyChooseCitiesRepository.addToHistory(currentCity)
                                                .subscribeOn(Schedulers.io())
                                                .observeOn(AndroidSchedulers.mainThread())
                                                .subscribe();
                                        dialogLoadData.dismiss();
                                        prepareCurrentCityToShow();
                                    }
                            )
                    );
                }, throwable -> OtherFunctions.makeShortToast(getResources().getString(R.string.error_load_data), requireContext())));
    }

    private LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            double lat = location.getLatitude();
            double lng = location.getLongitude();
            getAddress(new LatLng(lat, lng));
            locationManager.removeUpdates(locationListener);
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }
    };

    @Override
    public void onDestroyView() {
        compositeDisposable.dispose();
        super.onDestroyView();
    }
}
