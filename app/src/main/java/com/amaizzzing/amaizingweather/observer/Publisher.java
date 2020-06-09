package com.amaizzzing.amaizingweather.observer;

import java.util.ArrayList;
import java.util.List;

public class Publisher {
    private List<MyObserver> observers;

    public Publisher() {
        observers = new ArrayList<>();
    }

    public void subscribe(MyObserver observer) {
        observers.add(observer);
    }

    public void unsubscribe(MyObserver observer) {
        observers.remove(observer);
    }

    public void notify(String text) {
        if (text.equals("server")) {
            observers.get(0).updateCity(text);
        } else {
            for (MyObserver observer : observers) {
                observer.updateCity(text);
            }
        }
    }
}
