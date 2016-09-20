package com.trojan.ajay.hw_9;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by Ajay on 5/2/2016.
 */
public class StockModel {
    public String Name;
    public String Symbol;
    public String LastPrice;
    public String Change;
    public String ChangePercent;
    public boolean ChangeSign;
    public String Timestamp;
    public String MarketCap;
    public String Volume;
    public String ChangeYTD;
    public boolean ChangeYTDSign;
    public String High;
    public String Low;
    public String Open;

    public StockModel(JSONObject result) {
        try {
            this.Name = result.getString("Name");
            this.Symbol = result.getString("Symbol");
            this.LastPrice = result.getString("LastPrice");
            this.High = result.getString("High");
            this.Low = result.getString("Low");
            this.Open = result.getString("Open");
            this.Volume = result.getString("Volume");

            double volume = Double.parseDouble(result.getString("Volume"));
            double vres = (volume/(double)(1000000000));
            if(vres < 1)
            {
                vres = (volume/(double)(1000000));
                if(vres < 1)
                {
                    this.MarketCap = result.getString("Volume");
                }
                else {
                    this.MarketCap = Utils.round(vres, 2) + " Million";
                }
            }
            else
            {
                this.MarketCap = Utils.round(vres, 2) + " Billion";
            }

            double change = Double.parseDouble(result.getString("Change"));
            double changepercent = Double.parseDouble(result.getString("ChangePercent"));
            if(changepercent >= 0) {
                this.Change = Utils.round(change, 2) + "(+" + Utils.round(changepercent, 2) + "%)";
                this.ChangePercent = "+" + Utils.round(changepercent, 2) + "%";
            }
            else {
                this.Change = Utils.round(change, 2) + "(" + Utils.round(changepercent, 2) + "%)";
                this.ChangePercent = Utils.round(changepercent, 2) + "%";
            }
            if(change < 0) { this.ChangeSign = false; }
            else { this.ChangeSign = true; }

            double changeYTD = Double.parseDouble(result.getString("ChangeYTD"));
            double changeYTDPercent = Double.parseDouble(result.getString("ChangePercentYTD"));
            if(changeYTDPercent >= 0)
                this.ChangeYTD = Utils.round(changeYTD, 2) + "(+"+Utils.round(changeYTDPercent, 2)+"%)";
            else
                this.ChangeYTD = Utils.round(changeYTD, 2) + "("+Utils.round(changeYTDPercent, 2)+"%)";
            if(changeYTD < 0) { this.ChangeYTDSign = false; }
            else { this.ChangeYTDSign = true; }

            SimpleDateFormat fromUser = new SimpleDateFormat("EEE MMM dd HH:mm:ss z-HH:mm yyyy");
            SimpleDateFormat myFormat = new SimpleDateFormat("dd MMMM yyyy, HH:mm:ss");
            this.Timestamp =  myFormat.format(fromUser.parse(result.getString("Timestamp")));

            double marketCap = Double.parseDouble(result.getString("MarketCap"));
            double res = (marketCap/(double)(1000000000));
            if(res < 1)
            {
                res = (marketCap/(double)(1000000));
                if(res < 1)
                {
                    this.MarketCap = result.getString("MarketCap");
                }
                else {
                    this.MarketCap = Utils.round(res, 2) + " Million";
                }
            }
            else
            {
                this.MarketCap = Utils.round(res, 2) + " Billion";
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
