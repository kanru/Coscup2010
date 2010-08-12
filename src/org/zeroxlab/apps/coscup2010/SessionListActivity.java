package org.zeroxlab.apps.coscup2010;

import org.zeroxlab.apps.coscup2010.Agenda.Sessions;

import android.app.ListActivity;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SimpleCursorAdapter.ViewBinder;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class SessionListActivity extends ListActivity
    implements ViewBinder {
    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.session_list_view);

        String[] columns = new String[] {
            Sessions._ID,
            Sessions.TITLE,
            "strftime('%H:%M'," + Sessions.START + ",'localtime')",
            "strftime('%H:%M'," + Sessions.END + ",'localtime')",
            Sessions.ROOM
        };

        Cursor cursor = getContentResolver().query(Sessions.CONTENT_URI,
                                                   columns, null, null, null);
        startManagingCursor(cursor);

        int[] to = new int[] { R.id.session_time_room, R.id.session_title };

        SimpleCursorAdapter mAdapter =
            new SimpleCursorAdapter(this, R.layout.session_view, cursor, columns, to);

        mAdapter.setViewBinder(this);
        setListAdapter(mAdapter);
    }

    public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
        switch (view.getId()) {
        case R.id.session_time_room:
            TextView v = (TextView) view;
            Resources res = getResources();
            TypedArray rooms = res.obtainTypedArray(R.array.rooms);
            String room = rooms.getString(cursor.getInt(4));
            v.setText(cursor.getString(2) + " - " + cursor.getString(3)
                      + " " + room);
            return true;
        default:
            return false;
        }
    }
}
