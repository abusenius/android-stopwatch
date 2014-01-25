package org.alsoftlab.stopwatch;

import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

public class TimerActivity extends Activity {
	private boolean mStarted = false;
	private long mStartTime = 0;
	private TextView mChronometer;
	private TextView mText;
	private Handler mViewHandler = new Handler();
	private Runnable mUpdateView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.timer_activity);
		mChronometer = (TextView) findViewById(R.id.textView2);
		mText = (TextView) findViewById(R.id.textView1);

		mUpdateView = new Runnable() {
			@Override
			public void run() {
				setCurrentElapsedTime();
				
				mViewHandler.postDelayed(mUpdateView, 100);
			}
		};
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
			// stop
			
			mStarted = false;

			setCurrentElapsedTime();
	
			mViewHandler.removeCallbacks(mUpdateView);

		} else {
			// start
			
			mStartTime = SystemClock.elapsedRealtime();
			mStarted = true;
			mChronometer.setText("00:00");
			mText.setText("000");
			
			mViewHandler.post(mUpdateView);
		}
	}

	private void setCurrentElapsedTime() {
		long diff = SystemClock.elapsedRealtime() - mStartTime;
		long s = (diff / 1000) % 60;
		long m = (diff / 1000) / 60;
		mChronometer.setText(String.format("%02d:%02d", m, s));
		mText.setText(String.format("%03d", diff % 1000));
	}
}
