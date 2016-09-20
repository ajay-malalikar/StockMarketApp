package com.trojan.ajay.hw_9;

/**
 * Created by Ajay on 5/2/2016.
 */
public class AutosuggestModel {
    public String Symbol;
    public String Name;
    public String Exchange;
    public AutosuggestModel(String name, String symb, String exchange)
    {
        this.Symbol = symb;
        this.Name = name;
        this.Exchange = exchange;
    }

    @Override
    public String toString() {
        return this.Symbol.toString();
    }
}
