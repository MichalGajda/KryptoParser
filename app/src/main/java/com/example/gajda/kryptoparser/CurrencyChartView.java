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
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.CandleStickChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.example.gajda.kryptoparser.MainActivity.PSM_Project_log;

public class CurrencyChartView extends AppCompatActivity {

    private static String url = "https://poloniex.com/public?command=returnChartData&currencyPair=BTC_<SYMBOL_HOLDER>&start=<TIME_HOLDER>&end=9999999999&period=86400";
    private static String currenyChartURL;

    private final static String DATE = "date";
    private final static String HIGH = "high";
    private final static String LOW = "low";
    private final static String OPEN = "open";
    private final static String CLOSE = "close";

    CandleStickChart candleStickChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency_chart_view);

        Intent intent = getIntent();
        long day = 86400;
        long month = System.currentTimeMillis()/1000-21*day;
        String currenySymbol = intent.getStringExtra(MainActivity.CURRENCY_SIGN);
        TextView tv = (TextView) findViewById(R.id.tvSymbol);
        tv.setText(currenySymbol);
        currenyChartURL = url.replace("<SYMBOL_HOLDER>", currenySymbol);
        currenyChartURL = currenyChartURL.replace("<TIME_HOLDER>", String.valueOf(month));

        Log.d(PSM_Project_log, "URL: " + currenyChartURL);

        candleStickChart = (CandleStickChart) findViewById(R.id.wykres);
        new GetData().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class GetData extends AsyncTask<Void, Void, Void> {

        ArrayList<Float[]> listaDanych;
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            candleStickChart.setVisibility(View.INVISIBLE);

            progressDialog = new ProgressDialog(CurrencyChartView.this);
            progressDialog.setMessage(getString(R.string.waitMessage));
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            Polaczenie polaczenie = new Polaczenie();

            String jsonData = polaczenie.nawiazPolaczenie(currenyChartURL, Polaczenie.GET);

            Log.d("Response: ", "> " + jsonData);

            listaDanych = parseJson(jsonData);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if(progressDialog.isShowing()){
                progressDialog.dismiss();
            }

            ArrayList<CandleEntry> candleEntries = new ArrayList<>();

            try{
            Float[] timeStamp = listaDanych.get(0);
            Float[] high = listaDanych.get(1);
            Float[] low = listaDanych.get(2);
            Float[] open = listaDanych.get(3);
            Float[] close = listaDanych.get(4);
            int getLast = timeStamp.length - 1;

            for(int i = 0; i < timeStamp.length; i++){
                candleEntries.add(new CandleEntry(i,high[i],low[i],open[i],close[i]));
            }

            CandleDataSet dataSet = new CandleDataSet(candleEntries, "Data of cryptocurrency");

            TextView textView = (TextView) findViewById(R.id.tvHigh);
            //String s =  String.format("%d",high[getLast]);
            textView.setText(getString(R.string.highPrice) + high[getLast].toString());
            textView = (TextView) findViewById(R.id.tvLow);
            textView.setText(getString(R.string.lowPrice) + low[getLast].toString());
            textView = (TextView) findViewById(R.id.tvOpen);
            textView.setText(getString(R.string.openPrice) + open[getLast].toString());
            textView = (TextView) findViewById(R.id.tvClose);
            textView.setText(getString(R.string.closePrice) + close[getLast].toString());

            for(int i = 0; i < timeStamp.length; i++){
                Log.d("Time stamp: " + i, timeStamp[i].toString());
            }

            final String[] etykiety = new String[timeStamp.length];
            for(int i = 0; i < timeStamp.length; i++){
                etykiety[i] = dateConverter(timeStamp[i]);
                Log.d("Etykieta numer: " + i, etykiety[i]);
            }

            IAxisValueFormatter formatter = new IAxisValueFormatter() {
                @Override
                public String getFormattedValue(float value, AxisBase axis) {
                    return etykiety[(int) value];
                }
            };

            XAxis xAxis = candleStickChart.getXAxis();
            xAxis.setValueFormatter(formatter);

            dataSet.setValueTextSize(8f);
            dataSet.setShadowColorSameAsCandle(true);
            dataSet.setIncreasingColor(Color.RED);
            dataSet.setDecreasingColor(R.color.blue);

            CandleData data = new CandleData(dataSet);
            candleStickChart.setData(data);
            candleStickChart.setVisibility(View.VISIBLE);

            } catch (NullPointerException e) {
                Toast.makeText(CurrencyChartView.this, R.string.noCurrencyException, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(CurrencyChartView.this,MainActivity.class);
                startActivity(intent);
            }
        }

        private ArrayList<Float[]> parseJson(String json){

            if(json != null) {
                try{
                    ArrayList<Float[]> entriesList = new ArrayList<>();

                    JSONArray jsonArray = new JSONArray(json);
                    int size = jsonArray.length();
                    Float date[] = new Float[size];
                    Float high[] = new Float[size];
                    Float low[] = new Float[size];
                    Float open[] = new Float[size];
                    Float close[] = new Float[size];

                    for(int i = 0; i < jsonArray.length(); i++){

                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        date[i] = Float.parseFloat(jsonObject.getString(DATE));
                        high[i] = Float.parseFloat(jsonObject.getString(HIGH));
                        low[i] = Float.parseFloat(jsonObject.getString(LOW));
                        open[i] = Float.parseFloat(jsonObject.getString(OPEN));
                        close[i] = Float.parseFloat(jsonObject.getString(CLOSE));

                        entriesList.add(date);
                        entriesList.add(high);
                        entriesList.add(low);
                        entriesList.add(open);
                        entriesList.add(close);

                    }
                    return entriesList;
                } catch (JSONException e) {
                    e.printStackTrace();
                    return null;
                }
            } else {
                return null;
            }
        }

        private String dateConverter(Float arg){
            Date date = new Date(arg.longValue()*1000);
            @SuppressWarnings("deprecation")
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM", getResources().getConfiguration().locale);
            return sdf.format(date);
        }
    }
}