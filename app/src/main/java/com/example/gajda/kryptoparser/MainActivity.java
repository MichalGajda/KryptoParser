package com.example.gajda.kryptoparser;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends ListActivity {


    private static String url = "https://api.coinmarketcap.com/v1/ticker/?limit=10";

    private final static String ID = "id";
    private final static String NAME = "name";
    private final static String SYMBOL = "symbol";
    private final static String RANK = "rank";
    private final static String PRICE = "price_usd";

    private final static String VALUES = "values";
    private final static String X_AXIS = "x";
    private final static String Y_AXIS = "y";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new GetWaluty().execute();
    }

    public class GetWaluty extends AsyncTask<Void, Void, Void> {

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

            listaWalut = ParseJson(jsonString);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if(progressDialog.isShowing()){
                progressDialog.dismiss();
            }

            ListAdapter adapter = new SimpleAdapter(
                    MainActivity.this, listaWalut, R.layout.list_item,
                    new String[]{NAME, RANK, PRICE}, new int[]{R.id.name,
                    R.id.rank, R.id.price});

            setListAdapter(adapter);
        }

    }

    private ArrayList<HashMap<String, String>> ParseJson(String json) {
        if(json != null) {
            try {

                ArrayList<HashMap<String, String>> listaWalut = new ArrayList<HashMap<String, String>>();

                //JSONObject jsonObject = new JSONObject(json);
                JSONArray jsonArray = new JSONArray(json);

                //HashMap<String, String> waluty = new HashMap<>();

                for(int i = 0; i < jsonArray.length(); i++){

                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    String name = jsonObject.getString(NAME);
                    String rank = jsonObject.getString(RANK);
                    String price = jsonObject.getString(PRICE);

                    HashMap<String, String> waluty = new HashMap<>();

                    waluty.put(NAME, name);
                    waluty.put(RANK, rank);
                    waluty.put(PRICE, price);

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

    /*
    private ArrayList<HashMap<String, String>> ParseToGrapth(String json){

        ArrayList<HashMap<String, String>> coordinates = new ArrayList<>();

        if(json != null){
            try{
                JSONObject jsonObject = new JSONObject(json);

                JSONArray jsonArray = jsonObject.getJSONArray(VALUES);

                HashMap<String, String> wartosci = new HashMap<>();

                for(int i = 0; i < jsonArray.length(); i++){

                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                    String x = jsonObject1.getString(X_AXIS);
                    String y = jsonObject1.getString(Y_AXIS);

                    wartosci.put(X_AXIS, x);
                    wartosci.put(Y_AXIS, y);

                }
                return ;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            Log.e("ServiceHandler", "Nie można pobrać danych z podanego url");
            return null;
        }
    }
    */


}
