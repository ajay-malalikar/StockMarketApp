package com.trojan.ajay.hw_9;

/**
 * Created by Ajay on 4/30/2016.
 */

public class StockDetails {
    public String Name;
    public String Symbol;
    public boolean Sign;

    public StockDetails(String name, String symbol) {
        this.Name = name;
        this.Symbol = symbol;
    }

    public StockDetails(String name, String symbol, boolean sign) {
        this(name, symbol);
        this.Sign = sign;
    }
    @Override
    public String toString() {
        return Symbol.toString();
    }
}
