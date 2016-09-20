package com.trojan.ajay.hw_9;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.nhaarman.listviewanimations.util.Swappable;

import java.util.List;

/**
 * Created by Ajay on 5/3/2016.
 */
public class FavoritesAdapter extends ArrayAdapter<FavoriteModel> implements Swappable {

    private Context ctxt;
    private int res;
    private LayoutInflater layoutInflater;
    List<FavoriteModel> list;

    public FavoritesAdapter(Context context, int resource, List<FavoriteModel> objects) {
        super(context, R.layout.search_list);
        list = objects;
        ctxt = context;
        res = resource;
        layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = layoutInflater.inflate( R.layout.favorite_item, null );
        FavoriteModel item = getItem(position);

        TextView txtSymb = (TextView) convertView.findViewById(R.id.favSymbol);
        txtSymb.setText(item.Symbol);

        TextView txtName = (TextView) convertView.findViewById(R.id.favName);
        txtName.setText(item.Name);

        TextView txtStockPrice = (TextView) convertView.findViewById(R.id.favStockPrice);
        txtStockPrice.setText(item.LastPrice);

        TextView txtChangePercent = (TextView) convertView.findViewById(R.id.favChangePercent);
        txtChangePercent.setText(item.ChangePercent);
        if(item.ChangePercent.contains("-"))
            txtChangePercent.setBackgroundResource(android.R.color.holo_red_light);
        else
            txtChangePercent.setBackgroundResource(android.R.color.holo_green_light);

        TextView txtMarketCap = (TextView) convertView.findViewById(R.id.favMarketCap);
        txtMarketCap.setText("Market Cap: " + item.MarketCap);

        return convertView;
    }

    @Override
    public Filter getFilter()
    {
        return new Filter()
        {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence)
            {
                FilterResults results = new FilterResults();
                results.values = list;
                results.count = list.size();
                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults)
            {
                notifyDataSetChanged();
            }
        };
    }

    public void remove(int position)
    {
        super.remove(super.getItem(position));
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public void swapItems(int pos1, int pos2) {
        FavoriteModel f1 = this.getItem(pos1);
        FavoriteModel f2 = this.getItem(pos2);

        this.remove(f1);
        this.insert(f2, pos1);
        this.remove(f2);
        this.insert(f1, pos2);
        this.notifyDataSetChanged();
    }
}
