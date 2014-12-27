package com.shwavan.listsketcher.lists;

import android.app.ActionBar;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddListActivity extends ActionBarActivity {

    AutoCompleteTextView item, title;
    SQLiteListsHelper listsHelper;
    LinearLayout LL;
    ArrayAdapter<String> adapter, title_adapter;
    int itemsCount = 0;
    View rootView;
    AutoCompleteTextView tv;
    AdView adView;
    TimePickerDialog mTimePickerDialog;
    DatePickerDialog mDatePickerDialog;
    Calendar calDate;
    Calendar calTime;
    Calendar calNow;
    Calendar calSet;
    boolean isRemSet = false;
    private InterstitialAd interstitial;

    public AddListActivity() {
        calDate = Calendar.getInstance();
        calNow = Calendar.getInstance();
        calSet = (Calendar) calNow.clone();
    }

    private static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_list);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        //only if App runs on ICS & above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            getActionBar().setHomeButtonEnabled(true);
        }
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.save_but) {
            if (!title.getText().toString().equals("")) {
                save();
            } else {
                Toast.makeText(getApplicationContext(),
                        "List Name cannot be Empty", Toast.LENGTH_SHORT).show();
            }
        }
        if (id == R.id.disc_but) {
            discard();
        }
        return super.onOptionsItemSelected(item);
    }

    public void save() {
        listsHelper = new SQLiteListsHelper(this);
        String param = title.getText().toString();
        int listCount, itemCount;
        String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
        listCount = listsHelper.getAllListsCount();
        itemCount = listsHelper.getItemsCount();
        ListsClass newList = new ListsClass(listCount + 1, param, "to_do", date);
        listsHelper.addNewList(newList);
        if (isRemSet) {

            setNStartAlarm(listCount + 1);
        }
        int i;
        for (i = 1; i <= itemsCount; i++) {
            item = (AutoCompleteTextView) rootView.findViewById(i);
            String getItem = item.getText().toString();
            if (!item.getText().toString().equals("")) {
                ListsItemClass newItem = new ListsItemClass(++itemCount, listCount + 1, getItem, "to_do", "no");
                listsHelper.addListItem(newItem);
            }
        }
/*
        String list_1 = item1.getText().toString();
        ListsItemClass newItem1 = new ListsItemClass(itemCount+one , listCount+one, list_1, "to_do");
        listsHelper.addListItem(newItem1);

        String list_2 = item2.getText().toString();
        ListsItemClass newItem2 = new ListsItemClass(itemCount+2 , listCount+one, list_2, "to_do");
        listsHelper.addListItem(newItem2);
*/
        finish();
    }

    public void discard() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(AddListActivity.this);
        // Setting Dialog Title
        alertDialog.setTitle("Discard..");

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
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            toggleActionBar();
        }
        return true;
    }

    private void toggleActionBar() {
        ActionBar actionBar = getActionBar();

        if (actionBar != null) {
            if (actionBar.isShowing()) {
                actionBar.hide();
            } else {
                actionBar.show();
            }
        }
    }

    public void setNStartAlarm(long list_id) {

        calSet.set(Calendar.DAY_OF_MONTH, calDate.get(Calendar.DAY_OF_MONTH));
        calSet.set(Calendar.YEAR, calDate.get(Calendar.YEAR));
        calSet.set(Calendar.MONTH, calDate.get(Calendar.MONTH));
        calSet.set(Calendar.HOUR_OF_DAY, calTime.get(Calendar.HOUR_OF_DAY));
        calSet.set(Calendar.MINUTE, calTime.get(Calendar.MINUTE));
        calSet.set(Calendar.SECOND, calTime.get(Calendar.SECOND));
        calSet.set(Calendar.MILLISECOND, calTime.get(Calendar.MILLISECOND));

        int count = listsHelper.getRemCount();
        RemClass reminder = new RemClass(count + 1, list_id,
                calSet.get(Calendar.DAY_OF_MONTH),
                calSet.get(Calendar.MONTH),
                calSet.get(Calendar.YEAR),
                calSet.get(Calendar.HOUR_OF_DAY),
                calSet.get(Calendar.MINUTE),
                "not_done");

        listsHelper.addNewRem(reminder);
        //ReminderReceiver is the BroadCastReceiver
        Intent myIntent = new Intent(this, ReminderReceiver.class);
        myIntent.putExtra("list_id", list_id);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(AddListActivity.this,
                123454321 + (int) list_id, myIntent, 0);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        /*
         * The following sets the Alarm in the specific time by getting the long
		 * value of the alarm date time which is in calendar object by calling
		 * the getTimeInMillis(). Since Alarm supports only long value , we're
		 * using this method.
		 */
        alarmManager.set(AlarmManager.RTC_WAKEUP, calSet.getTimeInMillis(),
                pendingIntent);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public class PlaceholderFragment extends Fragment {


        public PlaceholderFragment() {
        }

        public void displayInterstitial() {
            if (interstitial.isLoaded()) {
                interstitial.show();
            } else {
                Log.d("Interstitial Ad", "Interstitial ad was not ready to be shown.");
            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            rootView = inflater.inflate(R.layout.fragment_add_list, container, false);
            // Create the interstitial.
            String[] title_array = getResources().getStringArray(R.array.list_suggestion);
            title_adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_dropdown_item_1line, title_array);
            interstitial = new InterstitialAd(getActivity());
            interstitial.setAdUnitId(getString(R.string.adMobId));

            // Create ad request.
            AdRequest adRequest1 = new AdRequest.Builder().build();

            // Begin loading your interstitial.
            interstitial.loadAd(adRequest1);

            String title1 = getIntent().getStringExtra("title");


            title = (AutoCompleteTextView) rootView.findViewById(R.id.add_list_title);
            title.setAdapter(title_adapter);
            title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    displayInterstitial();
                }
            });
            if (title1 != null) {
                if (title1.equals("")) {
                    title.setHint("List Title");
                } else {
                    title.setText(title1);
                }
            }
            adView = (AdView) rootView.findViewById(R.id.adView);

            AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR)       // Emulator
                    .build();
            adView.loadAd(adRequest);

            String[] items_array = getResources().getStringArray(R.array.items_suggestion);
            adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_dropdown_item_1line, items_array);

            Button btnCreate = (Button) rootView.findViewById(R.id.addView);
            btnCreate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ++itemsCount;
                    final LinearLayout LL = new LinearLayout(AddListActivity.this);
                    LinearLayout parentLL = (LinearLayout) findViewById(R.id.parentLL);
                    final AutoCompleteTextView tv;
                    tv = new AutoCompleteTextView(AddListActivity.this);
                    final ImageButton del = new ImageButton(AddListActivity.this);
                    LayoutParams pLLlp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                    parentLL.setLayoutParams(pLLlp);
                    parentLL.setPadding(0, 0, 0, 90);
                    LayoutParams LLlp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                    LL.setLayoutParams(LLlp);
                    LL.setOrientation(LinearLayout.HORIZONTAL);

                    LayoutParams tvLP = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1);
                    tvLP.setMargins(0, 5, 0, 0);
                    tv.setLayoutParams(tvLP);
                    tv.setTextAppearance(AddListActivity.this, android.R.attr.textAppearanceLarge);
                    tv.setId(itemsCount);
                    tv.setVerticalScrollBarEnabled(true);
                    tv.setAdapter(adapter);
                    tv.setHint("New Item");
                    tv.setSingleLine(true);
                    tv.setImeActionLabel("Next", EditorInfo.IME_ACTION_NEXT);
                    tv.setImeOptions(EditorInfo.IME_ACTION_NEXT);
                    tv.requestFocus();
                    tv.setTypeface(Typeface.SERIF);

                    //Delete button
                    del.setId(400 + itemsCount);
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
                        }
                    });
                    LL.addView(tv);
                    LL.addView(del);
                    LL.setVerticalScrollBarEnabled(true);
                    parentLL.addView(LL);

                    /*
                    ++itemsCount;
                    LL = (LinearLayout) findViewById(R.id.llMain);

                    LayoutParams lpView = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                    lpView.setMargins(0, 5, 0, 0);
                    tv = new AutoCompleteTextView(new ContextThemeWrapper(AddListActivity.this, R.style.AutoCompleteTextViewAppTheme));
                    tv.setLayoutParams(lpView);
                    tv.setId(itemsCount);
                    tv.setVerticalScrollBarEnabled(true);
                    tv.setAdapter(adapter);
                    tv.requestFocus();
                    tv.setHint("Item " + itemsCount);
                    tv.setSingleLine(true);
                    tv.setImeActionLabel("Next", EditorInfo.IME_ACTION_NEXT);
                    tv.setImeOptions(EditorInfo.IME_ACTION_NEXT);
                    tv.setTypeface(Typeface.SERIF);
                    LL.addView(tv);
                    LL.setVerticalScrollBarEnabled(true);
                    */
                }
            });

            final LinearLayout addll = (LinearLayout) rootView.findViewById(R.id.add_alarm_layout);
            final TextView time_but = (TextView) rootView.findViewById(R.id.add_time);
            final TextView date_but = (TextView) rootView.findViewById(R.id.add_date);
            final Button newRem = (Button) rootView.findViewById(R.id.new_rem);
            ImageButton close_rem = (ImageButton) rootView.findViewById(R.id.close_rem);


            newRem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    addll.setVisibility(View.VISIBLE);
                    calDate = Calendar.getInstance();
                    calTime = Calendar.getInstance();
                    calNow = Calendar.getInstance();
                    calSet = Calendar.getInstance();
                    newRem.setVisibility(View.INVISIBLE);
                    int day = calDate.get(Calendar.DAY_OF_MONTH);
                    int year = calDate.get(Calendar.YEAR);
                    int month = calDate.get(Calendar.MONTH);
                    int hour = calDate.get(Calendar.HOUR_OF_DAY);
                    int min = calDate.get(Calendar.MINUTE);
                    time_but.setText(new StringBuilder().append(pad(hour))
                            .append(":").append(pad(min)));
                    date_but.setText(new StringBuilder().append(pad(day))
                            .append("/").append(pad(month + 1)).append("/").append(pad(year)));
                    isRemSet = true;
                }
            });

            close_rem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addll.setVisibility(View.GONE);
                    newRem.setVisibility(View.VISIBLE);
                    calDate.clear();
                    calTime.clear();
                    calNow.clear();
                    calSet.clear();
                    isRemSet = false;

                }
            });


            final DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                    calDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    calDate.set(Calendar.YEAR, year);
                    calDate.set(Calendar.MONTH, monthOfYear);
                    date_but.setText(new StringBuilder().append(pad(dayOfMonth))
                            .append("/").append(pad(monthOfYear + 1)).append("/").append(pad(year)));

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
                    time_but.setText(new StringBuilder().append(pad(hourOfDay))
                            .append(":").append(pad(minute)));
                }
            };
            date_but.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int yr = calDate.get(Calendar.YEAR);
                    int month = calDate.get(Calendar.MONTH);
                    int day = calDate.get(Calendar.DAY_OF_MONTH);

                    mDatePickerDialog = new DatePickerDialog(AddListActivity.this,
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
                    mTimePickerDialog = new TimePickerDialog(AddListActivity.this,
                            AlertDialog.THEME_HOLO_DARK, time,
                            hour,
                            min,
                            true);
                    mTimePickerDialog.setTitle("Set Reminder Time");
                    mTimePickerDialog.show();
                }
            });
            return rootView;
        }

        @Override
        public void onConfigurationChanged(Configuration newConfig) {
            super.onConfigurationChanged(newConfig);
            AdRequest adRequest;
            adRequest = new AdRequest.Builder().build();

            adView.destroy();
            adView = new AdView(getActivity());
            adView.setAdUnitId(getString(R.string.adMobId));
            adView.setAdSize(AdSize.SMART_BANNER);
            adView.loadAd(adRequest);

            LinearLayout contentFrame = (LinearLayout) findViewById(R.id.adLayout);
            contentFrame.addView(adView);
        }
    }
}