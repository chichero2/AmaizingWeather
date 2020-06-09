package com.amaizzzing.amaizingweather.dialogs;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.fragment.app.DialogFragment;

import com.amaizzzing.amaizingweather.Constants;
import com.amaizzzing.amaizingweather.R;
import com.amaizzzing.amaizingweather.WebViewActivity;
import com.google.android.material.snackbar.Snackbar;

public class DialogAboutApp extends DialogFragment implements Constants {
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_about_app, container, false);

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        (v.findViewById(R.id.fabSendEmail_AboutApp)).setOnClickListener(v1 -> Snackbar
                .make(v1, R.string.send_email_to_developers, Snackbar.LENGTH_LONG)
                .setAction(R.string.send, v11 -> {
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                            MAILTO, getString(R.string.about_email), null));
                    startActivity(Intent.createChooser(emailIntent, "Send email"));
                })
                .show());
        (v.findViewById(R.id.politicsButton_AboutApp)).setOnClickListener(v12 -> {
            Intent intent = new Intent(requireContext(), WebViewActivity.class);
            startActivityForResult(intent,101);
        });

        return v;
    }

    public static DialogAboutApp newInstance() {
        return new DialogAboutApp();
    }

    @Override
    public void onResume() {
        super.onResume();
        WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = LinearLayout.LayoutParams.MATCH_PARENT;
        params.height = 950;
        getDialog().getWindow().setAttributes(params);
    }
}
