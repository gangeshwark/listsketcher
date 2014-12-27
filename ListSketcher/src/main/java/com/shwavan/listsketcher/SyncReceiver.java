package com.shwavan.listsketcher;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class SyncReceiver extends BroadcastReceiver {
    private static final String PREF_NAME = "SettingsPref";

    public SyncReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, 0);
        if (sharedPreferences.getBoolean("sync", true)) {
            Intent sync = new Intent(context, ScheduledSyncService.class);
            context.startService(sync);
        }
    }

}
