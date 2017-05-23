package com.example.gajda.kryptoparser;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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

    private String last_checked = "last_checked";
    public static String PSM_Project_log = "PSM_Project_log";

    private static String url = "https://api.coinmarketcap.com/v1/ticker/?limit=10";


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

    private final static String VALUES = "values";
    private final static String X_AXIS = "x";
    private final static String Y_AXIS = "y";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new GetWaluty().execute();
        //listView = (ListView) findViewById(R.id.list)
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
                //calculator();
                return true;
            case R.id.about:
                Toast.makeText(this, "About creator", Toast.LENGTH_SHORT).show();
                Intent intent1 = new Intent(this, AboutAuthors.class);
                startActivity(intent1);
                return true;
            case R.id.wallet:
                Toast.makeText(this, "Wallet", Toast.LENGTH_SHORT).show();
                Intent intent2 = new Intent(this, WaletView.class);
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

        //Toast.makeText(this, tv.getText().toString(), Toast.LENGTH_LONG).show();
    }

    private class GetWaluty extends AsyncTask<Void, Void, Void> {

        ProgressDialog progressDialog;
        ArrayList<HashMap<String, String>> listaWalut;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Proszę czekać...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            Polaczenie polaczenie = new Polaczenie();

            String jsonString = polaczenie.nawiazPolaczenie(url, Polaczenie.GET);

            Log.d("Odpowiedź: ", "> " + jsonString);

            if (!jsonString.equals(""))
                zapisz_do_pliku(jsonString, last_checked);
            else
                jsonString = wczytaj_z_pliku(last_checked);

            listaWalut = parseJson(jsonString);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if(progressDialog.isShowing())
                progressDialog.dismiss();

            ListAdapter adapter = new SimpleAdapter(
                    MainActivity.this, listaWalut, R.layout.list_item,
                    new String[]{NAME, SYMBOL, PRICE, PERCENT_CHANGE_24h, PERCENT_CHANGE_1h, PERCENT_CHANGE_7d},
                    new int[]{R.id.name, R.id.symbol, R.id.price, R.id.percent_change_24h, R.id.percent_change_1h, R.id.percent_change_7d});

            setListAdapter(adapter);
        }

    }

    private ArrayList<HashMap<String, String>> parseJson(String json) {
        if(json != null) {
            try {

                ArrayList<HashMap<String, String>> listaWalut = new ArrayList<>();

                //JSONObject jsonObject = new JSONObject(json);
                JSONArray jsonArray = new JSONArray(json);

                //HashMap<String, String> waluty = new HashMap<>();

                for(int i = 0; i < jsonArray.length(); i++){

                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    String name = jsonObject.getString(NAME);
                    String rank = jsonObject.getString(RANK);
                    String symbol = jsonObject.getString(SYMBOL);
                    String price = jsonObject.getString(PRICE);
                    String price_change_24h = jsonObject.getString(PERCENT_CHANGE_24h);
                    String price_change_1h = jsonObject.getString(PERCENT_CHANGE_1h);
                    String price_change_7d = jsonObject.getString(PERCENT_CHANGE_7d);

                    HashMap<String, String> waluty = new HashMap<>();

                    waluty.put(NAME, name);
                    waluty.put(RANK, "Rank: " + rank);
                    waluty.put(SYMBOL, "" + symbol);
                    waluty.put(PRICE, "Price per 1 unit: $" + price);
                    waluty.put(PERCENT_CHANGE_24h,"24h: " + price_change_24h + "%");
                    waluty.put(PERCENT_CHANGE_1h, "1h: "+ price_change_1h + "%");
                    waluty.put(PERCENT_CHANGE_7d, "7d: "+ price_change_7d + "%");


                    listaWalut.add(waluty);
                }
                return listaWalut;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            Log.e("ServiceHandler", "Nie można pobrać danych z podanego url");
            return null;
        }
    }


    public void zapisz_do_pliku (String raw_jason, String file_name) {
        Log.e(PSM_Project_log, "zapisz_do_pliku + raw_json: " + raw_jason);
        try {
            FileOutputStream fileOutputStream = openFileOutput(file_name, Context.MODE_PRIVATE);
            fileOutputStream.write(raw_jason.getBytes());
            fileOutputStream.close();
            System.out.println("zapisano do pliku");
            Log.e(PSM_Project_log,"zapisano do pliku");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String wczytaj_z_pliku (String file_name) {
        Log.e(PSM_Project_log, "wczytaj_z_pliku");
        StringBuilder stringBuilder = new StringBuilder();
        try {
            FileInputStream fileInputStream = openFileInput(file_name);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String message;
            while ( ( message = bufferedReader.readLine()) != null ) {
                stringBuilder.append(message);
            }

//            bufferedReader.close();
//            inputStreamReader.close();
//            fileInputStream.close();

            System.out.println("Wczytano z pliku");
            Log.e(PSM_Project_log,"Wczytano z pliku");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }


//    public void calculator(){
//
//        Intent intent = new Intent(this, CalculatorActivity.class);
//        startActivity(intent);
//    }

    public void go_to_walet (View view) {
        Intent intent = new Intent(this, WaletView.class);
        startActivity(intent);
    }
}
