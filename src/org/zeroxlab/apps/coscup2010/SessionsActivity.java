package org.zeroxlab.apps.coscup2010;

import org.zeroxlab.apps.coscup2010.Agenda.Sessions;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

public class SessionsActivity extends ListActivity {
    SessionListAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.sessions);

        final ImageButton btn_home = (ImageButton) findViewById(R.id.btn_home);
        btn_home.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(SessionsActivity.this, LauncherActivity.class);
                    startActivity(intent);
                }
            });
        final ImageButton btn_search = (ImageButton) findViewById(R.id.btn_search);
        btn_search.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    SessionsActivity.this.onSearchRequested();
                }
            });

        Intent intent = getIntent();
        if (intent.hasExtra("title")) {
            TextView title = (TextView)findViewById(R.id.action_bar_title);
            title.setText(intent.getIntExtra("title", R.string.search_sessions));
        }

        String query = null;
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            query = intent.getStringExtra(SearchManager.QUERY);
            TextView title = (TextView)findViewById(R.id.action_bar_title);
            title.setText(R.string.search_sessions);
        }
        Uri uri = getIntent().getData();
        mAdapter = new SessionListAdapter(this, uri, query);
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
