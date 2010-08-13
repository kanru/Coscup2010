package org.zeroxlab.apps.coscup2010;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class LauncherActivity extends Activity
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        final ImageButton btn_refresh = (ImageButton) findViewById(R.id.btn_refresh);
        btn_refresh.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    DatabaseHelper db = new DatabaseHelper(LauncherActivity.this);
                    db.sync();
                }
            });
        // Intent intent = new Intent();
        // intent.setClass(this, TrackListActivity.class);
        // startActivity(intent);
        // intent.setClass(this, TrackActivity.class);
        // startActivity(intent);
    }
}
