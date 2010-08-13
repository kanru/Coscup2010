package org.zeroxlab.apps.coscup2010;

import org.zeroxlab.apps.coscup2010.Agenda.Tracks;

import android.app.ListActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorAdapter.ViewBinder;

public class TracksActivity extends ListActivity
    implements ViewBinder {

    private SimpleCursorAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.tracks);

        // DatabaseHelper db = new DatabaseHelper(this);
        // db.sync();

        TextView title = (TextView)findViewById(R.id.action_bar_title);
        title.setText("Session Tracks");

        final ImageButton btn_home = (ImageButton) findViewById(R.id.btn_home);
        btn_home.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(TracksActivity.this, LauncherActivity.class);
                    startActivity(intent);
                }
            });

        String[] columns = new String[] {
            Tracks._ID,
            Tracks.TITLE
        };

        Cursor cursor = getContentResolver().query(Tracks.CONTENT_URI,
                                                   columns, null, null, null);
        startManagingCursor(cursor);

        int[] to = new int[] { R.id.track_icon, R.id.track_title };

        mAdapter = new SimpleCursorAdapter(this, R.layout.track_view, cursor, columns, to);

        mAdapter.setViewBinder(this);
        setListAdapter(mAdapter);
    }

    public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
        switch (view.getId()) {
        case R.id.track_icon:
            ImageView img = (ImageView) view;
            Resources res = getResources();
            TypedArray icons = res.obtainTypedArray(R.array.track_icons);
            Drawable drawable = icons.getDrawable(cursor.getInt(columnIndex));
            img.setImageDrawable(drawable);
            return true;
        default:
            return false;
        }
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        Cursor c = (Cursor)mAdapter.getItem(position);
        long _id = c.getLong(c.getColumnIndex(Tracks._ID));
        Intent intent = new Intent(Intent.ACTION_VIEW,
                                   Uri.parse(Tracks.CONTENT_URI + "/" + _id),
                                   this,
                                   TrackActivity.class);
        startActivity(intent);
    }
}
