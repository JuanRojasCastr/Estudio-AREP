package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class MainService {

    public MainService () {}

    private String apiURL = "https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol=$NAME&apikey=WBHE8DSX66S276FV";

    public String getApiURL() {
        return apiURL;
    }

    public String getRequest(String name) {
        String resp = "";
        try {

            URL url = new URL(getApiURL().replace("$NAME", name));

            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));

            String inputLine = null;

            while ((inputLine = reader.readLine()) != null) {
                resp += inputLine;
            }

        } catch (MalformedURLException ex) {
            System.out.println(ex.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return resp;
    }
}
