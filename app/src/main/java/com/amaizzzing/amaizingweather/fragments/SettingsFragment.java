package com.amaizzzing.amaizingweather.fragments;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import com.amaizzzing.amaizingweather.R;

public class SettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings, rootKey);
    }
}
