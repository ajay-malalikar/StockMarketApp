package com.trojan.ajay.hw_9;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ListView;
import android.view.View.OnClickListener;
import android.widget.TableRow;

import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ajay on 5/2/2016.
 */

public class Current extends Fragment {

    ListView list_details;
    ImageView chartImage;
    StockDetailsAdapter detailsAdapter;
    JSONObject result;

    public Current(){}

    public Current(JSONObject result){
        this.result = result;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.current, container, false);
        list_details = (ListView) view.findViewById(R.id.stockdetails);
        List<StockDetails> list = new ArrayList<>();
        StockModel obj = new StockModel(this.result);

        View headerView = View.inflate(getContext(), R.layout.detail_list_header, null);
        list_details.addHeaderView(headerView);
        View footerView = View.inflate(getContext(), R.layout.detail_list_footer, null);
        list_details.addFooterView(footerView);

        chartImage = (ImageView) view.findViewById(R.id.img_current_chart);

        list.add(new StockDetails("NAME", obj.Name));
        list.add(new StockDetails("SYMBOL", obj.Symbol));
        list.add(new StockDetails("LASTPRICE", obj.LastPrice));
        list.add(new StockDetails("CHANGE", obj.Change, obj.ChangeSign));
        list.add(new StockDetails("TIMESTAMP", obj.Timestamp));
        list.add(new StockDetails("MARKETCAP", obj.MarketCap));
        list.add(new StockDetails("VOLUME", obj.Volume));
        list.add(new StockDetails("CHANGEYTD", obj.ChangeYTD, obj.ChangeYTDSign));
        list.add(new StockDetails("HIGH", obj.High));
        list.add(new StockDetails("LOW", obj.Low));
        list.add(new StockDetails("OPEN", obj.Open));

        detailsAdapter = new StockDetailsAdapter(getActivity().getApplicationContext(), R.layout.detail_list_row, list);
        list_details.setAdapter(detailsAdapter);
        final String URL = "http://chart.finance.yahoo.com/t?s=" + obj.Symbol + "&width=1024&height=768&lang=en-US";
        new ImageLoadTask(URL, chartImage).execute();

        chartImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog chartDialog = new Dialog(getContext(), android.R.style.Theme);
                chartDialog.setCancelable(true);
                WebView wv = new WebView(getContext());
                wv.setLayoutParams(new TableRow.LayoutParams(android.widget.TableRow.LayoutParams.MATCH_PARENT, android.widget.TableRow.LayoutParams.MATCH_PARENT));
                wv.loadUrl(URL);
                wv.getSettings().setBuiltInZoomControls(true);
                wv.getSettings().setSupportZoom(true);
                wv.getSettings().setLoadWithOverviewMode(true);
                wv.getSettings().setUseWideViewPort(true);
                chartDialog.setContentView(wv);
                chartDialog.show();
                Window window = chartDialog.getWindow();
                window.setLayout(1000, 900);
            }
        });
        return view;
    }

    public class ImageLoadTask extends AsyncTask<Void, Void, Bitmap> {

        private String url;
        private ImageView imageView;

        public ImageLoadTask(String url, ImageView imageView) {
            this.url = url;
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            try {
                URL urlConnection = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) urlConnection
                        .openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                return myBitmap;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            imageView.setImageBitmap(result);
        }
    }
}