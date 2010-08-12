package org.zeroxlab.apps.coscup2010;

import android.app.ListActivity;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

public class SessionListActivity extends ListActivity {
    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.session_list_view);
        Uri uri = getIntent().getData();
        SessionListAdapter mAdapter = new SessionListAdapter(this, uri);
        setListAdapter(mAdapter);
    }
}
