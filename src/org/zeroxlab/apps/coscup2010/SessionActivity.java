package org.zeroxlab.apps.coscup2010;

import org.zeroxlab.apps.coscup2010.Agenda.Sessions;
import org.zeroxlab.apps.coscup2010.Agenda.Speakers;
import org.zeroxlab.apps.coscup2010.Agenda.Tracks;

import android.app.TabActivity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TabHost.TabSpec;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

public class SessionActivity extends TabActivity {

    int mId = -1;
    int mStarred = 0;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.session);

        final ImageButton btn_home = (ImageButton) findViewById(R.id.btn_home);
        btn_home.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(SessionActivity.this, LauncherActivity.class);
                    startActivity(intent);
                }
            });
        final ImageButton btn_search = (ImageButton) findViewById(R.id.btn_search);
        btn_search.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    SessionActivity.this.onSearchRequested();
                }
            });

        Intent intent = getIntent();
        Uri uri = intent.getData();

        String[] columns = new String[] {
            Sessions._ID,
            Sessions.TITLE,
            "strftime('%H:%M'," + Sessions.START + ",'localtime')",
            "strftime('%H:%M'," + Sessions.END + ",'localtime')",
            Sessions.ROOM,
            Sessions.SUMMARY,
            Sessions.TRACK,
            Sessions.STARRED
        };

        Cursor cursor = getContentResolver().query(uri, columns, null, null, null);

        cursor.moveToFirst();

        mId = cursor.getInt(cursor.getColumnIndex(Sessions._ID));
        mStarred = cursor.getInt(cursor.getColumnIndex(Sessions.STARRED));

        String[] track_columns = new String[] {
            Tracks._ID,
            Tracks.TITLE
        };

        Cursor track = getContentResolver().query(Tracks.CONTENT_URI, track_columns,
                                                  Tracks.UUID + "=?",
                                                  new String[] {cursor.getString(cursor.getColumnIndex(Sessions.TRACK))}, null);

        track.moveToFirst();

        TextView top_title = (TextView)findViewById(R.id.action_bar_title);
        top_title.setText(track.getString(track.getColumnIndex(Tracks.TITLE)));

        TextView title = (TextView)findViewById(R.id.session_title);
        title.setText(cursor.getString(cursor.getColumnIndex(Sessions.TITLE)));

        TypedArray colors = getResources().obtainTypedArray(R.array.track_colors);
        View view = findViewById(R.id.action_bar_home);
        view.setBackgroundColor(Color.parseColor(colors.getString(track.getInt(track.getColumnIndex(Tracks._ID)))));

        track.close();

        TextView room = (TextView)findViewById(R.id.session_time_room);
        TypedArray rooms = getResources().obtainTypedArray(R.array.rooms);
        String room_string = rooms.getString(cursor.getInt(4));
        room.setText(cursor.getString(2) + " - " + cursor.getString(3) + " " + room_string);

        Button tab;
        TabHost tabHost = getTabHost();
        TabHost.TabSpec spec;

        tab = new TabView(this);
        tab.setText("Summary");
        TextView summary = (TextView)findViewById(R.id.session_summary);
        summary.setText(Html.fromHtml(cursor.getString(cursor.getColumnIndex(Sessions.SUMMARY))));
        spec = tabHost.newTabSpec("summary").setIndicator(tab).setContent(R.id.session_summary);
        tabHost.addTab(spec);

        tab = new TabView(this);
        tab.setText("Notes");
        spec = tabHost.newTabSpec("notes").setIndicator(tab).setContent(R.id.noimpl);
        tabHost.addTab(spec);

        tabHost.setCurrentTab(0);

        cursor.close();

        columns = new String[] {
            Speakers.NAME,
            Speakers.BIOGRAPHY
        };
        cursor = getContentResolver().query(Uri.withAppendedPath(uri, "speakers"),
                                            columns, null, null, null);
        StringBuilder summary_string = new StringBuilder(summary.getText());
        while (cursor.moveToNext()) {
            // FIXME, L10N
            if (cursor.isFirst())
                summary_string.append("<p><h3>Speakers</p></h3>");
            summary_string.append("<p><h3>");
            summary_string.append(cursor.getString(cursor.getColumnIndex(Speakers.NAME)));
            summary_string.append("</h3></p><p>");
            summary_string.append(cursor.getString(cursor.getColumnIndex(Speakers.BIOGRAPHY)));
            summary_string.append("</p>");
        }
        summary.setText(Html.fromHtml(summary_string.toString()));
        cursor.close();

        final Button btn_star = (Button)findViewById(R.id.btn_star);
        if (mStarred != 0)
            btn_star.setBackgroundResource(R.drawable.ic_star_active);
        else
            btn_star.setBackgroundResource(R.drawable.ic_star_inactive);
        btn_star.setFocusable(true);
        btn_star.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    toggleStarred();
                }
            });
    }

    private void toggleStarred() {
        ContentValues values = new ContentValues();
        if (mStarred == 0)
            mStarred = 1;
        else
            mStarred = 0;
        values.put(Sessions.STARRED, mStarred);
        getContentResolver()
            .update(Uri.withAppendedPath(Sessions.CONTENT_URI, "starred"),
                    values, Sessions._ID + "=?",
                    new String[] { Integer.toString(mId) });
        final Button btn_star = (Button)findViewById(R.id.btn_star);
        if (mStarred != 0)
            btn_star.setBackgroundResource(R.drawable.ic_star_active);
        else
            btn_star.setBackgroundResource(R.drawable.ic_star_inactive);
    }
}
