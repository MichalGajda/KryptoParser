package com.example.gajda.kryptoparser;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class WebWaletChart extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_walet_chart);

        WebView webView = (WebView) findViewById(R.id.walet_balance_chart);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        Intent intent = getIntent();
        webView.loadUrl(intent.getStringExtra(WaletView.URL_CHART));
    }
}
