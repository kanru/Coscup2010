package org.zeroxlab.apps.coscup2010;

import org.zeroxlab.apps.coscup2010.Agenda.Sessions;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
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
            Sessions.STARRED,
            "strftime('%H:%M'," + Sessions.START + ",'localtime')",
            "strftime('%H:%M'," + Sessions.END + ",'localtime')",
            Sessions.ROOM,
        };
        mParent = (Activity)context;
        mCursor = mParent.getContentResolver().query(uri, columns, null, null, null);
        mParent.startManagingCursor(mCursor);

        int[] to = new int[] { R.id.session_time_room, R.id.session_title, R.id.btn_star };

        changeCursorAndColumns(mCursor, columns, to);
        setViewBinder(this);

        if (mCursor.getCount() == 0) {
            mParent.findViewById(R.id.noresult).setVisibility(View.VISIBLE);
            mParent.findViewById(android.R.id.list).setVisibility(View.GONE);
        }
    }

    public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
        switch (view.getId()) {
        case R.id.session_time_room:
            TextView v = (TextView) view;
            Resources res = mParent.getResources();
            TypedArray rooms = res.obtainTypedArray(R.array.rooms);
            String room = rooms.getString(cursor.getInt(5));
            v.setText(cursor.getString(3) + " - " + cursor.getString(4)
                      + " " + room);
            return true;
        case R.id.btn_star:
            int starred = cursor.getInt(columnIndex);
            Button btn_star = (Button)view;
            if (starred == 0)
                btn_star.setBackgroundResource(android.R.color.transparent);
            else
                btn_star.setBackgroundResource(R.drawable.ic_star_active);
            return true;
        default:
            return false;
        }
    }
}
