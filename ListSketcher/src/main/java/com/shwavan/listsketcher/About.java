package com.shwavan.listsketcher;

import android.app.Fragment;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


public class About extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_about, container, false);
        TextView textView = (TextView) rootView.findViewById(R.id.website_link);
        textView.setClickable(true);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        String text = "<a href='http://www.listsketcher.com'>listsketcher.com</a>";
        textView.setText(Html.fromHtml(text));
        PackageManager manager = getActivity().getPackageManager();
        PackageInfo info;
        TextView versionName = (TextView) rootView.findViewById(R.id.version);
        try {
            assert manager != null;

            info = manager.getPackageInfo(getActivity().getPackageName(), 0);
            String version = info.versionName;
            versionName.setText("Version " + version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        TextView shwavan = (TextView) rootView.findViewById(R.id.shwavan);
        shwavan.setClickable(true);
        shwavan.setMovementMethod(LinkMovementMethod.getInstance());
        String shwavan_link = "&#169; 2014 <a href='http://www.shwavan.com'>Shwavan Inc.</a>";
        shwavan.setText(Html.fromHtml(shwavan_link));

        Button changeLog = (Button) rootView.findViewById(R.id.changelog);
        changeLog.setMovementMethod(LinkMovementMethod.getInstance());
        changeLog.setClickable(true);
        changeLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangeLogDialog _ChangelogDialog = new ChangeLogDialog(getActivity());
                _ChangelogDialog.show();
            }
        });
        return rootView;
    }

}