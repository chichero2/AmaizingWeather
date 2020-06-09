package com.amaizzzing.amaizingweather;

import com.amaizzzing.amaizingweather.functions.EngToRusLabels;
import com.amaizzzing.amaizingweather.mappers.CityMapper;
import com.amaizzzing.amaizingweather.mappers.MapperWeatherRequest;
import com.amaizzzing.amaizingweather.models.City;
import com.amaizzzing.amaizingweather.models.CityRoom;
import com.amaizzzing.amaizingweather.models.GeonamesModel;
import com.amaizzzing.amaizingweather.repository.WeatherRequestRepositoryRoom;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class AllDataFromServer implements Constants {
    private static WeatherDB weatherDB;
    private City cityToLoadData;

    public AllDataFromServer(CityRoom city) {
        cityToLoadData = CityMapper.CityRoomToCity(city);
        weatherDB = MyApplication.getInstance().getDatabase();
    }

    public Observable<WeatherRequest> loadDataFromServer(int countCities) {
        if (MyApplication.isNetwork) {
            return getGeonames(cityToLoadData.getName())
                    .take(countCities)
                    .flatMap(firstGeoname -> {
                        prepareCity(firstGeoname);
                        return getWeatherRetrofit(cityToLoadData, TYPE_RETROFIT_UNITS, BuildConfig.WEATHER_API_KEY);
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
        }
        return Observable.empty();
    }

    private void prepareCity(GeonamesModel firstGeoname) {
        cityToLoadData.setName(firstGeoname.getName());
        cityToLoadData.setCountryName(firstGeoname.getCountryName());
        cityToLoadData.setAdminName(firstGeoname.getAdminName1());
        cityToLoadData.setLat(firstGeoname.getLat());
        cityToLoadData.setLng(firstGeoname.getLng());
    }

    public static Observable<GeonamesModel> getGeonames(String searchText) {
        if (MyApplication.isNetwork) {
            Geonames geonames = MyApplication.getRetrofitInstance(BASE_URL_GEONAMES).create(Geonames.class);
            return geonames.loadGeoNames(searchText, String.valueOf(GEONAMES_COUNT_ROWS), BuildConfig.GEONAMES_API_KEY, LANG_GEONAMES_RESPONSE)
                    .switchMap(geonames1 -> Observable.fromIterable(geonames1.geonames))
                    .distinctUntilChanged(GeonamesModel::getToponymName)
                    .distinctUntilChanged(GeonamesModel::getName);
        }
        return Observable.empty();
    }

    public static Observable<WeatherRequest> getWeatherRetrofit(City city, String units, String keyApi) {
        if (MyApplication.isNetwork) {
            OpenWeather openWeather = MyApplication.getRetrofitInstance(BASE_URL_OPENWEATHER).create(OpenWeather.class);
            return openWeather.loadWeather(String.valueOf(city.getLat()), String.valueOf(city.getLng()), units, keyApi)
                    .flatMap(weatherRequestForecast -> {
                        city.setId(weatherRequestForecast.getCity().getId());
                        city.setForecast(new ArrayList<>());
                        city.setSunrise(weatherRequestForecast.getCity().getSunrise());
                        city.setSunset(weatherRequestForecast.getCity().getSunset());
                        city.setTimezone(weatherRequestForecast.getCity().getTimezone());
                        return Observable.fromIterable(weatherRequestForecast.getWeatherRequestList());
                    })
                    .doOnNext(weatherRequest -> {
                        weatherRequest.setDt(weatherRequest.getDt() * 1000);
                        weatherRequest.setStrategyWeather(EngToRusLabels.toRus(weatherRequest.getWeather()[0].getMain()));
                        weatherRequest.getWeather()[0].setMain(EngToRusLabels.toRus(weatherRequest.getWeather()[0].getMain()));
                        city.getForecast().add(weatherRequest);
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
        }
        return Observable.empty();
    }

    public City getCityToLoadData() {
        return cityToLoadData;
    }

    static Observable<WeatherRequest> updateCityDbFromNetwork(long idCity, String nameCity) {
        WeatherRequestRepositoryRoom weatherRequestRepositoryRoom = new WeatherRequestRepositoryRoom();
        CityRoom tmpCityForUpdate = new CityRoom();
        tmpCityForUpdate.setName(nameCity);
        tmpCityForUpdate.setId_city(idCity);
        AllDataFromServer data = new AllDataFromServer(tmpCityForUpdate);
        return data.loadDataFromServer(1)
                .doOnNext(weatherRequest -> weatherRequestRepositoryRoom.getWeatherByIdCityAndDate(idCity, weatherRequest.getDt())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                weatherRequestRoom -> {
                                },
                                Throwable::printStackTrace,
                                () -> weatherRequestRepositoryRoom.insert(MapperWeatherRequest.WeatherRequestToRoom(idCity, weatherRequest))
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe()
                        )

                )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

    }

    /*метод получился очень страшный, буду дорабатывать, не хватает знаний и опыта в Rx :)*/
    public static void updateCitiesDbFromNetwork() {
        if (weatherDB != null && weatherDB.CityDao() != null) {
            weatherDB.CityDao().getCityList()
                    .flatMapObservable(Observable::fromIterable)
                    .doOnNext(cityRooms -> {
                        AllDataFromServer data = new AllDataFromServer(cityRooms);
                        data.loadDataFromServer(1)
                                .map(weatherRequest -> weatherDB.weatherRequestRoomDao().getWeatherByIdCityAndDate(data.getCityToLoadData().getId(), weatherRequest.getDt())
                                        .doOnError(Throwable::printStackTrace)
                                        .doOnComplete(() -> weatherDB.weatherRequestRoomDao().insert(MapperWeatherRequest.WeatherRequestToRoom(data.getCityToLoadData().getId(), weatherRequest))
                                                .subscribeOn(Schedulers.io())
                                                .subscribe())
                                        .subscribeOn(Schedulers.io())
                                        .subscribe()
                                )
                                .doOnError(Throwable::printStackTrace)
                                .doOnComplete(() -> {
                                    CityRoom tmpCity = CityMapper.CityToRoom(data.getCityToLoadData());
                                    tmpCity.setName(cityRooms.getName());
                                    weatherDB.CityDao().update(tmpCity)
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe();
                                    weatherDB.CityDao().getLastAddedCity()
                                            .doOnSuccess(cityRoom2 -> weatherDB.CityDao().getLastHistoryCity()
                                                    .doOnError(Throwable::printStackTrace)
                                                    .doOnComplete(() -> weatherDB.historyChooseCitiesDao().updateLastByCityId(cityRoom2.getId_city())
                                                            .subscribeOn(Schedulers.io())
                                                            .observeOn(AndroidSchedulers.mainThread())
                                                            .subscribe()).subscribeOn(Schedulers.io())
                                                    .observeOn(AndroidSchedulers.mainThread())
                                                    .subscribe())
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe();
                                })
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe();
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe();
        }
    }
}
