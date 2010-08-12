package org.zeroxlab.apps.coscup2010;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.Button;

public class TrackActivity extends TabActivity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.track);

        Button tab;
        TabHost tabHost = getTabHost();
        TabHost.TabSpec spec;
        Intent intent;

        tab = new TabView(this);
        tab.setText("Home");
        intent = new Intent().setClass(this, LauncherActivity.class);
        spec = tabHost.newTabSpec("Home").setIndicator(tab).setContent(intent);
        tabHost.addTab(spec);

        tab = new TabView(this);
        tab.setText("Tracks");
        intent = new Intent().setClass(this, TrackListActivity.class);
        spec = tabHost.newTabSpec("tracks").setIndicator(tab).setContent(intent);
        tabHost.addTab(spec);

        tab = new TabView(this);
        tab.setText("Sessions");
        intent = new Intent().setClass(this, SessionListActivity.class);
        spec = tabHost.newTabSpec("sessions").setIndicator(tab).setContent(intent);
        tabHost.addTab(spec);

        tabHost.setCurrentTab(2);
    }
}
