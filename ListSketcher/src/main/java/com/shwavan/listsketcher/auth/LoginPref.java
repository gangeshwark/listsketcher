package com.shwavan.listsketcher.auth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.plus.model.people.Person;
import com.shwavan.listsketcher.MainActivity;
import com.shwavan.listsketcher.R;


public class LoginPref extends PlusBaseActivity implements View.OnClickListener {
    private static final String TAG = "LoginActivity";
    SessionManager session;
    TextView description, title;
    private ProgressDialog mConnectionProgressDialog;

    @Override
    protected void onPlusClientRevokeAccess() {

    }

    @Override
    protected void onPlusClientSignIn() {
        mConnectionProgressDialog.dismiss();

        session = new SessionManager(this);
        if (getPlusClient().getCurrentPerson() != null) {
            String accountName = getPlusClient().getAccountName();
            Person currentPerson = getPlusClient().getCurrentPerson();
            String personName = currentPerson.getDisplayName();
            String personPhotoUrl = currentPerson.getImage().getUrl();
            String personGooglePlusProfile = currentPerson.getUrl();
            String personGooglePlusId = currentPerson.getId();
            String location = currentPerson.getCurrentLocation();
            session.createLoginSession(personName, accountName, personPhotoUrl, personGooglePlusProfile, personGooglePlusId, location);
        }

        Toast.makeText(this, "Welcome to ListSketcher!", Toast.LENGTH_LONG).show();
        Intent i = new Intent(this, MainActivity.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        startActivity(i);
        finish();

    }


    @Override
    protected void onPlusClientSignOut() {

    }

    @Override
    protected void updateConnectButtonState() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inflate the layout for this fragment
        setContentView(R.layout.fragment_login_pref);
        Intent i = getIntent();

        // Progress bar to be displayed if the connection failure is not resolved.
        mConnectionProgressDialog = new ProgressDialog(this);
        mConnectionProgressDialog.setMessage("Signing in...");

        Button signInBut = (Button) findViewById(R.id.sign_in_button);
        Button signUpBut = (Button) findViewById(R.id.sign_up_button);
        signUpBut.setOnClickListener(this);
        signInBut.setOnClickListener(this);

        title = (TextView) findViewById(R.id.login_title);
        description = (TextView) findViewById(R.id.login_desc);
        title.setText("Connect Now! It's Free!");
        description.setText(Html.fromHtml(getString(R.string.login_desc)));
        signInBut.setVisibility(View.VISIBLE);
        signUpBut.setVisibility(View.GONE);

        getActionBar().hide();

    }

    @Override
    protected void onStart() {
        super.onStart();
    }


    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onConnected(Bundle bundle) {
        super.onConnected(bundle);

    }

    @Override
    public void onDisconnected() {

        Log.d(TAG, "disconnected");
    }


    /**
     * Called when a view has been clicked.
     *
     * @param view The view that was clicked.
     */
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.sign_in_button && !getPlusClient().isConnected()) {
            mConnectionProgressDialog.show();
            signIn();

        }
        if (view.getId() == R.id.sign_up_button && !getPlusClient().isConnected()) {
            mConnectionProgressDialog.show();
            signIn();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        super.onConnectionFailed(result);
        mConnectionProgressDialog.dismiss();


    }
}