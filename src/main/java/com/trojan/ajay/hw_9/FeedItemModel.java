package com.trojan.ajay.hw_9;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by Ajay on 5/2/2016.
 */
public class FeedItemModel {
    public String Title;
    public String Url;
    public String Source;
    public String Description;
    public String Date;

    public FeedItemModel(String title, String url, String source, String description, String date)
    {
        this.Title = title;
        this.Url = url;
        this.Source = source;
        this.Description = description;
        SimpleDateFormat fromUser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        SimpleDateFormat myFormat = new SimpleDateFormat("dd MMMM yyyy, HH:mm:ss");
        try {
            this.Date =  myFormat.format(fromUser.parse(date)) + " EST";
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //this.Date = date;
    }
}
