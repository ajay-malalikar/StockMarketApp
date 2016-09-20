package com.trojan.ajay.hw_9;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Ajay on 4/30/2016.
 */
public class Historical extends Fragment {

    private String symbol;
    WebView webView;

    public Historical() {}
    public Historical(String symbol)
    {
        this.symbol = symbol;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.historical, container, false);
        webView = (WebView)view.findViewById(R.id.highchart);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

        webView.loadUrl("file:///android_asset/highcharts.html");
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient(){
            public void onPageFinished(WebView view, String url){
                webView.loadUrl("javascript:init('" + symbol + "');");
            }
        });
        return view;
    }
}