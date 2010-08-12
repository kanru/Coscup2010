package org.zeroxlab.apps.coscup2010;

import org.zeroxlab.apps.coscup2010.Agenda.Sessions;
import org.zeroxlab.apps.coscup2010.Agenda.Speakers;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TabHost.TabSpec;
import android.widget.TabHost;
import android.widget.TextView;

public class SessionActivity extends TabActivity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.session);

        Intent intent = getIntent();
        Uri uri = intent.getData();

        String[] columns = new String[] {
            Sessions._ID,
            Sessions.TITLE,
            "strftime('%H:%M'," + Sessions.START + ",'localtime')",
            "strftime('%H:%M'," + Sessions.END + ",'localtime')",
            Sessions.ROOM,
            Sessions.SUMMARY,
        };

        Cursor cursor = getContentResolver().query(uri, columns, null, null, null);

        cursor.moveToFirst();

        TextView title = (TextView)findViewById(R.id.session_title);
        title.setText(cursor.getString(cursor.getColumnIndex(Sessions.TITLE)));

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
        spec = tabHost.newTabSpec("notes").setIndicator(tab).setContent(R.id.session_summary);
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
    }
}
