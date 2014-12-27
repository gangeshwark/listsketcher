package com.shwavan.listsketcher;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;

import com.shwavan.listsketcher.lists.ListsClass;
import com.shwavan.listsketcher.lists.ListsDetailActivity;
import com.shwavan.listsketcher.lists.SQLiteListsHelper;

public class ReminderReceiver extends BroadcastReceiver {
    private static final String PREF_NAME = "SettingsPref";

    public ReminderReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        // When our Alaram time is triggered , this method will be excuted (onReceive)
        // We're invoking a service in this method which shows Notification to the User

        // Getting Notification Service

		/*
         * When the user taps the notification we have to show the Home Screen
		 * of our App, this job can be done with the help of the following
		 * Intent.
		 */

        SQLiteListsHelper listsHelper = new SQLiteListsHelper(context);
        SharedPreferences getAlarms = context.
                getSharedPreferences(PREF_NAME, 0);
        String alarms = getAlarms.getString("notifications_ringtone", "default ringtone");
        Uri uri = Uri.parse(alarms);

        long list_id = intent.getLongExtra("list_id", -1);
        Notification.Builder builder = new Notification.Builder(context);
        builder.setSmallIcon(R.drawable.ic_launcher)
                .setTicker("List Pending!")
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setSound(uri)
                .setLights(Color.RED, 0, 1)
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                .setDefaults(Notification.DEFAULT_ALL);
        Notification notification = builder.getNotification();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        int notifId = (int) (Math.random() * (double) 1000);
        ListsClass listsClass = listsHelper.getList(list_id);

        String MyNotificationTitle = "\'List\' Pending!";
        String MyNotificationText = "List Name: " + listsClass.getTitle();

        Intent myIntent = new Intent(context, ListsDetailActivity.class);
        myIntent.putExtra("NotifIdn", notifId);
        myIntent.putExtra("list_id", list_id);

        myIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Log.d("MyLog", "Just put int notifId = " + notifId);

        PendingIntent StartIntent = PendingIntent.getActivity(context, 0, myIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        //A PendingIntent will be fired when the notification is clicked. The FLAG_CANCEL_CURRENT flag cancels the pendingintent

        notification.setLatestEventInfo(context, MyNotificationTitle, MyNotificationText, StartIntent);

        notificationManager.notify(notifId, notification);
        //We are passing the notification to the NotificationManager with a unique id.
        listsHelper.updateRemToDone(list_id);
    }

}
