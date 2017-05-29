package com.example.gajda.kryptoparser;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class Wallet extends AppCompatActivity {

    private String url_base = "https://blockchain.info/address/<PLACE_HOLDER>?format=json";
    private final String PLACE_HOLDER = "<PLACE_HOLDER>";

    private final String WALLET_FILE_NAME = "WALLET_FILE";

    public static final String URL_CHART = "com.example.gajda.kryptoparser.URL_CHART";
    private final String url_base_chart = "https://blockchain.info/charts/balance?address=<PLACE_HOLDER>";

    private final String KEY_NUMBER_OF_TRANSACTIONS = "n_tx";
    private final String KEY_TOTAL_RECEIVED = "total_received";
    private final String KEY_TOTAL_SENT = "total_sent";
    private final String KEY_FINAL_BALANCE = "final_balance";
    private final String KEY_ADDRESS = "address";

    private String address_input = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(MainActivity.PSM_Project_log, "onCreate");
        setContentView(R.layout.activity_wallet);
        Log.d(MainActivity.PSM_Project_log, "po setContentView");

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(MainActivity.PSM_Project_log, "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(MainActivity.PSM_Project_log, "onResume");
        if (!address_input.isEmpty()) {
            String address_url = url_base.replace(PLACE_HOLDER, address_input);
            new ReadURLTask().execute(address_url);
            setContentView(R.layout.display_wallet);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater =  getMenuInflater();
        menuInflater.inflate(R.menu.menu_w, menu);
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public void checkOtherAdress (View view) {
        setContentView(R.layout.activity_wallet);
    }

    public void checkWallet(View view) {
        EditText editText = (EditText) findViewById(R.id.address_input);
        String tmpAddress = editText.getText().toString();
        if (!tmpAddress.isEmpty()) {
            address_input = tmpAddress;
            String address_url = url_base.replace(PLACE_HOLDER, address_input);
            new ReadURLTask().execute(address_url);
            setContentView(R.layout.display_wallet);
        } else {
            Toast.makeText(this, "address field can't be empty", Toast.LENGTH_SHORT).show();
        }
    }
    public void saveCurrentAddress (View view) {
        saveToFile(address_input, WALLET_FILE_NAME);
    }
    public void reset (View view) {
        Log.d(MainActivity.PSM_Project_log, "reset");
        try {
            FileOutputStream fileOutputStream = openFileOutput(WALLET_FILE_NAME, Context.MODE_PRIVATE);
            fileOutputStream.write(("").getBytes());
            fileOutputStream.close();
            Log.d(MainActivity.PSM_Project_log,"reset done");
            Toast.makeText(Wallet.this, "reset done", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
        EditText editText = (EditText) findViewById(R.id.oldAdresses);
        editText.setText("");
    }
    public void saveToFile (String toSave, String file_name) {
        Log.d(MainActivity.PSM_Project_log, "save to file + toSave: " + toSave);
        try {
            String alreadySaved = loadFile(WALLET_FILE_NAME);
            if (!alreadySaved.contains(toSave)) {
                FileOutputStream fileOutputStream = openFileOutput(file_name, Context.MODE_PRIVATE);
                fileOutputStream.write((alreadySaved + "\n " + toSave).getBytes());
                fileOutputStream.close();
                Log.d(MainActivity.PSM_Project_log,"file saved");
                Toast.makeText(Wallet.this, "address saved", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(Wallet.this, "address already saved", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String loadFile (String file_name) {
        Log.d(MainActivity.PSM_Project_log, "loadFile");
        StringBuilder stringBuilder = new StringBuilder();
        try {
            FileInputStream fileInputStream = openFileInput(file_name);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String message;
            while ( ( message = bufferedReader.readLine()) != null ) {
                stringBuilder.append(message);
            }

            Log.d(MainActivity.PSM_Project_log,"file loaded");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }
    public void loadOldAdresses (View view) {
        EditText editText = (EditText) findViewById(R.id.oldAdresses);
        String loadedFile = loadFile(WALLET_FILE_NAME);

        if (loadedFile.isEmpty())
            loadedFile = "";

        editText.setText(loadedFile);
    }
    private class ReadURLTask extends AsyncTask<String, Void, String> {

        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.e(MainActivity.PSM_Project_log, "onPreExecute");

            pd = ProgressDialog.show(Wallet.this, " https://blockchain.info/api ", "Downloading data...");
        }

        @Override
        protected String doInBackground(String... urls) {
            Log.d(MainActivity.PSM_Project_log, "doInBackground + urls: " + urls[0]);

            String response = "";

            // for wallet balance i don't need full web response, just first 7 lines so i created limit
            final int lineLimit = 7;
            int lineCounter = 0;

            try {
                URL url = new URL(urls[0]);
                HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                conn.setConnectTimeout(7500);
                conn.setReadTimeout(7500);
                conn.connect();
                InputStream is = conn.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                String linia;
                while((linia = br.readLine()) !=  null && lineCounter != lineLimit) {
                    response += linia;
                    ++lineCounter;
                }
                // after putting on limit the json response is not correct object so i need to fix it here
                if (response.endsWith(",")) {
                    response = response.substring(0,response.length()-1);
                    response += "}";
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

            setTextVievs(response);
        }
    }
    private void setTextVievs(String json) {
        if(json != null) {
            try {
                JSONObject full_json = new JSONObject(json);

                String total_recived = full_json.getString(KEY_TOTAL_RECEIVED);
                String total_sent = full_json.getString(KEY_TOTAL_SENT);
                String final_balance = full_json.getString(KEY_FINAL_BALANCE);
                String number_of_transactions = full_json.getString(KEY_NUMBER_OF_TRANSACTIONS);
                String addres_view = full_json.getString(KEY_ADDRESS);

                double satoshisToBTC;

                TextView textView = (TextView) findViewById(R.id.total_received);
                satoshisToBTC = satoshisToBitcoin(total_recived);
                textView.setText(getString(R.string.total_received_btc) + satoshisToBTC);

                textView = (TextView) findViewById(R.id.total_sent);
                satoshisToBTC = satoshisToBitcoin(total_sent);
                textView.setText(getString(R.string.total_sent_btc) + satoshisToBTC);

                textView = (TextView) findViewById(R.id.final_balance);
                satoshisToBTC = satoshisToBitcoin(final_balance);
                textView.setText(getString(R.string.final_balance_btc) + satoshisToBTC + " Bitcoin");

                textView = (TextView) findViewById(R.id.number_of_transactions);
                textView.setText(getString(R.string.number_of_transactions) + number_of_transactions);

                textView = (TextView) findViewById(R.id.address_view);
                textView.setText(getString(R.string.address) + addres_view);

            } catch (JSONException e) {
                //e.printStackTrace();
                Toast.makeText(Wallet.this, R.string.addres_warning, Toast.LENGTH_LONG).show();
                setContentView(R.layout.activity_wallet);
            }
        } else {
            Log.d("ServiceHandler", "Sorry, cannot download data from this url");
        }
    }
    private double satoshisToBitcoin (String satoshis) {
        return Double.parseDouble(satoshis) / Math.pow(10,8);
    }

    public void showHistoryAsChart(View view) {
            Intent intent = new Intent(this, WebWaletChart.class);
            intent.putExtra(URL_CHART, address_input);
            startActivity(intent);
    }
}
