package com.amaizzzing.amaizingweather;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        WebView webView_WebActivity = findViewById(R.id.webView_WebFragment);
        webView_WebActivity.setWebViewClient(new WebViewClient());
        webView_WebActivity.loadUrl("https://github.com/amaizzzing/AmaizingWeather/blob/master/README.md");
    }
}
