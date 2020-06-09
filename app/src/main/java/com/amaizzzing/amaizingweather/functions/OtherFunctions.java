package com.amaizzzing.amaizingweather.functions;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

public class OtherFunctions {
    private static final String LIFECYCLE = "LIFE_CYCLE";

    public static int rnd(int min, int max) {
        max -= min;
        return (int) (Math.random() * ++max) + min;
    }

    public static void makeShortToast(String textToast, Context ctx) {
        Toast.makeText(ctx, textToast, Toast.LENGTH_SHORT).show();
        Log.d(LIFECYCLE, textToast);
    }

    public static void makeShortSnackBar(View v, String text) {
        Snackbar.make(v, text, Snackbar.LENGTH_SHORT).show();
    }

    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
