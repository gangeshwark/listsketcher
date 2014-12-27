package com.shwavan.listsketcher;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.androidhive.imagefromurl.ImageLoader;
import com.parse.ParseUser;
import com.shwavan.listsketcher.adapter.CircularImageView;
import com.shwavan.listsketcher.adapter.NavDrawerListAdapter;
import com.shwavan.listsketcher.auth.PlusBaseActivity;
import com.shwavan.listsketcher.auth.SessionManager;
import com.shwavan.listsketcher.lists.SQLiteListsHelper;
import com.shwavan.listsketcher.lists.ViewLists;
import com.shwavan.listsketcher.model.NavDrawerItem;

import java.util.ArrayList;


public class MainActivity extends PlusBaseActivity {
    private final static String APP_P_NAME = "com.shwavan.listsketcher";
    SessionManager session;
    SQLiteListsHelper listsHelper;
    LinearLayout DrawerLL;
    // Variables
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ArrayList<NavDrawerItem> navDrawerItems;
    private ActionBarDrawerToggle mDrawerToggle;
    private NavDrawerListAdapter mMenuAdapter;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] navMenuTitles;
    private TypedArray navMenuIcons;
    private CircularImageView userImage;
    private TextView userName;

    @Override
    protected void onPlusClientRevokeAccess() {

    }

    @Override
    protected void onPlusClientSignIn() {

    }

    @Override
    protected void onPlusClientSignOut() {

    }

    @Override
    protected void updateConnectButtonState() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppRater.app_launched(this);
        listsHelper = new SQLiteListsHelper(this);

        session = new SessionManager(this);
        if (!session.isLoggedIn()) {
            session.checkLogin();
            finish();
        }
        if (ParseUser.getCurrentUser() == null && session.isLoggedIn()) {
            parseLogin();
        }
        DrawerLL = (LinearLayout) findViewById(R.id.drawer);
        userImage = (CircularImageView) findViewById(R.id.user_img);

        if (session.getKeyProfpic() != null) {
            String Url = session.getKeyProfpic();
            String imageURL = Url.substring(0, Url.length() - 6) + "?sz=200";
            //String imageURL = Url.substring(0,Url.length()-6) ;
            // ImageLoader class instance
            ImageLoader imgLoader = new ImageLoader(this);
            // whenever you want to load an image from url
            // call DisplayImage function
            // url - image url to load
            // loader - loader image, will be displayed before getting image
            // image - ImageView
            int default_img = R.drawable.ic_gravatar;
            imgLoader.DisplayImage(imageURL, default_img, userImage);
        }
        DrawerLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                return;
            }
        });
        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                return;
            }
        });

        userName = (TextView) findViewById(R.id.user_name);
        userName.setText(session.getName());
        TextView userEmail = (TextView) findViewById(R.id.user_email);
        userEmail.setText(session.getKeyEmail());

        // Get the title
        mTitle = mDrawerTitle = getTitle();
        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);

        // Link the content
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navDrawerItems = new ArrayList<NavDrawerItem>();
        navMenuIcons = getResources()
                .obtainTypedArray(R.array.nav_drawer_icons);
        // Adding nav drawer items to array

/*        // Home
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -one)));*/
        // Lists
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1)));
        // About
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1)));
        // Rate
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(3, -1)));
        // Send feedback
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[4], navMenuIcons.getResourceId(4, -1)));
        // Share this app
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[5], navMenuIcons.getResourceId(5, -1)));
        // Sign out
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[6], navMenuIcons.getResourceId(6, -1)));

        // Recycle the typed array
        navMenuIcons.recycle();

        mDrawerList = (ListView) findViewById(R.id.list_slidermenu);
        mMenuAdapter = new NavDrawerListAdapter(getApplicationContext(),
                navDrawerItems);

        mDrawerList.setAdapter(mMenuAdapter);

        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        if (mDrawerLayout != null) {
            // Set a custom shadow that overlays the main content when the drawer opens
            mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
            // Enable ActionBar app icon to behave as action to toggle nav drawer
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            // ActionBarDrawerToggle ties together the proper interactions
            // between the sliding drawer and the action bar app icon
            mDrawerToggle = new ActionBarDrawerToggle(
                    this,
                    mDrawerLayout,
                    R.drawable.ic_drawer, //nav menu toggle icon
                    R.string.app_name, // nav drawer open - description for accessibility
                    R.string.app_name) // nav drawer close - description for accessibility
            {

                public void onDrawerClosed(View view) {
                    getActionBar().setTitle(mTitle);
                    // calling onPrepareOptionsMenu() to show action bar icons
                    invalidateOptionsMenu();
                }

                public void onDrawerOpened(View drawerView) {
                    getActionBar().setTitle(mDrawerTitle);
                    // calling onPrepareOptionsMenu() to hide action bar icons
                    invalidateOptionsMenu();
                }

            };

            mDrawerLayout.openDrawer(DrawerLL);
            mDrawerLayout.setDrawerListener(mDrawerToggle);
        }

        if (savedInstanceState == null) {
            selectItem(0);
            if (mDrawerLayout != null) {
                if(session.isFirst()){
                    mDrawerLayout.openDrawer(DrawerLL);
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (mDrawerLayout != null) {
                if (mDrawerLayout.isDrawerOpen(DrawerLL)) {
                    mDrawerLayout.closeDrawer(DrawerLL);
                } else {
                    mDrawerLayout.openDrawer(DrawerLL);
                }
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void selectItem(int position) {
        Fragment fragment = null;
        String TAG = "LISTS";
        switch (position) {
           /* case :
                fragment = new ViewHome();
                TAG = "HOME";
                break;
                */
            case 0:
                fragment = new ViewLists();
                TAG = "LISTS";
                break;

            case 1:
                fragment = new About();
                TAG = "ABOUT";
                break;
            case 2:
                if (mDrawerLayout != null) {
                    if (mDrawerLayout.isDrawerOpen(DrawerLL)) {
                        mDrawerLayout.closeDrawer(DrawerLL);
                    }
                }
                AppRater.showRateDialog(this,session.getEditor());
                break;
            case 3:
                if (mDrawerLayout != null) {
                    if (mDrawerLayout.isDrawerOpen(DrawerLL)) {
                        mDrawerLayout.closeDrawer(DrawerLL);
                    }
                }
                /* Create the Intent */
                final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
                /* Fill it with Data */
                emailIntent.setType("plain/text");
                emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"shwavan.inc@gmail.com"});
                emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Feedback");
                emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "App Name: ListSketcher \nApp Version: 3.1 \nAndroid OS Version: \nFeedback: ");
                /* Send it off to the Activity-Chooser */
                startActivity(Intent.createChooser(emailIntent, "Send Feedback"));
                break;

            case 4:
                if (mDrawerLayout != null) {
                    if (mDrawerLayout.isDrawerOpen(DrawerLL)) {
                        mDrawerLayout.closeDrawer(DrawerLL);
                    }
                }
                shareIt();
                break;
            case 5:
                if (mDrawerLayout != null) {
                    if (mDrawerLayout.isDrawerOpen(DrawerLL)) {
                        mDrawerLayout.closeDrawer(DrawerLL);
                    }
                }
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
                // Setting Dialog Title
                alertDialog.setTitle("Sign Out?");
                // Setting Dialog Message
                alertDialog.setMessage("If you sign out, you can't use ListSketcher. Continue?");
                // Setting Icon to Dialog
                alertDialog.setIcon(R.drawable.ic_sign_out);
                // Setting Negative "Yes" Button
                alertDialog.setNegativeButton("Yeah, Sign Out!", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        signOut();
                        session.logoutUser();
                        finish();
                    }
                });
                // Setting Positive "NO" Button
                alertDialog.setPositiveButton("No, I will stay!", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to invoke NO event
                        return;
                    }
                });
                // Showing Alert Message
                alertDialog.show();
                break;
            default:
                break;
        }
        if (fragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_container, fragment, TAG).addToBackStack(null).commit();
            // update selected item and title, then close the drawer
            mDrawerList.setItemChecked(position, true);
            mDrawerList.setSelection(position);
            setTitle(navMenuTitles[position + 1]);
            mDrawerLayout.closeDrawer(DrawerLL);
        } else {
            // error in creating fragment
            Log.e("MainActivity", "Error in creating fragment");
            mDrawerList.setSelection(0);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (mDrawerLayout != null) {
            mDrawerToggle.syncState();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (mDrawerLayout != null) {
            // Pass any configuration change to the drawer toggles
            mDrawerToggle.onConfigurationChanged(newConfig);
            mDrawerLayout.closeDrawer(DrawerLL);
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    private void shareIt() {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBody = "Hey, I'm using ListSketcher to remember and organise my day-to-day Lists! It's a cool app that lets you manage your day perfectly!\n\n" +
                "ListSketcher is easy to use and let's you share your list with your friends!\n\n" +
                "Get ListSketcher : " + "https://play.google.com/store/apps/details?id=" + APP_P_NAME + "\n\n";
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "ListSketcher");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Share ListSketcher via..."));
    }

    public void onBackPressed() {
        FragmentManager fm = getFragmentManager();
        Fragment myFragment = getFragmentManager().findFragmentByTag("LISTS");
        if (fm.getBackStackEntryCount() == 1) {
            finish();
        } else {
            if (myFragment.isVisible()) {
                finish();
            } else {
                fm.popBackStack();
                setTitle("My Lists");
            }
        }
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            selectItem(position);
        }
    }
}