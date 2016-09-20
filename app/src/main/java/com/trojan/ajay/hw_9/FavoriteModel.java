package com.trojan.ajay.hw_9;

/**
 * Created by Ajay on 5/2/2016.
 */
public class FavoriteModel {
    public String Symbol;
    public String Name;
    public String LastPrice;
    public String ChangePercent;
    public String MarketCap;

    public FavoriteModel() {}

    public FavoriteModel(boolean fromAjax, String symbol, String name, String lastPrice, String changePercent, String marketCap) {
        Symbol = symbol;
        Name = name;
        LastPrice = "$ "+lastPrice;

        if(fromAjax)
        {
            double changepercent = Double.parseDouble(changePercent);
            if(changepercent >= 0) {
                this.ChangePercent = "+" + Utils.round(changepercent, 2) + "%";
            }
            else {
                this.ChangePercent = Utils.round(changepercent, 2) + "%";
            }

            double cap = Double.parseDouble(marketCap);
            double res = (cap/(double)(1000000000));
            if(res < 1)
            {
                res = (cap/(double)(1000000));
                if(res < 1)
                {
                    this.MarketCap = marketCap;
                }
                else {
                    this.MarketCap = Utils.round(res, 2) + " Million";
                }
            }
            else
            {
                this.MarketCap = Utils.round(res, 2) + " Billion";
            }
        }
        else {
            ChangePercent = changePercent;
            MarketCap = marketCap;
        }
    }
}
