package com.example.gajda.kryptoparser;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;

public class WaletView extends AppCompatActivity {

    private String url_base = "https://blockchain.info/address/<PLACE_HOLDER>?format=json";

    public static String URL_CHART = "com.example.gajda.kryptoparser.URL_CHART";
    private String url_base_chart = "https://blockchain.info/charts/balance?address=<PLACE_HOLDER>";

    private final String NUMBER_OF_TRANSACTIONS = "n_tx";
    private final String TOTAL_RECIVED= "total_received";
    private final String TOTAL_SENT = "total_sent";
    private final String FINAL_BALANCE = "final_balance";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(MainActivity.PSM_Project_log, "onCreate");
        setContentView(R.layout.activity_walet_view);

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e(MainActivity.PSM_Project_log, "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(MainActivity.PSM_Project_log, "onResume");

        EditText editText = (EditText) findViewById(R.id.address);
        String address = editText.getText().toString();
        if (address.equals("")) {
            set_visibility(View.INVISIBLE);
        } else {
            String address_url = url_base.replace("<PLACE_HOLDER>", address);
            set_visibility(View.VISIBLE);
            new ReadURLTask().execute(address_url);
        }
    }

    public void check_walet (View view) {
        EditText editText = (EditText) findViewById(R.id.address);
        String address = editText.getText().toString();
        if (!address.equals("")) {
            String address_url = url_base.replace("<PLACE_HOLDER>", address);
            set_visibility(View.VISIBLE);
            new ReadURLTask().execute(address_url);
        } else {
            Toast.makeText(this, "pole adresu nie moze byc puste", Toast.LENGTH_SHORT).show();
        }
    }
    private class ReadURLTask extends AsyncTask<String, Void, String> {

        // Hashmap for ListView
        ArrayList<HashMap<String, String>> waletInfo;
        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.e(MainActivity.PSM_Project_log, "onPreExecute");

            pd = ProgressDialog.show(WaletView.this, " https://blockchain.info/api ", "Pobieram dane...");
        }

        @Override
        protected String doInBackground(String... urls) {
            Log.e(MainActivity.PSM_Project_log, "doInBackground + urls: " + urls[0]);

            String response = "";

            try {
                URL url = new URL(urls[0]);
                HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                conn.setConnectTimeout(15000);
                conn.setReadTimeout(15000);
                conn.connect();
                InputStream is = conn.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                String linia = "";
                while((linia = br.readLine()) !=  null ) {
                    response += linia;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println(response);

            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.e(MainActivity.PSM_Project_log, "onPostExecute + s: " + s);

            System.out.println(s);

            pd.dismiss();

            setTextVievs(s);
        }
    }
    private void setTextVievs(String json) {
        if(json != null) {
            try {
                JSONObject full_json = new JSONObject(json);

                String total_recived = full_json.getString(TOTAL_RECIVED);
                String total_sent = full_json.getString(TOTAL_SENT);
                String final_balance = full_json.getString(FINAL_BALANCE);
                String number_of_transactions = full_json.getString(NUMBER_OF_TRANSACTIONS);

                TextView textView = (TextView) findViewById(R.id.total_received);
                textView.setText("total_recived: " + total_recived);

                textView = (TextView) findViewById(R.id.total_sent);
                textView.setText("total_sent: " + total_sent);

                textView = (TextView) findViewById(R.id.final_balance);
                textView.setText("final_balance: " + final_balance + " Bitcoin Satoshi");

                textView = (TextView) findViewById(R.id.number_of_transactions);
                textView.setText("number_of_transactions: " + number_of_transactions);

               // MainActivity.walet_text_views();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Log.e("ServiceHandler", "Nie można pobrać danych z podanego url");
        }
    }

    private void set_visibility(int visibility) {
        TextView textView;
        textView = (TextView) findViewById(R.id.final_balance);
        textView.setVisibility(visibility);
        textView = (TextView) findViewById(R.id.number_of_transactions);
        textView.setVisibility(visibility);
        textView = (TextView) findViewById(R.id.total_sent);
        textView.setVisibility(visibility);
        textView = (TextView) findViewById(R.id.total_received);
        textView.setVisibility(visibility);

//        Button button;
//        button = (Button) findViewById(R.id.show_history_as_chart);
//        button.setVisibility(visibility);
    }
    protected void show_history_as_chart (View view) {
        EditText editText = (EditText) findViewById(R.id.address);
        String address = editText.getText().toString();
        if (!address.equals("")) {
            String address_url = url_base_chart.replace("<PLACE_HOLDER>", address);
            Intent intent = new Intent(this, WebWaletChart.class);
            intent.putExtra(URL_CHART, address_url);
            startActivity(intent);
        } else {
            Toast.makeText(this, "pole adresu nie moze byc puste", Toast.LENGTH_SHORT).show();
        }
    }
}
