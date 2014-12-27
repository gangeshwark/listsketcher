package com.shwavan.listsketcher.auth;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.google.android.gms.common.Scopes;
import com.shwavan.listsketcher.lists.SQLiteListsHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;


/**
 * Created by Gokul on 22/one/14.
 */
public class SessionManager {
    // User name (make variable public to access from outside)
    public static final String KEY_NAME = "name";
    // Email address (make variable public to access from outside)
    public static final String KEY_EMAIL = "email";
    public static final String KEY_SIGNED_IN = "logged_in_on";
    public static final String KEY_GPLUSPROF = "profile_url";
    public static final String KEY_PROFPIC = "profile_pic_url";
    public static final String AUTH_SCOPES[] = {
            Scopes.PLUS_LOGIN,
            Scopes.PLUS_ME,
            "https://www.googleapis.com/auth/userinfo.email",
            "https://www.googleapis.com/auth/developerssite"};
    static final String KEY_TOKEN_TYPE;

    static {
        StringBuilder sb = new StringBuilder();
        sb.append("oauth2:");
        for (String scope : AUTH_SCOPES) {
            sb.append(scope);
            sb.append(" ");
        }
        KEY_TOKEN_TYPE = sb.toString();
    }

    // Sharedpref file name
    private static final String PREF_NAME = "ListSketcherLoginPref";
    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsUserLoggedIn";
    private static final String FIRST_USE = "FirstUse";
    private static final String TAG = "LoginActivity";
    private static final String KEY_CHOSEN_ACCOUNT = "chosen_account";
    private static final String KEY_AUTH_TOKEN = "auth_token";
    private static final String KEY_PLUS_PROFILE_ID = "plus_profile_id";
    private static final String KEY_LOCATION = "location";
    private static final String KEY_SYNCED = "synced";
    // Shared Preferences
    SharedPreferences pref;
    // Editor for Shared preferences
    Editor editor;
    // Context
    Context _context;
    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Constructor
    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }


    /**
     * Create login session
     */
    public void createLoginSession(String name, String email, String pic_url, String prof_url, String prof_id, String location) {
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);
        // Storing name in pref
        editor.putString(KEY_NAME, name);
        // Storing email in pref
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_PROFPIC, pic_url);
        editor.putString(KEY_GPLUSPROF, prof_url);
        editor.putString(KEY_PLUS_PROFILE_ID, prof_id);
        editor.putString(KEY_LOCATION, location);
        String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
        editor.putString(KEY_SIGNED_IN, date);

        // commit changes
        editor.commit();
    }

    public Editor getEditor() {
        return editor;
    }

    /**
     * Check login method wil check user login status
     * If false it will redirect user to login page
     * Else won't do anything
     */
    public void checkLogin() {
        // Check login status
        if (!this.isLoggedIn()) {
            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(_context, LoginPref.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            _context.startActivity(i);
        }

    }

    public String getName() {
        return pref.getString(KEY_NAME, "User");
    }

    public String getKeyProfpic() {
        return pref.getString(KEY_PROFPIC, null);
    }

    public String getKeyEmail() {
        return pref.getString(KEY_EMAIL, "no_email");
    }

    public String getKeyPlusProfileId() {
        return pref.getString(KEY_PLUS_PROFILE_ID, "no_id");
    }


    /**
     * Get stored session data
     */
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_NAME, pref.getString(KEY_NAME, null));

        // user email id
        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));
        user.put(KEY_GPLUSPROF, pref.getString(KEY_GPLUSPROF, null));
        user.put(KEY_PROFPIC, pref.getString(KEY_GPLUSPROF, null));
        user.put(KEY_SIGNED_IN, pref.getString(KEY_SIGNED_IN, null));


        // return user
        return user;

    }

    /**
     * Clear session details
     */
    public void logoutUser() {

        // Clearing all data from Shared Preferences
        final SharedPreferences Setprefs = _context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        final SharedPreferences.Editor Settingseditor = Setprefs.edit();
        editor.clear().commit();
        Settingseditor.clear().commit();

        SQLiteListsHelper helper = new SQLiteListsHelper(this._context);
        helper.clearDB();

        // After logout redirect user to Login Activity
        Intent i = new Intent(_context, LoginPref.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        _context.startActivity(i);

    }


    /**
     * Quick check for login
     * *
     */
    // Get Login State
    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false);
    }

    public boolean isFirst() {
        return pref.getBoolean(FIRST_USE, true);
    }

    public void setNotFirst() {
        editor.putBoolean(FIRST_USE, false).commit();
    }

    public void setKeySynced() {
        editor.putBoolean(KEY_SYNCED, true).commit();

    }

    public boolean isSynced() {
        return pref.getBoolean(KEY_SYNCED, false);
    }

}