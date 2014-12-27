package com.shwavan.listsketcher;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;


/**
 * Created by GANGESHWAR on one/3/14.
 */
public class AppRater {
    private final static String APP_TITLE = "ListSketcher";
    private final static String APP_P_NAME = "com.shwavan.listsketcher";

    private final static int DAYS_UNTIL_PROMPT = 3;
    private final static int LAUNCHES_UNTIL_PROMPT = 7;

    public static void app_launched(Context mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences("apprater", 0);
        if (prefs.getBoolean("dontshowagain", false)) {
            return;
        }

        SharedPreferences.Editor editor = prefs.edit();

        // Increment launch counter
        long launch_count = prefs.getLong("launch_count", 0) + 1;
        editor.putLong("launch_count", launch_count);

        // Get date of first launch
        Long date_firstLaunch = prefs.getLong("date_firstlaunch", 0);
        if (date_firstLaunch == 0) {
            date_firstLaunch = System.currentTimeMillis();
            editor.putLong("date_firstlaunch", date_firstLaunch);
        }

        // Wait at least n days before opening
        if (launch_count >= LAUNCHES_UNTIL_PROMPT) {
            if (System.currentTimeMillis() >= date_firstLaunch +
                    (DAYS_UNTIL_PROMPT * 24 * 60 * 60 * 1000)) {
                showRateDialog(mContext, editor);
            }
        }
        editor.apply();
    }

    public static void showRateDialog(final Context mContext, final SharedPreferences.Editor editor) {

/*
        final Dialog dialog = new Dialog(mContext);
        dialog.setTitle("Rate " + APP_TITLE +"!");

        LinearLayout ll = new LinearLayout(mContext);
        ll.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(280,200);
        ll.setLayoutParams(lp);

        TextView tv = new TextView(mContext);
        tv.setText("If you enjoy using " + APP_TITLE + ", please take a moment to rate it. Thanks for your support!");
        tv.setWidth(240);
        tv.setPadding(4, 0, 4, 10);
        ll.addView(tv);
        LinearLayout llb = new LinearLayout(mContext);
        llb.setOrientation(LinearLayout.HORIZONTAL);
        Button b1 = new Button(mContext);
        b1.setText("Rate " + APP_TITLE+"!");
        b1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + APP_P_NAME)));
                dialog.dismiss();
            }
        });
        llb.addView(b1);
        Button b2 = new Button(mContext);
        b2.setText("Remind me later!");
        b2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        llb.addView(b2);

        Button b3 = new Button(mContext);
        b3.setText("No, thanks!");
        b3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (editor != null) {
                    editor.putBoolean("dontshowagain", true);
                    editor.commit();
                }
                dialog.dismiss();
            }
        });
        llb.addView(b3);
        ll.addView(llb);


        dialog.setContentView(ll);
        dialog.show();
*/

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext, AlertDialog.THEME_HOLO_LIGHT);
        // Setting Dialog Title
        alertDialog.setTitle("Rate " + APP_TITLE + "!");
        // Setting Dialog Message
        alertDialog.setMessage("If you enjoy using " + APP_TITLE + ", please take a moment to rate it. Thanks for your support!");
        // Setting Icon to Dialog
        alertDialog.setIcon(R.drawable.ic_rate);
        // Setting Negative "Yes" Button
        alertDialog.setPositiveButton("Rate " + APP_TITLE + "!", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + APP_P_NAME)));
                dialog.dismiss();

            }
        });
        // Setting Positive "NO" Button
        alertDialog.setNegativeButton("Later!", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to invoke NO event
                return;
            }
        });
        alertDialog.setNeutralButton("No, Thanks",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (editor != null) {
                    editor.putBoolean("dontshowagain", true);
                    editor.commit();
                }
                dialog.dismiss();
            }
        });
        // Showing Alert Message
        alertDialog.show();
    }

}