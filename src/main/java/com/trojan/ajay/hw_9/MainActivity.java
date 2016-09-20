package com.trojan.ajay.hw_9;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.text.TextWatcher;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.nhaarman.listviewanimations.itemmanipulation.DynamicListView;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.OnDismissCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private AutoCompleteTextView acTextView;
    ProgressBar progressBar;
    ProgressBar favprogressBar;
    Timer timer;
    SuggestAdapter autoAdapter;
    DynamicListView dynamicLv;
    FavoritesAdapter favAdapter;
    Switch switchButton;
    SharedPreferences autoRefreshSP;
    SharedPreferences favoritesSP;
    List<FavoriteModel> favList;
    boolean isFavActivityRunning = false;

    private final static int INTERVAL = 1000 * 10; //10 seconds
    Handler mHandler = new Handler();
    Runnable mHandlerTask = new Runnable()
    {
        @Override
        public void run() {
        if(!isFavActivityRunning) {
            isFavActivityRunning = true;
            new GetFavoritesPopulateTask().execute();
        }
        mHandler.postDelayed(mHandlerTask, INTERVAL);
        }
    };
    void startRepeatingTask(){
        mHandlerTask.run();
    }
    void stopRepeatingTask(){
        mHandler.removeCallbacks(mHandlerTask);
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
            alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    acTextView.setText("");
                }
            });
            AlertDialog alertDialog;
            switch(msg.what){
                case 0:
                    alertDialogBuilder.setMessage("Please enter Stock Name/Symbol");
                    alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                    break;
                case 1:
                    alertDialogBuilder.setMessage("Invalid Symbol/Name");
                    alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                    break;
                case 2:
                    alertDialogBuilder.setMessage("No Internet Connection");
                    alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                    break;

                case 3:
                    alertDialogBuilder.setMessage("This symbol doesn't have valid data");
                    alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.drawable.ic_startimage);

        autoRefreshSP = getSharedPreferences(Utils.SHARED_PREF_AUTOREFRESH, 0);
        favoritesSP = getSharedPreferences(Utils.SHARED_PREF_FAVLIST, 0);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        favprogressBar = (ProgressBar) findViewById(R.id.favprogressBar);
        favprogressBar.setVisibility(View.GONE);

        acTextView = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
        autoAdapter = new SuggestAdapter(this);
        acTextView.setAdapter(autoAdapter);
        acTextView.setThreshold(3);

        acTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (timer != null) {
                    timer.cancel();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        GetAutoCompleteTask getList = new GetAutoCompleteTask();
                        getList.execute(String.valueOf(acTextView.getText()));
                    }
                }, 600);
            }
        });

        favList = new ArrayList<>();
        favAdapter = new FavoritesAdapter(this, R.layout.search_list, favList);
        dynamicLv = (DynamicListView) findViewById(R.id.dynamic_stock_listview);
        dynamicLv.setAdapter(favAdapter);
        if(!isFavActivityRunning) {
            isFavActivityRunning = true;
            new GetFavoritesPopulateTask().execute();
        }

        dynamicLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                new GETJSONTask().execute(favAdapter.getItem(position).Symbol);
            }
        });

        dynamicLv.enableDragAndDrop();
        dynamicLv.enableSwipeToDismiss(new OnDismissCallback() {
            @Override
            public void onDismiss(@NonNull ViewGroup listView, @NonNull int[] reverseSortedPositions) {
                final int[] array = reverseSortedPositions;

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                for (int position : array) {
                                    SharedPreferences.Editor editor = favoritesSP.edit();
                                    editor.remove(favAdapter.getItem(position).Symbol);
                                    editor.apply();
                                    favAdapter.remove(position);
                                    favAdapter.notifyDataSetChanged();
                                }
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();

            }
        });

        ImageView refreshIcon = (ImageView)findViewById(R.id.img_refresh);
        refreshIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isFavActivityRunning) {
                    isFavActivityRunning = true;
                    new GetFavoritesPopulateTask().execute();
                }
            }
        });


        switchButton = (Switch) findViewById(R.id.autoRefreshSw);
        if(autoRefreshSP.getBoolean("Auto", false)){
            switchButton.setChecked(true);
            startRepeatingTask();
        }
        else {
            switchButton.setChecked(false);
            stopRepeatingTask();
        }

        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            SharedPreferences.Editor editor = autoRefreshSP.edit();
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean bChecked) {
                if (bChecked) {
                    editor.putBoolean("Auto", true);
                    editor.commit();
                    startRepeatingTask();
                } else {
                    editor.putBoolean("Auto", false);
                    editor.commit();
                    stopRepeatingTask();
                }
            }
        });

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        stopRepeatingTask();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateFavorites();
        if(switchButton.isChecked())
            startRepeatingTask();
        /*if(!isFavActivityRunning) {
            isFavActivityRunning = true;
            new GetFavoritesPopulateTask().execute();
        }*/
    }

    private void updateFavorites()
    {
        Map<String, ?> allEntries = favoritesSP.getAll();
        ArrayList<FavoriteModel> favData = new ArrayList<>();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            favData.add(Utils.deserialize(entry.getValue().toString(), FavoriteModel.class));
        }
        if (favData != null) {
            favAdapter.clear();
            favAdapter.addAll(favData);
            favAdapter.notifyDataSetChanged();
        }
    }

    public void clear(View view) {
        acTextView.setText("");
    }

    public void getQuote(View view) {
        String symbol = acTextView.getText().toString();
        if(symbol.isEmpty())
        {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                handler.sendMessage(Message.obtain(handler,0));
                }
            });
        }
        else {
            ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                new GETJSONTask().execute(symbol);
            }
            else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                    handler.sendMessage(Message.obtain(handler,2));
                    }
                });
            }
        }
    }

    private class GetAutoCompleteTask extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... text) {
            String url = "http://ajaythetrojan.us-west-1.elasticbeanstalk.com/autocomplete.php?input=" + text[0];
            ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                return Utils.makeHttpGetRequest(url);
            }
            else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                    handler.sendMessage(Message.obtain(handler,2));
                    }
                });
                return null;
            }
        }

        @Override
        protected void onPreExecute() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressBar.setVisibility(View.VISIBLE);
                }
            });
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressBar.setVisibility(View.GONE);
                }
            });
            if(result != null) {
                List<AutosuggestModel> list = new ArrayList<>();
                try {
                    JSONArray jArray = new JSONArray(result);

                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject jObj = jArray.getJSONObject(i);
                        list.add(new AutosuggestModel(jObj.getString("Name"), jObj.getString("Symbol"), jObj.getString("Exchange")));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                autoAdapter.clear();
                autoAdapter.addAll(list);
                autoAdapter.notifyDataSetChanged();
            }
        }
    }

    private class GetFavoritesPopulateTask extends AsyncTask<String, Void, List<FavoriteModel>> {
        @Override
        protected void onPreExecute() {
            isFavActivityRunning = true;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    favprogressBar.setVisibility(View.VISIBLE);
                }
            });

            super.onPreExecute();
        }

        @Override
        protected List<FavoriteModel> doInBackground(String... params) {
            ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                SharedPreferences.Editor editor = favoritesSP.edit();
                Map<String, ?> allEntries = favoritesSP.getAll();
                ArrayList<FavoriteModel> favData = new ArrayList<>();
                for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                    String data = Utils.makeHttpGetRequest("http://ajaythetrojan.us-west-1.elasticbeanstalk.com/ajax-json.php?symb=" + entry.getKey());
                    StockModel stockModel = null;
                    try {
                        stockModel = new StockModel(new JSONObject(data));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    FavoriteModel fm = new FavoriteModel(false, stockModel.Symbol, stockModel.Name,
                            stockModel.LastPrice, stockModel.ChangePercent, stockModel.MarketCap);
                    favData.add(fm);
                    editor.putString(entry.getKey(), Utils.serialize(fm));
                    editor.commit();
                }
                return favData;
            }
            else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        handler.sendMessage(Message.obtain(handler,2));
                    }
                });
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<FavoriteModel> results) {
            ArrayList<FavoriteModel> favList = new ArrayList<>();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    favprogressBar.setVisibility(View.GONE);
                }
            });
            if(results!=null) {
                favList.addAll(results);
                if (favList != null) {
                    favAdapter.clear();
                    favAdapter.addAll(favList);
                    favAdapter.notifyDataSetChanged();
                }
            }
            isFavActivityRunning = false;
        }
    }

    private class GETJSONTask extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... params) {
            String url = "http://ajaythetrojan.us-west-1.elasticbeanstalk.com/ajax-json.php?symb=";
            url += params[0];
            ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                String res = Utils.makeHttpGetRequest(url);
                if(res.contains("No symbol matches")){
                    handler.sendMessage(Message.obtain(handler,1));
                    return null;
                }
                else if(res.contains("\"Status\":\"Failure")){
                    handler.sendMessage(Message.obtain(handler,3));
                    return null;
                }
                return res;
            }
            else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        handler.sendMessage(Message.obtain(handler, 2));
                    }
                });
                return null;
            }
        }

        @Override
        protected void onPreExecute() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressBar.setVisibility(View.VISIBLE);
                }
            });
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressBar.setVisibility(View.GONE);
                }
            });
            if(result != null) {
                try {
                    if (result != null) {
                        Intent resultActivity = new Intent(getApplicationContext(), ResultActivity.class);
                        resultActivity.putExtra("result", result);
                        startActivity(resultActivity);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
