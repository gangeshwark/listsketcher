package com.shwavan.listsketcher.lists;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.shwavan.listsketcher.R;
import com.shwavan.listsketcher.auth.SessionManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;


public class OpenList extends ActionBarActivity {
    SessionManager session;
    long fileLength;
    FullListObject listObject;
    SQLiteListsHelper listsHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_list);
        session = new SessionManager(this);
        if (!session.isLoggedIn()) {
            session.checkLogin();
            finish();
        }
        if (getActionBar() != null) {
            getActionBar().setTitle("List Reader");
        }
        getActionBar().setDisplayHomeAsUpEnabled(true);
        //only if App runs on ICS & above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            getActionBar().setHomeButtonEnabled(true);
        }
        Intent i = getIntent();
        Uri file_loc = i.getData();

        listObject = readFile(file_loc);
        if (listObject != null) {
            new loadData().execute(listObject);
        } else {
            Toast.makeText(this, "This File is not Compatible with ListSketcher!", Toast.LENGTH_LONG).show();
            finish();
        }

    }

    void save() {
        Toast.makeText(this, "Saving list! Please wait...", Toast.LENGTH_SHORT).show();
        listsHelper = new SQLiteListsHelper(this);
        int count_of_lists = listsHelper.getAllListsCount();
        int count_of_items = listsHelper.getItemsCount();

        String list_name = listObject.getList().getTitle();
        String list_status = listObject.getList().getStatus();
        String list_created_on = listObject.getList().getCreatedOn();

        ListsClass listsClass = new ListsClass(++count_of_lists, list_name, list_status, list_created_on);
        listsHelper.addNewList(listsClass);

        int count = listObject.getItems().size();
        for (int i = 0; i < count; ++i) {
            ++count_of_items;
            String item_name = listObject.getItems().get(i).getName();
            String item_stat = listObject.getItems().get(i).getStatus();
            String item_imp = listObject.getItems().get(i).getImp();
            ListsItemClass listsItemClass = new ListsItemClass(count_of_items, count_of_lists, item_name, item_stat, item_imp);
            listsHelper.addListItem(listsItemClass);
        }
        Toast.makeText(this, "Done!", Toast.LENGTH_SHORT).show();
        Intent i = new Intent(this, ListsDetailActivity.class);
        String list_id = Integer.toString(count_of_lists);

        i.putExtra("list_id", Long.parseLong(list_id));
        startActivity(i);
        finish();
    }

    public FullListObject readFile(Uri file_url) {
        try {
            FileInputStream fis = (FileInputStream) getContentResolver().openInputStream(file_url);
            ObjectInputStream ois = new ObjectInputStream(fis);
            FullListObject fullListObject = (FullListObject) ois.readObject();
            File file = new File(file_url.toString());
            fileLength = file.length();
            if (fullListObject != null) {
                return fullListObject;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.open_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.save_list:
                save();
                break;
            case R.id.close_window:
                finish();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    public class loadData extends AsyncTask<FullListObject, Void, Void> //(Param..., Progress, Result)
    {
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            pDialog = new ProgressDialog(OpenList.this);
            pDialog.setMessage("Opening List File.. Please wait!");
            pDialog.setMax(100);
            pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pDialog.setIndeterminate(true);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(FullListObject... fullListObjects) {

            pDialog.setProgress(0);
            final ListView myListView = (ListView) findViewById(R.id.openItemsView);
            String title = fullListObjects[0].getList().getTitle();
            String author = fullListObjects[0].getAuthor();
            pDialog.setProgress(15);
            OpenListsDetailAdapter adapter;
            adapter = new OpenListsDetailAdapter(OpenList.this, R.layout.main_listview_items, fullListObjects[0].getItems());
            pDialog.setProgress(30);
            TextView title_tv = (TextView) findViewById(R.id.openListTitle);
            title_tv.setText(title);
            TextView author_tv = (TextView) findViewById(R.id.authorView);
            author_tv.setText("From : " + author);
            myListView.setAdapter(adapter);
            pDialog.setProgress(45);
            TextView emptyTV = (TextView) findViewById(R.id.openEmpty);
            pDialog.setProgress(60);
            ImageView emptyImg = (ImageView) findViewById(R.id.openEmptyImg);
            pDialog.setProgress(75);
            myListView.setCacheColorHint(Color.TRANSPARENT);
            pDialog.setProgress(90);
            if (adapter.isEmpty()) {
                emptyTV.setText("This list is empty.");
                emptyTV.setVisibility(View.VISIBLE);
                emptyImg.setVisibility(View.VISIBLE);
            } else {
                myListView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                emptyTV.setVisibility(View.GONE);
                emptyImg.setVisibility(View.GONE);
            }
            pDialog.setProgress(100);


            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            pDialog.dismiss();
        }

    }

}
