package org.zeroxlab.apps.coscup2010;

import org.zeroxlab.apps.coscup2010.Agenda.Sessions;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

public class SessionListActivity extends ListActivity {
    SessionListAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.session_list_view);
        Uri uri = getIntent().getData();
        mAdapter = new SessionListAdapter(this, uri);
        setListAdapter(mAdapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        Cursor c = (Cursor)mAdapter.getItem(position);
        long _id = c.getLong(c.getColumnIndex(Sessions._ID));
        Intent intent = new Intent(Intent.ACTION_VIEW,
                                   Uri.parse(Sessions.CONTENT_URI + "/" + _id),
                                   this,
                                   SessionActivity.class);
        startActivity(intent);
    }
}
