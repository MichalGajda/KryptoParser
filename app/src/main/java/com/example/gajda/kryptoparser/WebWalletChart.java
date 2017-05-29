package com.example.gajda.kryptoparser;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.net.ssl.HttpsURLConnection;

public class WebWalletChart extends AppCompatActivity {

    public static final String KEY_WALLET_VALUES = "values";
    public static final String KEY_X = "x";
    public static final String KEY_Y = "y";

    private final static String formatJson = "https://api.blockchain.info/charts/balance?address=<ADDRESS_HOLDER>&format=json"; //&format=json&timespan=30days
    private final static String ADDRESS_HOLDER = "<ADDRESS_HOLDER>";

    private LineChart lineChart;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_walet_chart);
        Log.d(MainActivity.PSM_Project_log, "onCreate: ");


        Intent intent = getIntent();
        String address = intent.getStringExtra(Wallet.URL_CHART);
        address = address.trim();
        String finalUrl = formatJson.replace(ADDRESS_HOLDER, address);
        finalUrl = finalUrl.trim();
        Log.d("finalUrl: ", finalUrl);

        lineChart = (LineChart) findViewById(R.id.walletAsChart);

        new WebWalletChart.ReadURLTask().execute(finalUrl);
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
                Intent intent = new Intent(this, CalculatorActivity.class);
                startActivity(intent);
                return true;
            case R.id.about:
                Toast.makeText(this, "About creator", Toast.LENGTH_SHORT).show();
                Intent intent1 = new Intent(this, AboutAuthors.class);
                startActivity(intent1);
                return true;
            case R.id.wallet:
                Toast.makeText(this, "Wallet", Toast.LENGTH_SHORT).show();
                Intent intent2 = new Intent(this, Wallet.class);
                startActivity(intent2);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private String dateConverter(Float arg){
        Date date = new Date(arg.longValue()*1000);
        @SuppressWarnings("deprecation")
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM", getResources().getConfiguration().locale);
        return sdf.format(date);
    }

    private void parseJson_blockChainWallet(String json) {
        Log.d(MainActivity.PSM_Project_log, "parseJson_blockChainWallet: " + json);

        final String[] dates;
        ArrayList<Entry> entries = new ArrayList<>();
        float[] x;
        float[] y;

        if(json != null) {
            try {
                JSONObject addressJson = new JSONObject(json);
                JSONArray valuesArray = addressJson.getJSONArray(KEY_WALLET_VALUES);

                int numberOfPoints = valuesArray.length();
                dates = new String[numberOfPoints];
                y = new float[numberOfPoints];
                x = new float[numberOfPoints];

                for(int i = 0; i < numberOfPoints; i++){

                    JSONObject xyObject = valuesArray.getJSONObject(i);

                    float xValue =  Float.parseFloat(xyObject.getString(KEY_X));
                    dates[i] = (dateConverter(xValue));
                    x[i] = xValue;

                    float yValue =  Float.parseFloat(xyObject.getString(KEY_Y));
                    y[i] = yValue;

                }

                int j = 0;
                for (int i = x.length-1; i >=0 ; --i) {
                    entries.add(new Entry(x[i], y[i]));
                }

                LineDataSet dataSet = new LineDataSet(entries, "wallet as chart");
//                dataSet.setDrawCircles(true);
                dataSet.setColor(Color.BLUE);
                dataSet.setValueTextColor(Color.RED);
                dataSet.setValueTextSize(0f);

                LineData lineData = new LineData(dataSet);

                XAxis xAxis = lineChart.getXAxis();
                xAxis.setEnabled(false);

                lineChart.setData(lineData);
                lineChart.invalidate();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Log.d("ServiceHandler", "can't download data from  url");
        }
    }

    private class ReadURLTask extends AsyncTask<String, Void, String> {

        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d(MainActivity.PSM_Project_log, "onPreExecute");
            pd = ProgressDialog.show(WebWalletChart.this, " https://blockchain.info/api ", "Downloading data...");
        }

        @Override
        protected String doInBackground(String... urls) {
            Log.d(MainActivity.PSM_Project_log, "doInBackground + urls: " + urls[0]);
            String response = "";
            try {
                URL url = new URL(urls[0]);
                HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                conn.setConnectTimeout(7500);
                conn.setReadTimeout(7500);
                conn.connect();
                InputStream is = conn.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                String line;
                while((line = br.readLine()) !=  null ) {
                    response += line;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.d(MainActivity.PSM_Project_log, "response: " + response);

            return response;
        }
        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            Log.d(MainActivity.PSM_Project_log, "onPostExecute + response: " + response);

            pd.dismiss();
            parseJson_blockChainWallet(response);
        }
    }
}