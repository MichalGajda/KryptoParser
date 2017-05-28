package com.example.gajda.kryptoparser;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

class Polaczenie {

    final static int GET = 1;
    final static int POST = 2;

    Polaczenie() {
    }

    String nawiazPolaczenie(String urladdress, int requestMethod){

        URL url;
        String responseFromServer = "";
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

            Log.d("Tak ", Integer.toString(responseCode));

            if(responseCode == HttpURLConnection.HTTP_OK){
                String message;
                InputStreamReader isr = new InputStreamReader(connection.getInputStream());
                BufferedReader br = new BufferedReader(isr);
                while((message=br.readLine())!= null){
                    responseFromServer += message;
                }
            } else {
                responseFromServer = "";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return responseFromServer;
    }
}