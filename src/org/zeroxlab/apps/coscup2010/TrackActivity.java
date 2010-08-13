package org.zeroxlab.apps.coscup2010;

import org.zeroxlab.apps.coscup2010.Agenda.Tracks;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.net.Uri;
import android.text.Html;
import android.view.View;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.Button;

public class TrackActivity extends TabActivity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.track);

        final ImageButton btn_home = (ImageButton) findViewById(R.id.btn_home);
        btn_home.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(TrackActivity.this, LauncherActivity.class);
                    startActivity(intent);
                }
            });

        Intent intent = getIntent();
        Uri uri = intent.getData();

        Cursor cursor = getContentResolver()
            .query(uri, new String[] {Tracks._ID, Tracks.TITLE, Tracks.SUMMARY},
                   null, null, null);

        cursor.moveToFirst();

        TextView title = (TextView)findViewById(R.id.action_bar_title);
        title.setText(cursor.getString(cursor.getColumnIndex(Tracks.TITLE)));

        Button tab;
        TabHost tabHost = getTabHost();
        TabHost.TabSpec spec;

        tab = new TabView(this);
        tab.setText("Summary");
        TextView summary = (TextView)findViewById(R.id.track_summary);
        summary.setText(Html.fromHtml(cursor.getString(cursor.getColumnIndex(Tracks.SUMMARY))));
        spec = tabHost.newTabSpec("summary").setIndicator(tab).setContent(R.id.track_summary);
        tabHost.addTab(spec);

        tab = new TabView(this);
        tab.setText("Sessions");
        intent = new Intent(Intent.ACTION_VIEW,
                            Uri.withAppendedPath(uri, "sessions"),
                            this,
                            SessionListActivity.class);
        spec = tabHost.newTabSpec("sessions").setIndicator(tab).setContent(intent);
        tabHost.addTab(spec);

        tabHost.setCurrentTab(1);

        TypedArray colors = getResources().obtainTypedArray(R.array.track_colors);
        View view = findViewById(R.id.action_bar_home);
        view.setBackgroundColor(Color.parseColor(colors.getString(cursor.getInt(cursor.getColumnIndex(Tracks._ID)))));

        cursor.close();
    }
}
