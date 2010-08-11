package org.zeroxlab.apps.coscup2010;

import android.app.Activity;
import android.os.Bundle;

public class LauncherActivity extends Activity
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        DatabaseHelper db = new DatabaseHelper(this);
        db.sync();
    }
}
