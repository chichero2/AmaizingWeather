package com.amaizzzing.amaizingweather;

public interface Constants {
    String THEME_SETTINGS = "theme_settings";
    String MAIN_FRAGMENT = "mainFragment";
    String FRAGMENT_CITIES_CHOOSE = "fragmentCitiesChoose";
    String FRAGMENT_HISTORY = "FRAGMENT_HISTORY";
    String SETTINGS_FRAGMENT = "settingsFragment";
    String FRAGMENT_MAPS="FRAGMENT_MAPS";
    String WEATHER_URL = "https://api.openweathermap.org/data/2.5/forecast?id=%s&units=metric&appid=";
    String MAILTO = "mailto";
    String DIALOG_ABOUT_APP = "dialogAboutApp";
    String DIALOG_ERROR_SERVER_INFO = "dialogErrorServerInfo";
    String DIALOG_HOURLY_FORECAST = "dialogHourlyForecast";
    String DIALOG_SHOW_WEATHER_FROM_MAP="dialogShowWeatherFromMap";
    String DIALOG_LOAD_DATA="dialogLoadData";
    String GET = "GET";
    String GMT = "GMT";
    String HH_MM = "HH:mm";
    String DD_MM_YY = "dd.MM.yy";
    String CITY_NAME = "cityName";
    String DEFAULT_CITY = "Москва";
    String BASE_URL_GEONAMES = "http://api.geonames.org/";
    int GEONAMES_COUNT_ROWS = 10;
    String LANG_GEONAMES_RESPONSE = "ru";
    String BASE_URL_OPENWEATHER = "https://api.openweathermap.org/";
    String TYPE_RETROFIT_UNITS = "metric";
    String NAME_WEATHER_DB = "weatherdb";
    String TITLE_NETWORK_NOTIFICATION = "Amaizzzing weather";
    String TEXT_NETWORK_NOTIFICATION = "Нет сети!";
    String LOW_LEVEL_BATTERY_TEXT_NOTIFICATION = "Низкий уровень заряда!";
    String LAT="lat";
    String LNG="lng";

    enum FragmentTypes {
        MAIN_FRAGMENT,
        FRAGMENT_CITIES_CHOOSE,
        FRAGMENT_HISTORY,
        SETTINGS_FRAGMENT,
        FRAGMENT_MAPS
    }
}
