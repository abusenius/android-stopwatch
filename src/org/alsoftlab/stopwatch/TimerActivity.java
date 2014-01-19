package org.alsoftlab.stopwatch;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class TimerActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timer_activity);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.timer, menu);
        return true;
    }

}
