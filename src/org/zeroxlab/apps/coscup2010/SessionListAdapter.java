package org.zeroxlab.apps.coscup2010;

import org.zeroxlab.apps.coscup2010.Agenda.Sessions;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.net.Uri;
import android.view.View;
import android.widget.SimpleCursorAdapter.ViewBinder;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.util.Log;

public class SessionListAdapter extends SimpleCursorAdapter
    implements ViewBinder {

    Cursor mCursor;
    Activity mParent;

    public SessionListAdapter(Context context, Uri uri) {
        super(context, R.layout.session_view, null, null, null);

        String[] columns = new String[] {
            Sessions._ID,
            Sessions.TITLE,
            "strftime('%H:%M'," + Sessions.START + ",'localtime')",
            "strftime('%H:%M'," + Sessions.END + ",'localtime')",
            Sessions.ROOM
        };
        Log.d("SessionList", "query " + uri);
        mParent = (Activity)context;
        mCursor = mParent.getContentResolver().query(uri, columns, null, null, null);
        mParent.startManagingCursor(mCursor);

        int[] to = new int[] { R.id.session_time_room, R.id.session_title };

        changeCursorAndColumns(mCursor, columns, to);
        setViewBinder(this);
    }

    public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
        switch (view.getId()) {
        case R.id.session_time_room:
            TextView v = (TextView) view;
            Resources res = mParent.getResources();
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
