package com.amaizzzing.amaizingweather;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;

import com.amaizzzing.amaizingweather.dialogs.DialogAboutApp;
import com.amaizzzing.amaizingweather.dialogs.DialogErrorServerInfo;
import com.amaizzzing.amaizingweather.fragments.MainFragment;
import com.amaizzzing.amaizingweather.fragments.MyFragmentFactory;
import com.amaizzzing.amaizingweather.mappers.CityMapper;
import com.amaizzzing.amaizingweather.mappers.MapperWeatherRequest;
import com.amaizzzing.amaizingweather.models.CityRoom;
import com.amaizzzing.amaizingweather.observer.MyObserver;
import com.amaizzzing.amaizingweather.observer.Publisher;
import com.amaizzzing.amaizingweather.observer.PublisherGetter;
import com.amaizzzing.amaizingweather.receivers.BatteryReceiver;
import com.amaizzzing.amaizingweather.receivers.NetworkChangeReceiver;
import com.amaizzzing.amaizingweather.repository.ContextCity;
import com.amaizzzing.amaizingweather.repository.HistoryChooseCitiesRepository;
import com.amaizzzing.amaizingweather.repository.Repository;
import com.amaizzzing.amaizingweather.repository.WeatherRequestRepositoryRoom;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        Constants,
        PublisherGetter,
        MyObserver,
        DialogErrorServerInfo.onUpdateServerInfo,
        MainFragment.OnHistoryClickListener {

    private static final String TAG = "MyDB";
    private static final String CHANNEL_ID = "2";
    private static final String CHANNEL_NAME = "name";
    private static final int PERMISSION_REQUEST_CODE = 10;

    private FirebaseAnalytics mFirebaseAnalytics;

    private BottomNavigationView navView;
    private ProgressBar pbNetworkLoad;
    Toolbar toolbar_MainActivity;

    private SharedPreferences sp;
    private boolean isUpdateTheme;
    public Publisher publisher;
    private Repository repo;
    private WeatherRequestRepositoryRoom weatherRequestRepositoryRoom;
    private CityRoom currentCity;
    private MyFragmentFactory fragmentFactory;

    private CompositeDisposable compositeDisposable;
    private HistoryChooseCitiesRepository historyChooseCitiesRepository;

    private NetworkChangeReceiver networkChangeReceiver;
    private BatteryReceiver batteryReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setupTheme();
        super.onCreate(savedInstanceState);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        initVariables();
        initNotificationChannel();
        registerReceivers();
        getFirebaseToken();
        setContentView(R.layout.activity_main);
        clearFragmentBackStack(false);
        refreshTheme();
        initViews();
        initListeners();
        if (getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
            initToolbar();
            initDrawer();
        }
        createCurrentCity();
    }

    private void getFirebaseToken() {
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                return;
            }
            String token = task.getResult().getToken();
            Log.e(TAG, token);
        });
    }

    private void registerReceivers() {
        this.registerReceiver(networkChangeReceiver,
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        this.registerReceiver(batteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    private void initNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void createCurrentCity() {
        compositeDisposable.add(repo.getLastHistoryCity()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        city -> {
                            currentCity = city;
                            currentCity.setForecast(new ArrayList<>());
                            prepareCurrentCityRoom();
                        }, Throwable::printStackTrace,
                        () -> {
                            historyChooseCitiesRepository.addToHistory(currentCity)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe();
                            prepareCurrentCityNetwork();
                        }));
    }

    private void prepareCurrentCityRoom() {
        compositeDisposable.add(weatherRequestRepositoryRoom.getWeatherForCity(currentCity.getId_city())
                .flatMapObservable(Observable::fromIterable)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(weatherRequestRooms -> currentCity.getForecast().add(MapperWeatherRequest.RoomToWeatherRequest(weatherRequestRooms)), Throwable::printStackTrace,
                        () -> {
                            compositeDisposable.add(AllDataFromServer.updateCityDbFromNetwork(currentCity.getId_city(), currentCity.getName())
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(
                                            weatherRequest -> {
                                            },
                                            Throwable::printStackTrace));
                            prepareMainScreenAfterLoadFromServer();
                        }));
    }

    private void prepareCurrentCityNetwork() {
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
                                        ))),
                        throwable -> showErrorDialog(),
                        () -> {
                            currentCity = CityMapper.CityToRoom(allDataFromServer.getCityToLoadData());
                            historyChooseCitiesRepository.updateLastByCityId(currentCity.getId_city())
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe();
                            prepareMainScreenAfterLoadFromServer();
                        }
                )
        );
    }

    private void showErrorDialog() {
        DialogErrorServerInfo dialogErrorServerInfo = DialogErrorServerInfo.newInstance();
        dialogErrorServerInfo.show(getSupportFragmentManager(), DIALOG_ERROR_SERVER_INFO);
    }

    private void initVariables() {
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        isUpdateTheme = sp.getBoolean(THEME_SETTINGS, false);
        publisher = new Publisher();
        publisher.subscribe(this);
        repo = new ContextCity(this).getRepository();
        historyChooseCitiesRepository = new HistoryChooseCitiesRepository();
        weatherRequestRepositoryRoom = new WeatherRequestRepositoryRoom();
        compositeDisposable = new CompositeDisposable();
        fragmentFactory = new MyFragmentFactory();
        networkChangeReceiver = new NetworkChangeReceiver();
        batteryReceiver = new BatteryReceiver();
        SharedPreferences sp2 = getPreferences(Context.MODE_PRIVATE);
        String cityNameFirst = sp2.getString(CITY_NAME, DEFAULT_CITY);
        currentCity = new CityRoom();
        currentCity.setName(cityNameFirst);
        currentCity.setForecast(new ArrayList<>());
    }

    private void initDrawer() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar_MainActivity, R.string.about_app, R.string.about_email);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view2);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void initToolbar() {
        toolbar_MainActivity = findViewById(R.id.toolbar_MainActivity);
        setSupportActionBar(toolbar_MainActivity);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    public void setupTheme() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setTheme(R.style.AppTheme3);
        } else {
            if (!isUpdateTheme) {
                setTheme(R.style.AppTheme);
            } else {
                setTheme(R.style.AppTheme2);
            }
        }
    }

    public void createMainScreen() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getSupportFragmentManager().popBackStack(MAIN_FRAGMENT, 1);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.flFrag1_MainActivityLand, fragmentFactory.getFragment(FragmentTypes.MAIN_FRAGMENT), MAIN_FRAGMENT)
                    .replace(R.id.flFrag2_MainActivityLand, fragmentFactory.getFragment(FragmentTypes.FRAGMENT_CITIES_CHOOSE), FRAGMENT_CITIES_CHOOSE)
                    .addToBackStack(FRAGMENT_CITIES_CHOOSE)
                    .addToBackStack(MAIN_FRAGMENT)
                    .commit();
        } else {
            if (getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
                pbNetworkLoad.setVisibility(View.GONE);
                navView.setSelectedItemId(R.id.bottom_nav_weather);
            }
            switchFragments(FragmentTypes.MAIN_FRAGMENT, MAIN_FRAGMENT);
        }
    }

    public void createAboutAppDialog() {
        DialogAboutApp dialogAboutApp = DialogAboutApp.newInstance();
        dialogAboutApp.show(getSupportFragmentManager(), DIALOG_ABOUT_APP);
    }

    public void clearFragmentBackStack(boolean goToMainFragment) {
        int count = 1;
        if (goToMainFragment) {
            count = 2;
        }
        FragmentManager fm = getSupportFragmentManager();
        for (int i = 0; i <= fm.getBackStackEntryCount() - count; i++) {
            fm.popBackStack();
        }
    }

    public boolean refreshTheme() {
        boolean notif = sp.getBoolean(THEME_SETTINGS, false);
        if (isUpdateTheme != notif) {
            super.recreate();
            isUpdateTheme = notif;
            return true;
        }
        return false;
    }

    public void switchFragments(FragmentTypes fragmentTypes, String tag) {
        getSupportFragmentManager().popBackStack(tag, 1);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.containerFragment_MainActivity, fragmentFactory.getFragment(fragmentTypes), tag)
                .commit();
    }

    private void initListeners() {
        if (getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
            navView.setOnNavigationItemSelectedListener(item -> {
                if (!refreshTheme()) {
                    switch (item.getItemId()) {
                        case R.id.bottom_nav_weather:
                            switchFragments(FragmentTypes.MAIN_FRAGMENT, MAIN_FRAGMENT);
                            return true;
                        case R.id.bottom_nav_maps:
                            requestPemissions();
                            return true;
                        case R.id.bottom_nav_settings:
                            switchFragments(FragmentTypes.SETTINGS_FRAGMENT, SETTINGS_FRAGMENT);
                            return true;
                    }
                } else {
                    navView.setSelectedItemId(R.id.bottom_nav_weather);
                }
                return false;
            });
        }
    }

    private void requestPemissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            switchFragments(FragmentTypes.FRAGMENT_MAPS, FRAGMENT_MAPS);
        } else {
            requestLocationPermissions();
        }
    }

    private void requestLocationPermissions() {
        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE)) {
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION
                    },
                    PERMISSION_REQUEST_CODE);
        }
    }


    private void initViews() {
        navView = findViewById(R.id.nav_view);
        pbNetworkLoad = findViewById(R.id.pbNetworkLoad);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            navView.setSelectedItemId(R.id.bottom_nav_settings);
            switchFragments(FragmentTypes.SETTINGS_FRAGMENT, SETTINGS_FRAGMENT);
        }
        if (id == R.id.about_app) {
            createAboutAppDialog();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Publisher getPublisher() {
        return publisher;
    }

    @Override
    public void updateCity(String text) {
        clearFragmentBackStack(true);
        SharedPreferences settings = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(CITY_NAME, text);
        editor.apply();
        if (getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
            pbNetworkLoad.setVisibility(View.VISIBLE);
        }
        currentCity = new CityRoom();
        currentCity.setName(text);
        createCurrentCity();
    }

    private void prepareMainScreenAfterLoadFromServer() {
        compositeDisposable.add(repo.getCityByName(currentCity.getName())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        name -> compositeDisposable.add(repo.updateCityIdSunriceSunset(currentCity)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(
                                        count -> createMainScreen(),
                                        throwable -> createMainScreen())),
                        Throwable::printStackTrace,
                        () -> compositeDisposable.add(repo.insert(currentCity)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(insId -> createMainScreen(), Throwable::printStackTrace)
                        ))
        );
    }

    @Override
    public void onBackPressed() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            finish();
        } else {
            int countBackStack = getSupportFragmentManager().getBackStackEntryCount() - 1;
            if (countBackStack >= 0) {
                if (getSupportFragmentManager().getBackStackEntryAt(countBackStack).getName().equals(SETTINGS_FRAGMENT)) {
                    refreshTheme();
                }
                navView.setSelectedItemId(R.id.bottom_nav_weather);
                clearFragmentBackStack(true);
            } else {
                finish();
            }
        }

        super.onBackPressed();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.drawer_nav_weather) {
            switchFragments(FragmentTypes.MAIN_FRAGMENT, MAIN_FRAGMENT);
            navView.setSelectedItemId(R.id.bottom_nav_weather);
        } else if (id == R.id.drawer_nav_settings) {
            switchFragments(FragmentTypes.SETTINGS_FRAGMENT, SETTINGS_FRAGMENT);
            navView.setSelectedItemId(R.id.bottom_nav_settings);
        } else if (id == R.id.drawer_nav_about) {
            createAboutAppDialog();
        } else if (id == R.id.drawer_nav_maps) {
            switchFragments(FragmentTypes.FRAGMENT_MAPS, FRAGMENT_MAPS);
            navView.setSelectedItemId(R.id.bottom_nav_maps);
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void updateInfo() {
        if (getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
            pbNetworkLoad.setVisibility(View.VISIBLE);
        }
        prepareCurrentCityNetwork();
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(networkChangeReceiver);
        compositeDisposable.dispose();
        super.onDestroy();
    }

    @Override
    public void onItemClick() {
        switchFragments(FragmentTypes.FRAGMENT_HISTORY, FRAGMENT_HISTORY);
    }
}
