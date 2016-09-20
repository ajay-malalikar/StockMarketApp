package com.trojan.ajay.hw_9;
import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Ajay on 5/2/2016.
 */

public class SuggestAdapter extends ArrayAdapter<AutosuggestModel> {

    private static final int MAX_RESULTS = 10;
    private Context context;
    private LayoutInflater layoutInflater;
    private List<AutosuggestModel> suggestions;

    public SuggestAdapter(Activity context) {
        super(context, R.layout.search_list);
        suggestions = new ArrayList<>();
        this.context = context;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = (LinearLayout) layoutInflater.inflate( R.layout.search_list, null );

        AutosuggestModel item = (AutosuggestModel)getItem(position);

        TextView txtDesc = (TextView) convertView.findViewById(R.id.autosymbol);
        txtDesc.setText(item.Symbol);

        TextView txtName = (TextView) convertView.findViewById(R.id.autoname);
        txtName.setText(item.Name + " (" + item.Exchange + ")");
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
                results.values = suggestions;
                results.count = suggestions.size();
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