package com.trojan.ajay.hw_9;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Ajay on 5/2/2016.
 */
public class Utils {
    public static String SHARED_PREF_FAVLIST = "FavoritesList";
    public static String SHARED_PREF_AUTOREFRESH = "AutoRefresh";


    public static <T> String serialize(T obj){
        Gson gson = new Gson();
        String j = gson.toJson(obj);
        return j;
    }

    public static <T> T deserialize(String data,  Class<T> classOfT){
        Gson gson = new Gson();
        T obj = gson.fromJson(data, classOfT);
        return obj;
    }

    public static String getString(InputStream stream) throws IOException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        StringBuilder output = new StringBuilder();
        char[] buffer = new char[1000];
        for(;;) {
            int abh = reader.read(buffer, 0, buffer.length);
            if(abh < 0)
                break;
            output.append(buffer, 0, abh);
        }
        return output.toString();
    }

    public static String makeHttpGetRequest(String url){
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection)(new URL(url)).openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();
            String contentAsString = Utils.getString(conn.getInputStream());
            return contentAsString;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String round(double d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Double.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.toString();
    }
}
