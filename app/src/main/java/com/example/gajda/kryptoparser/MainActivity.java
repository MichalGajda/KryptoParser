package com.example.gajda.kryptoparser;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends ListActivity {

    private final String LIST_FILE_NAME = "LIST_FILE";
    public static String PSM_Project_log = "PSM_Project_log";

    private static final String basic_url = "https://api.coinmarketcap.com/v1/ticker/?limit=<LIMIT_HOLDER>";
    private static final String LIMIT_HOLDER = "<LIMIT_HOLDER>";

    private final static String ID = "id";
    private final static String NAME = "name";
    private final static String SYMBOL = "symbol";
    private final static String RANK = "rank";
    private final static String PRICE = "price_usd";
    private final static String MARKET_CAP_USD = "market_cap_usd";
    private final static String PERCENT_CHANGE_1h = "percent_change_1h";
    private final static String PERCENT_CHANGE_7d = "percent_change_7d";
    private final static String PERCENT_CHANGE_24h = "percent_change_24h";
    public final static String CURRENCY_SIGN = "currency_sign";

    public static final String LIST_PREFFERENCES = "com.example.gajda.kryptoparser.LIST_PREFERENCES";
    public static final String KEY_PREFERENCES_LIST_LIMIT = "com.example.gajda.kryptoparser.LIST_LIMIT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new GetCurrencies().execute();
    }
    protected void refresh (View view) {
        new GetCurrencies().execute();
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

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent intent = new Intent(this, CurrencyChartView.class);
        TextView tv = (TextView) v.findViewById(R.id.symbol);
        intent.putExtra(CURRENCY_SIGN, tv.getText().toString());
        startActivity(intent);
    }
    public void changeListLimit (View view) {
        Intent intent = new Intent(this, ChangingListLimit.class);
        startActivity(intent);
    }
    private class GetCurrencies extends AsyncTask<Void, Void, String> {

        ProgressDialog progressDialog;
        ArrayList<HashMap<String, String>> listaWalut;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Please wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {

            Polaczenie polaczenie = new Polaczenie();
            SharedPreferences preferences = getSharedPreferences(MainActivity.LIST_PREFFERENCES, Context.MODE_PRIVATE);
            String listLimit = String.valueOf(preferences.getInt(MainActivity.KEY_PREFERENCES_LIST_LIMIT, 25));
            String finalUrl = basic_url.replace(LIMIT_HOLDER, listLimit);
            String response = polaczenie.nawiazPolaczenie(finalUrl, Polaczenie.GET);

            Log.d("Response: ", "> " + response);

            return response;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);

            if(progressDialog.isShowing())
                progressDialog.dismiss();

            if (!response.isEmpty()) {
                saveToFile(response, LIST_FILE_NAME);
            } else {
                if(progressDialog.isShowing())
                    progressDialog.dismiss();
                response = loadFile(LIST_FILE_NAME);
                Toast.makeText(MainActivity.this, "no internet connection, loading last downloaded data", Toast.LENGTH_LONG).show();
            }

            listaWalut = parseJson_coinmarketcap(response);
            ListAdapter adapter = new SimpleAdapter(
                    MainActivity.this, listaWalut, R.layout.list_item,
                    new String[]{NAME, SYMBOL, PRICE, PERCENT_CHANGE_24h, PERCENT_CHANGE_1h, PERCENT_CHANGE_7d},
                    new int[]{R.id.name, R.id.symbol, R.id.price, R.id.percent_change_24h, R.id.percent_change_1h, R.id.percent_change_7d});
            setListAdapter(adapter);
        }
    }

    private ArrayList<HashMap<String, String>> parseJson_coinmarketcap(String json) {
        if(json != null) {
            try {

                ArrayList<HashMap<String, String>> currencyList = new ArrayList<>();
                JSONArray curencyArray = new JSONArray(json);

                for(int i = 0; i < curencyArray.length(); i++){

                    JSONObject currencyObject = curencyArray.getJSONObject(i);

                    String name = currencyObject.getString(NAME);
                    String rank = currencyObject.getString(RANK);
                    String symbol = currencyObject.getString(SYMBOL);
                    String price = currencyObject.getString(PRICE);
                    String price_change_24h = currencyObject.getString(PERCENT_CHANGE_24h);
                    String price_change_1h = currencyObject.getString(PERCENT_CHANGE_1h);
                    String price_change_7d = currencyObject.getString(PERCENT_CHANGE_7d);

                    HashMap<String, String> waluty = new HashMap<>();

                    waluty.put(NAME, name);
                    waluty.put(RANK, "Rank: " + rank);
                    waluty.put(SYMBOL, "" + symbol);
                    waluty.put(PRICE, "Price per 1 unit: $" + price);
                    waluty.put(PERCENT_CHANGE_24h,"24h: " + price_change_24h + "%");
                    waluty.put(PERCENT_CHANGE_1h, "1h: "+ price_change_1h + "%");
                    waluty.put(PERCENT_CHANGE_7d, "7d: "+ price_change_7d + "%");

                    currencyList.add(waluty);
                }
                return currencyList;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            Log.d("ServiceHandler", "Nie można pobrać danych z podanego basic_url");
            return null;
        }
    }

    public void saveToFile(String raw_jason, String file_name) {
        Log.d(PSM_Project_log, "save to file + raw_json: " + raw_jason);
        try {
            FileOutputStream fileOutputStream = openFileOutput(file_name, Context.MODE_PRIVATE);
            fileOutputStream.write(raw_jason.getBytes());
            fileOutputStream.close();
            System.out.println("file saved");
            Log.d(PSM_Project_log,"file saved");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String loadFile(String file_name) {
        Log.d(PSM_Project_log, "loadFile");
        StringBuilder stringBuilder = new StringBuilder();
        try {
            FileInputStream fileInputStream = openFileInput(file_name);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String message;
            while ( ( message = bufferedReader.readLine()) != null ) {
                stringBuilder.append(message);
            }

            Log.d(PSM_Project_log,"file loaded");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return stringBuilder.toString();
    }
}
