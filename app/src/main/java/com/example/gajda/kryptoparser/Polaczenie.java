package com.example.gajda.kryptoparser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

class Polaczenie {

    final static int GET = 1;
    private final static int POST = 2;

    Polaczenie() {

    }

    String nawiazPolaczenie(String urladdress, int requestMethod){

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
        } catch (IOException e) {
            e.printStackTrace();
        }
        return odpowiedz;
    }


}