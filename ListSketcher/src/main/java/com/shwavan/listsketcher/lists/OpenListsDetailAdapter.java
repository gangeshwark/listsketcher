package com.shwavan.listsketcher.lists;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shwavan.listsketcher.R;

import java.util.List;

/**
 * Created by Gokul on 17/one/14.
 */
public class OpenListsDetailAdapter extends ArrayAdapter<ListsItemClass> {
    int resource;
    SQLiteListsHelper listsHelper;

    public OpenListsDetailAdapter(Context context, int resource, List<ListsItemClass> objects) {
        super(context, resource, objects);
        this.resource = resource;
        listsHelper = new SQLiteListsHelper(getContext());

    }

    public void savealert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        // Setting Dialog Title
        alertDialog.setTitle("Save?");
        // Setting Dialog Message
        alertDialog.setMessage("You can not modify this list until you save! Save this list from the menu!");
        // Setting Icon to Dialog
        alertDialog.setIcon(R.drawable.ic_save_list);
        // Setting Positive "Yes" Button
        alertDialog.setNeutralButton("Okay!", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });
        // Showing Alert Message
        alertDialog.show();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final LinearLayout listView;
        final ListsItemClass item = getItem(position);

        String title = item.getName();
        final long id = item.getItemID();
        String status = item.getStatus();
        final String impView = item.getImp();

        if (convertView == null) {
            listView = new LinearLayout(getContext());
            listView.setBackgroundResource(R.drawable.apptheme_list_selector_holo_light);
            String inflater = Context.LAYOUT_INFLATER_SERVICE;
            LayoutInflater li;
            li = (LayoutInflater) getContext().getSystemService(inflater);
            li.inflate(resource, listView, true);

        } else {
            listView = (LinearLayout) convertView;
        }

        final CheckedTextView titleView = (CheckedTextView) listView.findViewById(R.id.item_name);
        titleView.setText(title);

        TextView idView = (TextView) listView.findViewById(R.id.list_item_id);
        idView.setText(Long.toString(id));

        TextView statView = (TextView) listView.findViewById(R.id.item_status);
        statView.setText(status);

        final ImageButton imp = (ImageButton) listView.findViewById(R.id.imp_but);

        if (item.getImp().equals("no")) {
            imp.setImageResource(R.drawable.ic_not_imp);
        } else {
            imp.setImageResource(R.drawable.ic_imp);
        }
        imp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                savealert();

            }
        });
        if (item.getStatus().equals("done")) {
            titleView.setChecked(true);
            titleView.setTextColor(Color.LTGRAY);
            titleView.setPaintFlags(titleView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            titleView.setChecked(false);
            titleView.setTextColor(Color.BLACK);
            titleView.setPaintFlags(titleView.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
        }
        titleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savealert();

            }
        });

        /*
        ImageButton delete = (ImageButton) listView.findViewById(R.id.delete_item);
        delete.setOnClickListener(newView.OnClickListener() {
            @Override
            public void onClick(View view) {
                listView.setVisibility(View.GONE);
                SQLiteListsHelper sql = new SQLiteListsHelper(getContext()) ;
                sql.deleteItem(item);
            }
        });
*/
        return listView;
    }
}