package org.zeroxlab.apps.coscup2010;

import org.zeroxlab.apps.coscup2010.Agenda.Tracks;

import android.app.ListActivity;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorAdapter.ViewBinder;

public class TrackListActivity extends ListActivity
    implements ViewBinder {
    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.track_list_view);

        String[] columns = new String[] {
            Tracks._ID,
            Tracks.TITLE
        };

        Cursor cursor = getContentResolver().query(Tracks.CONTENT_URI,
                                                   columns, null, null, null);
        startManagingCursor(cursor);

        int[] to = new int[] { R.id.track_icon, R.id.track_title };

        SimpleCursorAdapter mAdapter =
            new SimpleCursorAdapter(this, R.layout.track_view, cursor, columns, to);

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
}
