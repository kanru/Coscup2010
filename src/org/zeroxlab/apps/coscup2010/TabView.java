package org.zeroxlab.apps.coscup2010;

import android.content.Context;
import android.graphics.Typeface;
import android.view.Gravity;
import android.widget.Button;

public class TabView extends Button {
    public TabView(Context context) {
        super(context);
        setBackgroundResource(R.drawable.tab_background);
        setGravity(Gravity.CENTER);
        setPadding(10, 15, 10, 10);
        setTypeface(Typeface.DEFAULT_BOLD);
    }
}
