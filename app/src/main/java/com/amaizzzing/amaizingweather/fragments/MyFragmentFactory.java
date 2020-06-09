package com.amaizzzing.amaizingweather.fragments;

import androidx.fragment.app.Fragment;

import com.amaizzzing.amaizingweather.Constants;

public class MyFragmentFactory implements Constants {
    public Fragment getFragment(FragmentTypes type) {
        Fragment result = null;
        switch (type) {
            case MAIN_FRAGMENT:
                result = new MainFragment();
                break;
            case FRAGMENT_CITIES_CHOOSE:
                result = new FragmentCitiesChoose();
                break;
            case FRAGMENT_HISTORY:
                result = new FragmentHistoryFilter();
                break;
            case SETTINGS_FRAGMENT:
                result = new SettingsFragment();
                break;
            case FRAGMENT_MAPS:
                result = new FragmentMaps();
                break;
        }
        return result;
    }
}
