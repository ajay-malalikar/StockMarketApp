package com.trojan.ajay.hw_9;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ajay on 4/30/2016.
 */

public class StockDetailsAdapter extends ArrayAdapter<StockDetails> {

    private Context mContext;
    private int res;
    private LayoutInflater mLayoutInflater;
    List<StockDetails> list;

    public StockDetailsAdapter(Context context, int resource, List<StockDetails> objects) {
        super(context, resource, objects);
        list = objects;
        mContext = context;
        res = resource;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        convertView = mLayoutInflater.inflate( res, null );
        StockDetails item = getItem( position );

        TextView txtName = (TextView) convertView.findViewById(R.id.text_stock_detail);
        txtName.setText(item.Name);

        TextView txtDesc = (TextView) convertView.findViewById(R.id.text_stock_value);
        txtDesc.setText(item.Symbol);

        if(item.Name.equals("CHANGE"))
        {
            ImageView image = (ImageView) convertView.findViewById(R.id.image_stock_detail);
            if(item.Sign)
                image.setImageResource(R.drawable.up);
            else
                image.setImageResource(R.drawable.down);
        }

        if(item.Name.equals("CHANGEYTD"))
        {
            ImageView image = (ImageView) convertView.findViewById(R.id.image_stock_detail);
            if(item.Sign)
                image.setImageResource(R.drawable.up);
            else
                image.setImageResource(R.drawable.down);
        }

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
}

