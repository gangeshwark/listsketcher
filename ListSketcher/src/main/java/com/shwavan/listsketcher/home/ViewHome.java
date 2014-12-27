package com.shwavan.listsketcher.home;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidhive.imagefromurl.ImageLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.shwavan.listsketcher.About;
import com.shwavan.listsketcher.AppRater;
import com.shwavan.listsketcher.R;
import com.shwavan.listsketcher.auth.SessionManager;
import com.shwavan.listsketcher.lists.AddListActivity;
import com.shwavan.listsketcher.lists.ViewLists;


public class ViewHome extends Fragment {
    AdView adView;
    AdRequest adRequest;
    View rootView;

    public ViewHome() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        rootView = inflater.inflate(R.layout.fragment_view_home, container, false);
        final SessionManager session = new SessionManager(getActivity());
        TextView mStickyView = (TextView) rootView.findViewById(R.id.welText);
        mStickyView.setText("Howdy, " + session.getName() + "!");

        adView = (AdView) rootView.findViewById(R.id.adView);

        adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR)       // Emulator
                .build();

        adView.loadAd(adRequest);

        // Imageview to show
        ImageView image = (ImageView) rootView.findViewById(R.id.profPic);
        // Image url
        if (session.getKeyProfpic() != null) {
            String Url = session.getKeyProfpic();
            String imageURL = Url.substring(0, Url.length() - 6) + "?sz=512";
            //String imageURL = Url.substring(0,Url.length()-6) ;

            //String imageUrl = "http://api.androidhive.info/images/sample.jpg" ;
            // Loader image - will be shown before loading image
            int loader = R.drawable.ic_loading;
            // ImageLoader class instance
            ImageLoader imgLoader = new ImageLoader(getActivity());
            // whenever you want to load an image from url
            // call DisplayImage function
            // url - image url to load
            // loader - loader image, will be displayed before getting image
            // image - ImageView
            imgLoader.DisplayImage(imageURL, loader, image);
        }

        Button viewList, addList, about, rateApp;
        viewList = (Button) rootView.findViewById(R.id.viewListHome);
        addList = (Button) rootView.findViewById(R.id.addListHome);
        about = (Button) rootView.findViewById(R.id.aboutBut);
        rateApp = (Button) rootView.findViewById(R.id.rateBut);
        viewList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String TAG = "ABOUT";
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.frame_container, new ViewLists(), TAG).addToBackStack(null).commit();
            }
        });
        addList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), AddListActivity.class);
                startActivity(i);
            }
        });

        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String TAG = "ABOUT";
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.frame_container, new About(), TAG).addToBackStack(null).commit();

            }
        });
        rateApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppRater.showRateDialog(getActivity(),session.getEditor());
            }
        });

        return rootView;
    }

    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        adView.destroy();
        adView = new AdView(getActivity());
        adView.setAdUnitId(getString(R.string.adMobId));
        adView.setAdSize(AdSize.SMART_BANNER);
        adView.loadAd(adRequest);

        LinearLayout contentFrame = (LinearLayout) rootView.findViewById(R.id.adLayout);
        contentFrame.addView(adView);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.view_home, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_list:
                Intent i = new Intent(this.getActivity(), AddListActivity.class);
                startActivity(i);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}