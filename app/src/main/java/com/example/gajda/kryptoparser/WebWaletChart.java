package com.example.gajda.kryptoparser;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

public class WebWaletChart extends AppCompatActivity {

    private final static String formatJson = "https://api.blockchain.info/charts/balance?address=<ADDRESS_HOLDER>&format=json";
    private final static String ADDRESS_HOLDER = "<ADDRESS_HOLDER>";

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_walet_chart);

        Intent intent = getIntent();
        String address = intent.getStringExtra(Wallet.URL_CHART);
        String finalUrl = formatJson.replace(ADDRESS_HOLDER, address);
        Log.d("finalUrl: ", finalUrl);

    }



    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        MenuInflater menuInflater =  getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.calculator:
                Toast.makeText(this, "Calculator", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.about:
                Toast.makeText(this, "About creator", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
