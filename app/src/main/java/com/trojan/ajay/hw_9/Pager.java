package com.trojan.ajay.hw_9;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Ajay on 5/2/2016.
 */
public class Pager extends FragmentStatePagerAdapter {
    private JSONObject JsonResult;

    public Pager(FragmentManager fm, JSONObject result) {
        super(fm);
        this.JsonResult = result;
    }

    //Overriding method getItem
    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        try {
            switch (position) {
                case 0:
                    fragment = new Current(this.JsonResult);
                    break;
                case 1:
                    fragment = new Historical(this.JsonResult.getString("Symbol"));
                    break;
                case 2:
                    fragment = new News(this.JsonResult.getString("Symbol"));
                    break;
                default:
                    fragment = null;
                    break;
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        finally {
            return fragment;
        }
    }

    //Overriden method getCount to get the number of tabs
    @Override
    public int getCount() {
        return 3;
    }
}