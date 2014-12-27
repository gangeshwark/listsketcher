package com.shwavan.listsketcher;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.PushService;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;
import org.acra.sender.HttpSender;

@ReportsCrashes(
        sharedPreferencesName = "SettingsPref" ,
        sharedPreferencesMode = 0 ,
        formKey = "",
        formUri = "http://api.listsketcher.com/crashreport.php",
        httpMethod = HttpSender.Method.POST,
        formUriBasicAuthLogin = "listsketcher",
        formUriBasicAuthPassword = "listsketcher",
        // Your usual ACRA configuration
        mode = ReportingInteractionMode.NOTIFICATION,
        resToastText = R.string.crash_toast_text, // optional, displayed as soon as the crash occurs, before collecting data which can take a few seconds
        resNotifTickerText = R.string.crash_notif_ticker_text,
        resNotifTitle = R.string.crash_notif_title,
        resNotifText = R.string.crash_notif_text,
        resNotifIcon = R.drawable.ic_launcher, // optional. default is a warning sign
        resDialogText = R.string.crash_dialog_text,
        resDialogIcon = R.drawable.ic_launcher, //optional. default is a warning sign
        resDialogTitle = R.string.crash_dialog_title, // optional. default is your application name
        resDialogCommentPrompt = R.string.crash_dialog_comment_prompt, // optional. when defined, adds a user text field input with this text resource as a label
        resDialogOkToast = R.string.crash_dialog_ok_toast // optional. displays a Toast message when the user accepts to send a report.

)
public class ListSketcher extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // The following line triggers the initialization of ACRA
        ACRA.init(this);
        Parse.initialize(this, "76aRATozBDlVtquGgSIRWSAnVu0XvvrkR6nYRxTK", "nnEzzujYBe1cg7ioSvdj81paev9zBDGuK5QGs6kA");
        PushService.setDefaultPushCallback(this, MainActivity.class);
        ParseInstallation.getCurrentInstallation().saveInBackground();
    }
}