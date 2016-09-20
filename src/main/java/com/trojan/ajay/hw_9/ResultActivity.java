package com.trojan.ajay.hw_9;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.design.widget.TabLayout;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

import org.json.JSONException;
import org.json.JSONObject;

public class ResultActivity extends AppCompatActivity {
    ViewPager viewPager;
    TabLayout tabLayout;
    Menu globalMenu;
    ShareDialog shareDialog;
    CallbackManager callbackManager;
    Pager adapter;

    JSONObject result;
    boolean faventry;
    StockModel stockModel;

    boolean fav = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        try {
            result = new JSONObject(getIntent().getExtras().getString("result"));
            stockModel = new StockModel(result);
            actionBar.setTitle(stockModel.Name);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);
        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                if(result.getPostId() == null) {
                    Toast.makeText(getApplicationContext(), "Post Cancelled", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "FB Post Successful", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancel() {
                Toast.makeText(getApplicationContext(), "Post Cancelled", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onError(FacebookException error) {
                Toast.makeText(getApplicationContext(), "Post Failed", Toast.LENGTH_SHORT).show();
            }
        });

        tabLayout = (TabLayout) findViewById(R.id.tabs);

        tabLayout.addTab(tabLayout.newTab().setText("Current"));
        tabLayout.addTab(tabLayout.newTab().setText("Historical"));
        tabLayout.addTab(tabLayout.newTab().setText("News"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        viewPager = (ViewPager) findViewById(R.id.pager);
        adapter = new Pager(getSupportFragmentManager(), result);
        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
            @Override
            public void onPageSelected(int position) {
                tabLayout.setScrollPosition(position, 0, true);
                tabLayout.setSelected(true);
            }
            @Override
            public void onPageScrollStateChanged(int state) {}
        });

        //Adding onTabSelectedListener to swipe views
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.result_menu, menu);
        globalMenu = menu;
        SharedPreferences settings = getSharedPreferences(Utils.SHARED_PREF_FAVLIST, 0);
        faventry = settings.getString(stockModel.Symbol, null) == null ? false : true;

        if (faventry) {
            menu.getItem(0).setIcon(android.R.drawable.btn_star_big_on);
        }
        else {
            menu.getItem(0).setIcon(android.R.drawable.btn_star_big_off);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        SharedPreferences favorites = getSharedPreferences(Utils.SHARED_PREF_FAVLIST, 0);
        SharedPreferences.Editor editor = favorites.edit();
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.favorite:
                // Make favorite
                if(!faventry) {
                    String json = Utils.serialize(new FavoriteModel(false, stockModel.Symbol, stockModel.Name,
                            stockModel.LastPrice, stockModel.ChangePercent, stockModel.MarketCap));
                    editor.putString(stockModel.Symbol, json);
                    editor.commit();
                    globalMenu.getItem(0).setIcon(android.R.drawable.btn_star_big_on);
                    faventry = true;
                }
                else {
                    editor.remove(stockModel.Symbol);
                    editor.apply();
                    globalMenu.getItem(0).setIcon(android.R.drawable.btn_star_big_off);
                    faventry = false;
                }
                return true;
            case R.id.fb_share:
                String name = "Current Stock price of " + stockModel.Symbol + " is $" + stockModel.LastPrice;
                Uri picture = Uri.parse("http://chart.finance.yahoo.com/t?s="+ stockModel.Symbol + "&width=150&height=150&lang=en-US");
                String caption= "LAST TRADED PRICE: $"+stockModel.LastPrice+" CHANGE: "+ stockModel.Change;
                String link = "http://dev.markitondemand.com/";
                String description = "Stock Information of "+stockModel.Name+" ("+stockModel.Symbol+")";
                if (ShareDialog.canShow(ShareLinkContent.class)) {
                    ShareLinkContent content = new ShareLinkContent.Builder()
                            .setContentUrl(Uri.parse(link))
                            .setContentTitle(name)
                            .setImageUrl(picture)
                            .setContentDescription(description)
                            .setQuote(caption)
                            .build();
                    shareDialog.show(content, ShareDialog.Mode.FEED);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
