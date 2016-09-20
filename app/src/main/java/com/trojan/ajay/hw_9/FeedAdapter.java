package com.trojan.ajay.hw_9;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Ajay on 5/2/2016.
 */

public class FeedAdapter extends ArrayAdapter<FeedItemModel> {

    private Context ctxt;
    private int res;
    private LayoutInflater mLayoutInflater;
    List<FeedItemModel> list;

    public FeedAdapter(Context context, int resource, List<FeedItemModel> objects) {
        super(context, resource, objects);
        list = objects;
        ctxt = context;
        res = resource;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater)ctxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(res, parent, false);
        }
        convertView = (LinearLayout) mLayoutInflater.inflate( res, null );

        FeedItemModel item = (FeedItemModel) getItem( position );

        TextView txtTitle = (TextView) convertView.findViewById(R.id.title);
        txtTitle.setText(Html.fromHtml("<u>" + item.Title + "</u>"));

        TextView txtContent = (TextView) convertView.findViewById(R.id.description);
        txtContent.setText(Html.fromHtml(item.Description));

        TextView txtPublisher = (TextView) convertView.findViewById(R.id.pub);
        txtPublisher.setText("Publisher: "+item.Source);

        TextView txtDate = (TextView) convertView.findViewById(R.id.date);
        txtDate.setText("Date:" + item.Date);

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