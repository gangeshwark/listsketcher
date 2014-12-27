package com.shwavan.listsketcher.lists;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shwavan.listsketcher.R;

import java.util.List;

/**
 * Created by Gokul on 12/one/14.
 */
public class ListsOverviewAdapter extends ArrayAdapter<ListsClass> {
    int resource;

    public ListsOverviewAdapter(Context context, int resource, List<ListsClass> objects) {
        super(context, resource, objects);
        this.resource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout listView;
        ListsClass item = getItem(position);

        String title = item.getTitle();
        String date = item.getCreatedOn();
        long id = item.getID();

        if (convertView == null) {
            listView = new LinearLayout(getContext());
            String inflater = Context.LAYOUT_INFLATER_SERVICE;
            LayoutInflater li;
            li = (LayoutInflater) getContext().getSystemService(inflater);
            li.inflate(resource, listView, true);
        } else {
            listView = (LinearLayout) convertView;
        }
        TextView dateView = (TextView) listView.findViewById(R.id.day);
        TextView titleView = (TextView) listView.findViewById(R.id.list_title);
        TextView idView = (TextView) listView.findViewById(R.id.list_id);

        dateView.setText(date);
        titleView.setText(title);
        idView.setText(Long.toString(id));
        return listView;
    }
}
