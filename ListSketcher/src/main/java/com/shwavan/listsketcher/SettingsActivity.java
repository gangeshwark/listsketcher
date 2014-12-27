package com.shwavan.listsketcher;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MenuItem;

import com.shwavan.listsketcher.auth.SessionManager;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p/>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends PreferenceActivity {
    private static final String PREF_NAME = "SettingsPref";
    /**
     * Determines whether to always show the simplified settings UI, where
     * settings are presented in a single list. When false, settings are shown
     * as a master/detail two-pane view on tablets. When true, a single pane is
     * shown on tablets.
     */
    private static final boolean ALWAYS_SIMPLE_PREFS = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupActionBar();
        getPreferenceManager().setSharedPreferencesName(PREF_NAME);
        addPreferencesFromResource(R.xml.settings);
        final SessionManager sessionManager = new SessionManager(this);
        final SharedPreferences prefs = this.getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        final SharedPreferences.Editor editor = prefs.edit();

        final EditTextPreference editTextPreference = (EditTextPreference) findPreference("acra.user.email");
        String email = prefs.getString("acra.user.email",sessionManager.getKeyEmail());
        if( editTextPreference!=null) {
            if (email.equals(sessionManager.getKeyEmail())) {
                editTextPreference.setText(sessionManager.getKeyEmail());
            } else {
                editTextPreference.setText(email);
            }
        }

        final ListPreference listPreference = (ListPreference) findPreference("sync_frequency");

        if (listPreference != null) {
            String val = prefs.getString("sync_frequency","1");
            listPreference.setValue(val);
            switch (Integer.parseInt(val)) {
                case 1:
                    listPreference.setSummary("Everday");
                    break;
                case 2:
                    listPreference.setSummary("Once in 2 days");
                    break;
                case 3:
                    listPreference.setSummary("Once in 3 days");
                    break;
            }



            listPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    editor.putString("sync_frequency", newValue.toString()).commit();
                    Log.e("SyncFreq", prefs.getString("sync_frequency", "-1"));
                    listPreference.setValue(newValue.toString());
                    switch (Integer.parseInt(newValue.toString())) {
                        case 1:
                            listPreference.setSummary("Everday");
                            break;
                        case 2:
                            listPreference.setSummary("Once in 2 days");
                            break;
                        case 3:
                            listPreference.setSummary("Once in 3 days");
                            break;
                    }
                    return false;
                }
            });
        }


        Preference manualSync = findPreference("manual_sync");
        if (manualSync != null) {
            manualSync.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent sync = new Intent(SettingsActivity.this, SyncService.class);
                    startService(sync);

                    return false;
                }
            });
        }

    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupActionBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // Show the Up button in the action bar.
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            // TODO: If Settings has multiple levels, Up should navigate up
            // that hierarchy.
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
