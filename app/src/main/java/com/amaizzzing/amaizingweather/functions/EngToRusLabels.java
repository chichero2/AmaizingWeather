package com.amaizzzing.amaizingweather.functions;

public class EngToRusLabels {
    public static String toRus(String engLabel) {
        switch (engLabel) {
            case "Clear":
                return "Ясно";
            case "Clouds":
                return "Облачно";
            case "Fog":
                return "Туман";
            case "Smoke":
                return "Туман";
            case "Haze":
                return "Туман";
            case "Dust":
                return "Пыль";
            case "Squall":
                return "Шквал";
            case "Tornado":
                return "Торнадо";
            case "Rain":
                return "Дождь";
            case "Mist":
                return "Легкий туман";
            case "Thunderstorm":
                return "Гроза";
            case "Drizzle":
                return "Изморозь";
            case "Snow":
                return "Снег";
            default:
                return "Ясно";
        }
    }
}
