package com.example.gajda.kryptoparser;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/*public class Polaczenie extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_polaczenie);
    }
}*/

public class Polaczenie {

    public final static int GET = 1;
    public final static int POST = 2;

    public Polaczenie() {

    }

    public String nawiazPolaczenie(String urladdress, int requestMethod){

        URL url;
        String odpowiedz = "";
        try {
            url = new URL(urladdress);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(15000);
            connection.setConnectTimeout(15000);
            connection.setDoInput(true);

            if(requestMethod==POST){
                connection.setRequestMethod("POST");
            } else if(requestMethod==GET){
                connection.setRequestMethod("GET");
            }


            int responseCode = connection.getResponseCode();

            if(responseCode == HttpURLConnection.HTTP_OK){
                String message;
                InputStreamReader isr = new InputStreamReader(connection.getInputStream());
                BufferedReader br = new BufferedReader(isr);
                while((message=br.readLine())!= null){
                    odpowiedz += message;
                }
            } else {
                odpowiedz = "";
            }



        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return odpowiedz;
    }


}