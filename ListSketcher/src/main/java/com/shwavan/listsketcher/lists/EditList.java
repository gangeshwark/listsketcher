package com.shwavan.listsketcher.lists;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.shwavan.listsketcher.R;
import com.shwavan.listsketcher.ReminderReceiver;

import java.util.Calendar;
import java.util.List;

public class EditList extends ActionBarActivity {
    long list_id;
    List<ListsItemClass> arrayList;
    SQLiteListsHelper listsHelper;
    AutoCompleteTextView item;
    TextView stat;
    TextView imp;
    ArrayAdapter<String> adapter;
    int count, i;
    EditText title;
    AdView adView;
    boolean isRemSet;
    TimePickerDialog mTimePickerDialog;
    DatePickerDialog mDatePickerDialog;
    RemClass reminder;
    TextView time_but;
    TextView date_but;
    LinearLayout addll;
    Button addRem;
    Button delRem;
    ImageButton close_rem;
    Calendar calDate;
    Calendar calTime;
    Calendar calNow;
    Calendar calSet;
    private InterstitialAd interstitial;

    private static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_list);
        listsHelper = new SQLiteListsHelper(this);


        // Create the interstitial.
        interstitial = new InterstitialAd(EditList.this);
        interstitial.setAdUnitId(getString(R.string.adMobId));

        // Create ad request.
        AdRequest adRequest = new AdRequest.Builder().build();

        // Begin loading your interstitial.
        interstitial.loadAd(adRequest);

        adView = (AdView) findViewById(R.id.adView);

        adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR)       // Emulator
                .build();
        adView.loadAd(adRequest);

        Intent intent = getIntent();
        list_id = intent.getLongExtra("list_id", 0);
        ListsClass list = listsHelper.getList(list_id);
        title = (EditText) findViewById(R.id.edit_list_title);
        title.setText(list.getTitle());

        new getListItems().execute();

        Button btnCreate = (Button) findViewById(R.id.addItem);
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final LinearLayout LL = new LinearLayout(EditList.this);
                LinearLayout parentLL = (LinearLayout) findViewById(R.id.parentLL);
                final AutoCompleteTextView tv;
                tv = new AutoCompleteTextView(EditList.this);
                final ImageButton del = new ImageButton(EditList.this);

                LayoutParams pLLlp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                parentLL.setLayoutParams(pLLlp);
                parentLL.setPadding(0, 0, 0, 90);

                LayoutParams LLlp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                LL.setLayoutParams(LLlp);
                LL.setOrientation(LinearLayout.HORIZONTAL);

                LayoutParams tvLP = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1);
                tvLP.setMargins(0, 5, 0, 0);
                tv.setLayoutParams(tvLP);
                tv.setTextAppearance(EditList.this, android.R.attr.textAppearanceLarge);
                tv.setId(200 + i);
                tv.setVerticalScrollBarEnabled(true);
                tv.setAdapter(adapter);
                tv.setHint("New Item");

                tv.setSingleLine(true);
                tv.setImeActionLabel("Next", EditorInfo.IME_ACTION_NEXT);
                tv.setImeOptions(EditorInfo.IME_ACTION_NEXT);
                tv.requestFocus();
                tv.setTypeface(Typeface.SERIF);
                LayoutParams stat = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

                final TextView stat_tv = new TextView(EditList.this);
                stat_tv.setLayoutParams(stat);
                stat_tv.setId(600 + i);
                stat_tv.setVisibility(View.GONE);
                stat_tv.setText("to_do");
                final TextView imp_tv = new TextView(EditList.this);
                imp_tv.setLayoutParams(stat);
                imp_tv.setId(800 + i);
                imp_tv.setVisibility(View.GONE);
                imp_tv.setText("no");

                //Delete button

                del.setId(400 + i);
                del.setImageResource(R.drawable.ic_item_del);
                del.setBackgroundResource(R.drawable.but_selector);
                LayoutParams delparam = new LayoutParams(55,55);
                del.setLayoutParams(delparam);
                del.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        tv.setText("");
                        tv.setVisibility(View.GONE);
                        del.setVisibility(View.GONE);
                        imp_tv.setText("");
                        stat_tv.setText("");
                    }
                });


                LL.addView(tv);
                LL.addView(del);
                LL.addView(imp_tv);
                LL.addView(stat_tv);
                LL.setVerticalScrollBarEnabled(true);
                parentLL.addView(LL);
                i = i + 1;
            }
        });

        addll = (LinearLayout) findViewById(R.id.add_alarm_layout);
        time_but = (TextView) findViewById(R.id.add_time);

        date_but = (TextView) findViewById(R.id.add_date);
        close_rem = (ImageButton) findViewById(R.id.close_rem);
        addRem = (Button) findViewById(R.id.add_rem);

        close_rem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addll.setVisibility(View.GONE);
                addRem.setVisibility(View.VISIBLE);
                calDate.clear();
                calTime.clear();
                calNow.clear();
                calSet.clear();
                isRemSet = false;
                cancelRem();

            }
        });

        addRem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addll.setVisibility(View.VISIBLE);
                addRem.setVisibility(View.INVISIBLE);
                calDate = Calendar.getInstance();
                calTime = Calendar.getInstance();
                calNow = Calendar.getInstance();
                calSet = Calendar.getInstance();

                int day = calDate.get(Calendar.DAY_OF_MONTH);
                int month = calDate.get(Calendar.MONTH);
                int year = calDate.get(Calendar.YEAR);
                int hour = calDate.get(Calendar.HOUR_OF_DAY);
                int min = calDate.get(Calendar.MINUTE);
                String dateString = new StringBuilder().append(pad(day))
                        .append("/").append(pad(month + 1)).append("/").append(pad(year)).toString();
                String timeString = new StringBuilder().append(pad(hour))
                        .append(":").append(pad(min)).toString();

                time_but.setText(timeString);
                date_but.setText(dateString);

                isRemSet = true;

            }
        });

        final DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                calDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                calDate.set(Calendar.YEAR, year);
                calDate.set(Calendar.MONTH, monthOfYear);
                String dateString = new StringBuilder().append(pad(dayOfMonth))
                        .append("/").append(pad(monthOfYear + 1)).append("/").append(pad(year)).toString();

                date_but.setText(dateString);
            }
        };

        final TimePickerDialog.OnTimeSetListener time = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {


                calTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calTime.set(Calendar.MINUTE, minute);
                calTime.set(Calendar.SECOND, 0);
                calTime.set(Calendar.MILLISECOND, 0);
                calNow = Calendar.getInstance();

                if (calTime.compareTo(calNow) <= 0) {
                    //Today Set time passed, count to tomorrow
                    Toast.makeText(getApplicationContext(),"This time has already elapsed!",Toast.LENGTH_SHORT).show();
                }
                String timeString = new StringBuilder().append(pad(hourOfDay))
                        .append(":").append(pad(minute)).toString();

                time_but.setText(timeString);
            }
        };

        date_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int yr = calDate.get(Calendar.YEAR);
                int month = calDate.get(Calendar.MONTH);
                int day = calDate.get(Calendar.DAY_OF_MONTH);

                mDatePickerDialog = new DatePickerDialog(EditList.this,
                        AlertDialog.THEME_HOLO_DARK, dateSetListener,
                        yr,
                        month,
                        day);
                mDatePickerDialog.setTitle("Set Reminder Date");
                mDatePickerDialog.show();
            }
        });
        time_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int hour = calTime.get(Calendar.HOUR_OF_DAY);
                int min = calTime.get(Calendar.MINUTE);
                mTimePickerDialog = new TimePickerDialog(EditList.this,
                        AlertDialog.THEME_HOLO_DARK, time,
                        hour,
                        min,
                        true);
                mTimePickerDialog.setTitle("Set Reminder Time");
                mTimePickerDialog.show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.edit_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.action_save:
                if (!title.getText().toString().equals("")) {
                    save();
                } else {
                    Toast.makeText(getApplicationContext(),
                            "List Name cannot be Empty", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.action_discard:
                discard();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void save() {

        String param = title.getText().toString();
        int itemCount;
        listsHelper.updateLists(list_id, param);
        listsHelper.updateListItemsToDelete(list_id);
        int j;


        for (j = 0; j < i; j++) {
            itemCount = listsHelper.getItemsCount();
            item = (AutoCompleteTextView) findViewById(200 + j);
            stat = (TextView) findViewById(600 + j);
            imp = (TextView) findViewById(800 + j);
            String getItem = item.getText().toString();
            String getStat = stat.getText().toString();
            String getImp = imp.getText().toString();
            if (getItem != null) {
                if (!getItem.equals("")) {
                    ListsItemClass newItem = new ListsItemClass(++itemCount, list_id, getItem, getStat, getImp);
                    listsHelper.addListItem(newItem);
                }
            }
        }
        if (isRemSet) {
            setNStartAlarm();
        }
        finish();
    }

    public void discard() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(EditList.this);
        // Setting Dialog Title
        alertDialog.setTitle("Discard Changes...");

        // Setting Dialog Message
        alertDialog.setMessage("Are you sure?");

        // Setting Icon to Dialog
        alertDialog.setIcon(R.drawable.ic_discard);

        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
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

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        AdRequest adRequest;
        adRequest = new AdRequest.Builder().build();

        adView.destroy();
        adView = new AdView(EditList.this);
        adView.setAdUnitId(getString(R.string.adMobId));
        adView.setAdSize(AdSize.SMART_BANNER);
        adView.loadAd(adRequest);

        LinearLayout contentFrame = (LinearLayout) findViewById(R.id.adLayout);
        contentFrame.addView(adView);
    }

    void cancelRem() {
        Intent myIntent = new Intent(this, ReminderReceiver.class);
        myIntent.putExtra("list_id", list_id);
        try {
            listsHelper.updateRemToDone(list_id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        PendingIntent pendingIntent = PendingIntent.getBroadcast(EditList.this,
                123454321 + (int) list_id, myIntent, 0);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        try {
            alarmManager.cancel(pendingIntent);
        } catch (Exception e) {
            Toast.makeText(this, "Reminder Not Set!", Toast.LENGTH_SHORT).show();
        }

    }

    public void setNStartAlarm() {
        //Cancel previous reminder
        cancelRem();
        //add Reminder again
        calSet.set(Calendar.DAY_OF_MONTH, calDate.get(Calendar.DAY_OF_MONTH));
        calSet.set(Calendar.YEAR, calDate.get(Calendar.YEAR));
        calSet.set(Calendar.MONTH, calDate.get(Calendar.MONTH));

        calSet.set(Calendar.HOUR_OF_DAY, calTime.get(Calendar.HOUR_OF_DAY));
        calSet.set(Calendar.MINUTE, calTime.get(Calendar.MINUTE));
        calSet.set(Calendar.SECOND, calTime.get(Calendar.SECOND));
        calSet.set(Calendar.MILLISECOND, calTime.get(Calendar.MILLISECOND));
        // MyView is my current Activity, and AlarmReceiver is the
        // BroadCastReceiver
        int count = listsHelper.getRemCount();
        RemClass reminder = new RemClass(count + 1,
                list_id,
                calSet.get(Calendar.DAY_OF_MONTH),
                calSet.get(Calendar.MONTH),
                calSet.get(Calendar.YEAR),
                calSet.get(Calendar.HOUR_OF_DAY),
                calSet.get(Calendar.MINUTE),
                "not_done");

        listsHelper.addNewRem(reminder);
        Intent intent = new Intent(this, ReminderReceiver.class);
        intent.putExtra("list_id", list_id);
        PendingIntent myPendingIntent = PendingIntent.getBroadcast(EditList.this,
                123454321 + (int) list_id, intent, 0);
        AlarmManager alarmManager1 = (AlarmManager) getSystemService(ALARM_SERVICE);
        /*
         * The following sets the Alarm in the specific time by getting the long
		 * value of the alarm date time which is in calendar object by calling
		 * the getTimeInMillis(). Since Alarm supports only long value , we're
		 * using this method.
		 */
        alarmManager1.set(AlarmManager.RTC_WAKEUP, calSet.getTimeInMillis(),
                myPendingIntent);
    }

    public class getListItems extends AsyncTask<Long, Void, List<ListsItemClass>>//(Param..., Progress, Result)
    {
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            pDialog = new ProgressDialog(EditList.this);
            pDialog.setMessage("Loading to Edit List..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected List<ListsItemClass> doInBackground(Long... id) {


            listsHelper = new SQLiteListsHelper(EditList.this);
            arrayList = listsHelper.getAllListsItems(list_id);
            count = listsHelper.getListItemsCountNotDelete(list_id);
            reminder = listsHelper.getReminder(list_id);

            return arrayList;
        }

        @Override
        protected void onPostExecute(List<ListsItemClass> arrayList) {
            String[] Items_array = getResources().getStringArray(R.array.items_suggestion);
            adapter = new ArrayAdapter<String>(EditList.this, android.R.layout.simple_dropdown_item_1line, Items_array);

            for (i = 0; i < count; i++) {

                final LinearLayout LL = new LinearLayout(EditList.this);
                LinearLayout parentLL = (LinearLayout) findViewById(R.id.parentLL);
                final AutoCompleteTextView tv;
                tv = new AutoCompleteTextView(EditList.this);
                final ImageButton del = new ImageButton(EditList.this);
                LayoutParams pLLlp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                parentLL.setLayoutParams(pLLlp);
                parentLL.setPadding(0, 0, 0, 90);
                LayoutParams LLlp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                LL.setLayoutParams(LLlp);
                LL.setOrientation(LinearLayout.HORIZONTAL);

                LayoutParams tvLP = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1);
                tvLP.setMargins(0, 5, 0, 0);

                tv.setLayoutParams(tvLP);
                tv.setTextAppearance(EditList.this, android.R.attr.textAppearanceLarge);
                tv.setId(200 + i);
                tv.setVerticalScrollBarEnabled(true);
                tv.setAdapter(adapter);
                tv.setText(arrayList.get(i).getName());
                tv.setSingleLine(true);
                tv.setImeActionLabel("Next", EditorInfo.IME_ACTION_NEXT);
                tv.setImeOptions(EditorInfo.IME_ACTION_NEXT);
                tv.requestFocus();
                tv.setTypeface(Typeface.SERIF);

                final LayoutParams stat = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

                final TextView stat_tv = new TextView(EditList.this);
                stat_tv.setLayoutParams(stat);
                stat_tv.setId(600 + i);
                stat_tv.setVisibility(View.GONE);
                stat_tv.setText(arrayList.get(i).getStatus());
                final TextView imp_tv = new TextView(EditList.this);
                imp_tv.setLayoutParams(stat);
                imp_tv.setId(800 + i);
                imp_tv.setVisibility(View.GONE);
                imp_tv.setText(arrayList.get(i).getImp());

                //Delete button

                del.setId(400 + i);
                del.setImageResource(R.drawable.ic_item_del);
                del.setBackgroundResource(R.drawable.but_selector);
                LayoutParams delparam = new LayoutParams(55,55);
                delparam.setMargins(5, 5, 5, 5);
                del.setLayoutParams(delparam);
                del.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        tv.setText("");
                        tv.setVisibility(View.GONE);
                        del.setVisibility(View.GONE);
                        imp_tv.setText("");
                        stat_tv.setText("");
                    }
                });
                LL.addView(tv);
                LL.addView(del);
                LL.addView(imp_tv);
                LL.addView(stat_tv);
                LL.setVerticalScrollBarEnabled(true);
                parentLL.addView(LL);
            }

            if (reminder == null) {
                addll.setVisibility(View.GONE);
                addRem.setVisibility(View.VISIBLE);
                isRemSet = false;
            } else {
                calDate = Calendar.getInstance();
                calTime = Calendar.getInstance();
                calNow = Calendar.getInstance();
                calSet = Calendar.getInstance();
                addll.setVisibility(View.VISIBLE);
                addRem.setVisibility(View.INVISIBLE);
                int day = reminder.get_day();
                int month = reminder.get_month();
                int year = reminder.get_year();
                int hour = reminder.get_hour();
                int min = reminder.get_minute();
                calDate.set(Calendar.DAY_OF_MONTH, day);
                calDate.set(Calendar.MONTH, month);
                calDate.set(Calendar.YEAR, year);
                calTime.set(Calendar.HOUR_OF_DAY, hour);
                calTime.set(Calendar.MINUTE, min);
                String dateString = new StringBuilder().append(pad(day))
                        .append("/").append(pad(month + 1)).append("/").append(pad(year)).toString();
                String timeString = new StringBuilder().append(pad(hour))
                        .append(":").append(pad(min)).toString();
                time_but.setText(timeString);
                date_but.setText(dateString);
                isRemSet = true;
            }

            pDialog.dismiss();
        }
    }

}