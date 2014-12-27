package com.shwavan.listsketcher.lists;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.plus.PlusClient;
import com.shwavan.listsketcher.R;
import com.shwavan.listsketcher.ReminderReceiver;
import com.shwavan.listsketcher.auth.SessionManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ListsDetailActivity extends ActionBarActivity {
    private final static String APP_P_NAME = "com.shwavan.listsketcher";
    long list_id;
    List<ListsItemClass> arrayList;
    ListsDetailAdapter adapter;
    ArrayAdapter<String> items_adapter;
    PlusClient mPlusClient;
    SQLiteListsHelper listsHelper;
    RemClass reminder;
    TextView textTitle;
    AutoCompleteTextView title;
    ListsClass list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lists_detail);
        //getActionBar().setDisplayHomeAsUpEnabled(true);
        //only if App runs on ICS & above
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            getActionBar().setHomeButtonEnabled(true);
        }*/

        getActionBar().setDisplayHomeAsUpEnabled(true);
        //only if App runs on ICS & above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            getActionBar().setHomeButtonEnabled(true);
        }

        textTitle = (TextView) findViewById(R.id.listTitle);
        String[] Items_array = getResources().getStringArray(R.array.items_suggestion);
        items_adapter = new ArrayAdapter<String>(ListsDetailActivity.this, android.R.layout.simple_dropdown_item_1line, Items_array);

        Intent i = getIntent();
        list_id = i.getLongExtra("list_id", -1);
        int notifId = i.getIntExtra("NotifIdn", -1);
        if (notifId != -1) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.cancel(notifId);
        }
        listsHelper = new SQLiteListsHelper(this);

        list = listsHelper.getList(list_id);

        if (list == null || list.getStatus().equals("delete")) {
            Toast.makeText(this, "List Couldn't be found!", Toast.LENGTH_LONG).show();
            finish();
        }

        textTitle.setText(list.getTitle());

        title = (AutoCompleteTextView) findViewById(R.id.add_itemTitle);
        title.setAdapter(items_adapter);

        ImageButton add = (ImageButton) findViewById(R.id.addItemBut);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (title.getText().toString().equals("")) {
                    Toast.makeText(ListsDetailActivity.this, "Give item name", Toast.LENGTH_SHORT).show();
                    title.requestFocus();
                } else {
                    int item_id = listsHelper.getItemsCount();
                    String param = title.getText().toString();
                    ListsItemClass listsItemClass = new ListsItemClass(++item_id, list_id, param, "to_do", "no");
                    listsHelper.addListItem(listsItemClass);
                    title.setText("");
                    new getListItems().execute();

                }

            }
        });
        //new getListItems().execute();

    }

    public void onResume() {
        super.onResume();
        new getListItems().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.lists_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.share_as_list:
                shareAsFile();
                break;
            case R.id.share_as_text:
                shareAsText();
                break;
            case R.id.action_delete:
                delete();
                break;
            case R.id.action_edit:
                edit();
                break;
            default:
                break;

        }
        return super.onOptionsItemSelected(item);
    }


    public void edit() {
        Intent i = new Intent(ListsDetailActivity.this, EditList.class);
        i.putExtra("list_id", list_id);
        startActivity(i);
    }


    public void delete() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ListsDetailActivity.this);
        // Setting Dialog Title
        alertDialog.setTitle("Confirm List Delete...");

        // Setting Dialog Message
        alertDialog.setMessage("Are you sure you want delete this List?");

        // Setting Icon to Dialog
        alertDialog.setIcon(R.drawable.ic_delete);

        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                listsHelper.updateListToDelete(list_id);
                listsHelper.updateListItemsToDelete(list_id);
                cancelRem();
                // Write your code here to invoke YES event
                Toast.makeText(getApplicationContext(), "List deleted", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        // Setting Negative "NO" Button
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to invoke NO event
                return;
            }
        });

        // Showing Alert Message
        alertDialog.show();

    }

    void cancelRem() {
        Intent myIntent = new Intent(this, ReminderReceiver.class);
        myIntent.putExtra("list_id", list_id);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(ListsDetailActivity.this,
                123454321 + (int) list_id, myIntent, 0);
        listsHelper.updateRemToDone(list_id);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    public File saveObject(FullListObject fullListObject) {
        try {
            if (isExternalStorageWritable()) {
                File fileDir = new File(Environment.getExternalStorageDirectory().getPath() + "/ListSketcher/");
                fileDir.mkdirs();
                String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
                File outputFile = new File(fileDir, "LS_" + fullListObject.getList().getTitle() + "_" + date + ".lsk");
                ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream
                        (outputFile)); //Select where you wish to save the file...
                oos.writeObject(fullListObject); // write the class as an 'object'
                oos.flush(); // flush the stream to insure all of the information was written to 'save_object.bin'
                oos.close();// close the stream
                return outputFile;
            }
        } catch (Exception ex) {
            Log.v("Serialization Save Error : ", ex.getMessage());
            ex.printStackTrace();
        }
        return null;
    }

    private void shareAsFile() {
        SessionManager sessionManager = new SessionManager(ListsDetailActivity.this);
        List<ListsItemClass> shareList = listsHelper.getAllListsItems(list_id);
        ListsClass list = listsHelper.getList(list_id);
        FullListObject newSaveList = new FullListObject(list, shareList, sessionManager.getName());

        File send_file = saveObject(newSaveList);
        if (send_file == null) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(ListsDetailActivity.this);
            // Setting Dialog Title
            alertDialog.setTitle("Can't Share!");
            // Setting Dialog Message
            alertDialog.setMessage("Couldn't open the file to share! Try again later!");
            // Setting Icon to Dialog
            alertDialog.setIcon(R.drawable.ic_discard);

            // Setting Positive "Yes" Button
            alertDialog.setPositiveButton("Close", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    return;
                }
            });
            alertDialog.show();
        }

        int count = listsHelper.getListItemsCountNotDelete(list_id);
        String listName = list.getTitle();
        StringBuilder sb = new StringBuilder();
        sb.append("List Name : " + listName + "\n");


        for (int i = 0; i < count; i++) {
            if (shareList.get(i).getStatus().equals("done")) {
                if (shareList.get(i).getImp().equals("no")) {
                    sb.append(i + 1 + ". " + shareList.get(i).getName() + ", Not Important, Done\n");
                } else if (shareList.get(i).getImp().equals("yes")) {
                    sb.append(i + 1 + ". " + shareList.get(i).getName() + ", Important, Done\n");
                }
            } else if (shareList.get(i).getStatus().equals("to_do")) {
                if (shareList.get(i).getImp().equals("no")) {
                    sb.append(i + 1 + ". " + shareList.get(i).getName() + ", Not Important, Not Done\n");
                } else if (shareList.get(i).getImp().equals("yes")) {
                    sb.append(i + 1 + ". " + shareList.get(i).getName() + ", Important, Not Done\n");
                }
            }
        }
        sb.append("\nThis attachment can't be opened without ListSketcher App.\n\nSent via ListSketcher!\nGet it now : https://play.google.com/store/apps/details?id=" + APP_P_NAME + "\n\n");

        Uri uri = Uri.parse("file://" + send_file.getAbsolutePath());

        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("message/rfc822");
        String shareBody = sb.toString();
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, listName);
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        sharingIntent.putExtra(android.content.Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(sharingIntent, "Share List via"));
    }

    private void shareAsText() {

        List<ListsItemClass> shareList = listsHelper.getAllListsItems(list_id);
        ListsClass list = listsHelper.getList(list_id);
        int count = listsHelper.getListItemsCountNotDelete(list_id);
        String listName = list.getTitle();
        StringBuilder sb = new StringBuilder();
        sb.append("List Name : " + listName + "\n");


        for (int i = 0; i < count; i++) {
            if (shareList.get(i).getStatus().equals("done")) {
                if (shareList.get(i).getImp().equals("no")) {
                    sb.append(i + 1 + ". " + shareList.get(i).getName() + ", Not Important, Done\n");
                } else if (shareList.get(i).getImp().equals("yes")) {
                    sb.append(i + 1 + ". " + shareList.get(i).getName() + ", Important, Done\n");
                }
            } else if (shareList.get(i).getStatus().equals("to_do")) {
                if (shareList.get(i).getImp().equals("no")) {
                    sb.append(i + 1 + ". " + shareList.get(i).getName() + ", Not Important, Not Done\n");
                } else if (shareList.get(i).getImp().equals("yes")) {
                    sb.append(i + 1 + ". " + shareList.get(i).getName() + ", Important, Not Done\n");
                }
            }
        }
        sb.append("\nSent via ListSketcher!\nGet it now : https://play.google.com/store/apps/details?id=" + APP_P_NAME + "\n\n");

        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBody = sb.toString();
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, listName);
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Share List via"));
    }

    public String parse(int num) {
        String res;
        if (num < 10) {
            res = "0" + String.valueOf(num);
        } else {
            res = Integer.toString(num);
        }
        return res;
    }

    public class getListItems extends AsyncTask<Long, Void, List<ListsItemClass>>//(Param..., Progress, Result)
    {
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            pDialog = new ProgressDialog(ListsDetailActivity.this);
            pDialog.setMessage("Loading Lists..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected List<ListsItemClass> doInBackground(Long... id) {

            arrayList = listsHelper.getAllListsItems(list_id);
            reminder = listsHelper.getReminder(list_id);
            list = listsHelper.getList(list_id);
            return arrayList;
        }

        @Override
        protected void onPostExecute(List<ListsItemClass> arrayList) {
            TextView remtv;
            remtv = (TextView) findViewById(R.id.rem);
            textTitle.setText(list.getTitle());
            if (reminder != null) {

                String rem = "Alarm: " + parse(reminder.get_hour()) + ":"
                        + parse(reminder.get_minute());

                if (reminder.get_day() == Calendar.getInstance().get(Calendar.DAY_OF_MONTH) &&
                        reminder.get_month() == Calendar.getInstance().get(Calendar.MONTH)) {
                    rem = rem + ", Today";
                } else {
                    rem = rem + ", " + parse(reminder.get_day()) + "/" + parse(reminder.get_month() + 1);
                }
                remtv.setText(rem);
            } else {
                remtv.setText("No Reminder!");
            }
            final ListView myListView = (ListView) findViewById(R.id.itemsView);
            adapter = new ListsDetailAdapter(ListsDetailActivity.this, R.layout.main_listview_items, arrayList);
            TextView emptyTV = (TextView) findViewById(R.id.empty);
            ImageView emptyImg = (ImageView) findViewById(R.id.empty_img);
            myListView.setCacheColorHint(Color.TRANSPARENT);
            if (adapter.isEmpty()) {
                emptyTV.setText("This list is empty. Add items!");
                emptyTV.setVisibility(View.VISIBLE);
                emptyImg.setVisibility(View.VISIBLE);
            } else {
                myListView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                emptyTV.setVisibility(View.GONE);
                emptyImg.setVisibility(View.GONE);
            }
            /*myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View view, int arg2,
                                        long arg3) {
                    CheckedTextView item = (CheckedTextView) view.findViewById(R.id.item_name);

                    if (listsHelper.getListsItems(list_id).getStatus().equals("to_do")) {
                        item.setTextColor(Color.GREEN);
                        item.setPaintFlags(item.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        listsHelper.updateListItemToDone(list_id);
                    }
                    else{
                        item.setTextColor(Color.BLACK);
                        item.setPaintFlags(item.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
                        listsHelper.updateListItemToDo(list_id);
                    }
                }
            });*/
            pDialog.dismiss();
        }
    }
}