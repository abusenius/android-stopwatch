package org.alsoftlab.stopwatch;

import android.os.Bundle;
import android.os.SystemClock;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.Chronometer;
import android.widget.TextView;

public class TimerActivity extends Activity {
    private boolean mStarted = false;
    private long mStartTime = 0;
    private Chronometer mChronometer;
    private TextView mText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timer_activity);
        mChronometer = (Chronometer) findViewById(R.id.chronometer1);
        mText = (TextView) findViewById(R.id.textView1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.timer, menu);
        return true;
    }

    public void button_OnClick(View view) {
        // assert (view.getId() == R.id.imageButton1);

        if (mStarted) {
            mChronometer.stop();
            mStarted = false;

            long diff = SystemClock.elapsedRealtime() - mStartTime;
            mText.setText(String.format("%03d", diff % 1000));
        } else {
            mStartTime = SystemClock.elapsedRealtime();
            mChronometer.setBase(mStartTime);
            mChronometer.start();
            mStarted = true;
            mText.setText("000");
        }
    }
}
