package com.amaizzzing.amaizingweather.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.amaizzzing.amaizingweather.R;
import com.amaizzzing.amaizingweather.WebViewActivity;
import com.google.android.material.snackbar.Snackbar;

public class AboutAppFragment extends Fragment {
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_about_app, container, false);
        (v.findViewById(R.id.fabSendEmail_AboutApp)).setOnClickListener(v1 -> Snackbar
                .make(v1, "Отправить e-mail разработчикам?", Snackbar.LENGTH_LONG)
                .setAction("Отправить", v11 -> {
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                            "mailto", getString(R.string.about_email), null));
                    startActivity(Intent.createChooser(emailIntent, "Send email"));
                })
                .show());

        return v;
    }
}
