package org.zeroxlab.apps.coscup2010;

import org.zeroxlab.apps.coscup2010.Agenda.Sessions;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.File;

public class LauncherActivity extends Activity
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        if (!getDatabasePath(DatabaseHelper.DATABASE_NAME).exists()) {
                    DatabaseHelper db = new DatabaseHelper(LauncherActivity.this);
                    db.sync();
        }

        final ImageButton btn_refresh = (ImageButton) findViewById(R.id.btn_refresh);
        btn_refresh.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    DatabaseHelper db = new DatabaseHelper(LauncherActivity.this);
                    db.sync();
                }
            });
        final ImageButton btn_search = (ImageButton) findViewById(R.id.btn_search);
        btn_search.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    LauncherActivity.this.onSearchRequested();
                }
            });
        final Button btn_tracks = (Button) findViewById(R.id.btn_tracks);
        btn_tracks.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(LauncherActivity.this, TracksActivity.class);
                    startActivity(intent);
                }
            });
        final Button btn_map = (Button) findViewById(R.id.btn_map);
        btn_map.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW,
                                               Uri.parse("geo:25.039406,121.616957?z=19"));
                    try {
                        startActivity(intent);
                    } catch (ActivityNotFoundException e){
                        Toast.makeText(LauncherActivity.this,
                                       "Cannot open Map", Toast.LENGTH_LONG);
                    }
                }
            });
        final Button btn_next = (Button) findViewById(R.id.btn_next);
        btn_next.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW,
                                               Uri.withAppendedPath(Sessions.CONTENT_URI, "next"));
                    intent.setClass(LauncherActivity.this, SessionsActivity.class);
                    // FIXME, L10N
                    intent.putExtra("title", "Following Sessions");
                    startActivity(intent);
                }
            });
        final Button btn_starred = (Button) findViewById(R.id.btn_starred);
        btn_starred.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW,
                                               Uri.withAppendedPath(Sessions.CONTENT_URI, "starred"));
                    intent.setClass(LauncherActivity.this, SessionsActivity.class);
                    // FIXME, L10N
                    intent.putExtra("title", "Starred Sessions");
                    startActivity(intent);
                }
            });
    }
}
