package com.shwavan.listsketcher.lists;


import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.shwavan.listsketcher.HelpActivity;
import com.shwavan.listsketcher.R;
import com.shwavan.listsketcher.ScheduledSyncService;
import com.shwavan.listsketcher.SettingsActivity;
import com.shwavan.listsketcher.auth.SessionManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class ViewLists extends Fragment {

    public static ListsOverviewAdapter adapter;
    View rootView;
    ListView myListView;
    EditText title;
    String list_name;
    SQLiteListsHelper listsHelper;
    SessionManager session;
    private InterstitialAd interstitial;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_view_lists, container, false);

        new getItems().execute();
        title = (EditText) rootView.findViewById(R.id.add_listTitle);
        title.clearFocus();

        ImageButton add = (ImageButton) rootView.findViewById(R.id.addBut);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                list_name = title.getText().toString();
                if (list_name.equals("")) {
                    Toast.makeText(getActivity(), "List Name cannot be Empty", Toast.LENGTH_SHORT).show();
                } else {
                    Intent i = new Intent(getActivity(), AddListActivity.class);
                    i.putExtra("title", list_name);
                    startActivity(i);
                    title.setText("");
                }

            }
        });

        myListView = (ListView) rootView.findViewById(R.id.listView);

        listsHelper = new SQLiteListsHelper(getActivity());
        session = new SessionManager(getActivity());
        if (session.isFirst()) {
            session.getEditor().putBoolean("FirstUse", false);

            String param = "Shopping List";
            int listCount, itemCount;
            String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
            listCount = listsHelper.getAllListsCount();
            itemCount = listsHelper.getItemsCount();
            ListsClass newList = new ListsClass(listCount + 1, param, "to_do", date);
            listsHelper.addNewList(newList, "AA112233");
            ListsItemClass newItem1 = new ListsItemClass(++itemCount, listCount + 1, "Milk", "to_do", "no");
            ListsItemClass newItem2 = new ListsItemClass(++itemCount, listCount + 1, "Bread", "done", "no");
            ListsItemClass newItem3 = new ListsItemClass(++itemCount, listCount + 1, "Cake", "to_do", "yes");
            ListsItemClass newItem4 = new ListsItemClass(++itemCount, listCount + 1, "Cheese", "done", "yes");
            ListsItemClass newItem5 = new ListsItemClass(++itemCount, listCount + 1, "Eggs", "to_do", "no");
            listsHelper.addListItem(newItem1, "AA112233");
            listsHelper.addListItem(newItem2, "AA112233");
            listsHelper.addListItem(newItem3, "AA112233");
            listsHelper.addListItem(newItem4, "AA112233");
            listsHelper.addListItem(newItem5, "AA112233");

            param = "Meeting List";
            listCount = listsHelper.getAllListsCount();
            itemCount = listsHelper.getItemsCount();
            newList = new ListsClass(listCount + 1, param, "to_do", date);
            listsHelper.addNewList(newList, "AA112233");

            newItem1 = new ListsItemClass(++itemCount, listCount + 1, "Laptop", "to_do", "no");
            newItem2 = new ListsItemClass(++itemCount, listCount + 1, "Documents", "done", "no");
            newItem3 = new ListsItemClass(++itemCount, listCount + 1, "Files", "to_do", "yes");
            newItem4 = new ListsItemClass(++itemCount, listCount + 1, "Tender Notice", "done", "yes");
            newItem5 = new ListsItemClass(++itemCount, listCount + 1, "Sanction Letter", "to_do", "no");
            listsHelper.addListItem(newItem1, "AA112233");
            listsHelper.addListItem(newItem2, "AA112233");
            listsHelper.addListItem(newItem3, "AA112233");
            listsHelper.addListItem(newItem4, "AA112233");
            listsHelper.addListItem(newItem5, "AA112233");

            param = "To-Do List";
            listCount = listsHelper.getAllListsCount();
            itemCount = listsHelper.getItemsCount();
            newList = new ListsClass(listCount + 1, param, "to_do", date);
            listsHelper.addNewList(newList, "AA112233");

            newItem1 = new ListsItemClass(++itemCount, listCount + 1, "Wash Car", "to_do", "no");
            newItem2 = new ListsItemClass(++itemCount, listCount + 1, "Gardening", "done", "no");
            newItem3 = new ListsItemClass(++itemCount, listCount + 1, "Clean Room", "done", "yes");
            newItem5 = new ListsItemClass(++itemCount, listCount + 1, "Cycling", "to_do", "yes");
            listsHelper.addListItem(newItem1, "AA112233");
            listsHelper.addListItem(newItem2, "AA112233");
            listsHelper.addListItem(newItem3, "AA112233");
            listsHelper.addListItem(newItem5, "AA112233");
            session.setNotFirst();
        }

        return rootView;
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        new getItems().execute();

    }


    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);
        session = new SessionManager(getActivity());
        if (!session.isSynced() && session.isLoggedIn()) {
            Intent i = new Intent(getActivity(), ScheduledSyncService.class);
            getActivity().startService(i);
        }
    }

    public void onResume() {
        super.onResume();
        new getItems().execute();
        title.setText("");

    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.view_lists, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_list:
                Intent i = new Intent(this.getActivity(), AddListActivity.class);
                startActivity(i);
                break;
            case R.id.action_settings:
                Intent settings = new Intent(this.getActivity(), SettingsActivity.class);
                startActivity(settings);
                break;
            case R.id.refresh:
                new getItems().execute();
                break;
            /*case R.id.help:
                Intent help = new Intent(this.getActivity(), HelpActivity.class);
                startActivity(help);*/
            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    public void displayInterstitial() {
        if (interstitial.isLoaded()) {
            interstitial.show();
        } else {
            Log.d("Interstitial Ad", "Interstitial ad was not ready to be shown.");
        }
    }

    public class getItems extends AsyncTask<String, Void, List<ListsClass>>//(Param, Progress, Result)
    {
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Loading Lists..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
            interstitial = new InterstitialAd(getActivity());
            interstitial.setAdUnitId(getString(R.string.adMobId));
            // Create ad request.
            AdRequest adRequest1 = new AdRequest.Builder().build();
            // Begin loading your interstitial.
            interstitial.loadAd(adRequest1);
        }

        @Override
        protected List<ListsClass> doInBackground(String... obj) {

            SQLiteListsHelper listsHelper = new SQLiteListsHelper(getActivity());
            List<ListsClass> arrayList = listsHelper.getAllLists("to_do");

            return arrayList;
        }

        @Override
        protected void onPostExecute(List<ListsClass> arrayList) {

            adapter = new ListsOverviewAdapter(rootView.getContext(),
                    R.layout.main_listsview_list, arrayList);
            myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View view, int arg2,
                                        long arg3) {
                    displayInterstitial();

                    Intent i = new Intent(getActivity(), ListsDetailActivity.class);
                    String list_id = ((TextView) view.findViewById(R.id.list_id)).getText().toString();
                    i.putExtra("list_id", Long.parseLong(list_id));
                    startActivity(i);

                }
            });
            myListView.setAdapter(adapter);
            pDialog.dismiss();
        }
    }
}