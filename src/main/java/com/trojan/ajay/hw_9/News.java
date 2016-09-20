package com.trojan.ajay.hw_9;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ajay on 5/2/2016.
 */
public class News extends Fragment {

    private String symbol;
    ListView list_details;
    ImageView chartImage;
    FeedAdapter feedAdapter;

    public News(){}

    public News(String symbol)
    {
        this.symbol = symbol;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.news, container, false);
        List<FeedItemModel> newsList = new ArrayList<>();
        feedAdapter = new FeedAdapter(getContext(), R.layout.feeditem, newsList);
        list_details = (ListView) view.findViewById(R.id.news_list);

        View headerView = View.inflate(getContext(), R.layout.news_feed_footer, null);
        list_details.addHeaderView(headerView);

        list_details.setAdapter(feedAdapter);
        list_details.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FeedItemModel news = (FeedItemModel) parent.getAdapter().getItem(position);
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(news.Url));
                startActivity(browserIntent);
            }
        });

        GetNewsTask getNews = new GetNewsTask();
        getNews.execute(symbol);
        return view;
    }

    private class GetNewsTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                URL url = new URL("http://ajaythetrojan.us-west-1.elasticbeanstalk.com/newsfeed.php?symb="+params[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                String contentAsString = Utils.getString(urlConnection.getInputStream());
                return contentAsString;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            ArrayList<FeedItemModel> feedsArrayList = new ArrayList<>();
            try {
                JSONObject obj = new JSONObject(result);
                JSONArray feeds = obj.getJSONObject("d").getJSONArray("results");
                for (int i = 0; i < (feeds.length() > 4 ? 4 : feeds.length()); i++) {
                    JSONObject jobj = feeds.getJSONObject(i);
                    feedsArrayList.add(new FeedItemModel(jobj.getString("Title"), jobj.getString("Url"),
                            jobj.getString("Source"), jobj.getString("Description"), jobj.getString("Date")));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (feedsArrayList != null) {
                feedAdapter.clear();
                feedAdapter.addAll(feedsArrayList);
                feedAdapter.notifyDataSetChanged();
            }
        }
    }
}