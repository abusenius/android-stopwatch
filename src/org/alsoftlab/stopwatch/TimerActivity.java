package org.alsoftlab.stopwatch;

import java.util.ArrayList;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TimerActivity extends Activity {
    /** The number of ticks shown */
    private final static int TICK_COUNT = 12;

    /**
     * All views are refreshed with the current time every this many
     * milliseconds
     */
    private final static int REFRESH_PERIOD_MS = 1000 / TICK_COUNT;

    private boolean mStarted = false;
    private long mStartTime = 0;
    private TextView mChronometer;
    private TextView mText;
    private Handler mViewHandler = new Handler();
    private Runnable mUpdateView;
    private ArrayList<ImageView> mImages = new ArrayList<ImageView>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(getClass().getSimpleName(), "onCreate");

        setContentView(R.layout.timer_activity);
        mChronometer = (TextView) findViewById(R.id.textView2);
        mText = (TextView) findViewById(R.id.textView1);

        mUpdateView = new Runnable() {
            @Override
            public void run() {
                setCurrentElapsedTime(SystemClock.elapsedRealtime());

                mViewHandler.postDelayed(mUpdateView, REFRESH_PERIOD_MS);
            }
        };

        final RelativeLayout background = (RelativeLayout) findViewById(R.id.background);
        ViewTreeObserver viewTreeObserver = background.getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
                @SuppressWarnings("deprecation")
                @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                @Override
                public void onGlobalLayout() {
                    Log.d("TimerActivity", "onGlobalLayout");

                    if (background.getViewTreeObserver().isAlive()) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            background.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        } else {
                            background.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        }
                    }
                    drawImages();
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d("TimerActivity", "onCreateOptionsMenu");

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.timer, menu);
        return true;
    }

    public void button_OnClick(View view) {
        // assert (view.getId() == R.id.imageButton1);

        if (mStarted) {
            // stop

            long endTime = SystemClock.elapsedRealtime();
            mStarted = false;

            mViewHandler.removeCallbacks(mUpdateView);
            setCurrentElapsedTime(endTime);
        } else {
            // start

            mStartTime = SystemClock.elapsedRealtime();
            mStarted = true;

            setCurrentElapsedTime(mStartTime);
            mViewHandler.post(mUpdateView);
        }
    }

    /**
     * Update text views with the current elapsed time
     */
    private void setCurrentElapsedTime(long elapsedTime) {
        long diff = elapsedTime - mStartTime;
        long s = (diff / 1000) % 60;
        long m = (diff / 1000) / 60;
        mChronometer.setText(String.format("%02d:%02d", m, s));
        mText.setText(String.format("%03d", diff % 1000));
    }

    /**
     * Move view to clock position
     * 
     * @param view
     * @param x
     *            X-coordinate of the circle origin
     * @param y
     *            Y-coordinate of the circle origin
     * @param radius
     *            distance from the circle origin to the outer border of the
     *            image
     * @param angleDeg
     *            angle in degrees, counted clockwise from the top (12 o'clock)
     */
    private void moveView(ImageView view, int x, int y, int radius, float angleDeg) {
        float center_radius = radius
                        - Math.max(view.getDrawable().getIntrinsicWidth(), view.getDrawable().getIntrinsicHeight())
                        / 2.0f;
        // calculate future image center
        float tgt_x = Math.round(center_radius * Math.cos((angleDeg - 90) / 180.0f * Math.PI)) + x;
        float tgt_y = Math.round(center_radius * Math.sin((angleDeg - 90) / 180.0f * Math.PI)) + y;
        // calculate offset to top left corner
        tgt_x -= view.getDrawable().getIntrinsicWidth() / 2.0f;
        tgt_y -= view.getDrawable().getIntrinsicHeight() / 2.0f;
        // move
        view.setTranslationX(tgt_x);
        view.setTranslationY(tgt_y);
        // rotates around the center
        view.setRotation(angleDeg);
    }

    /**
     * Draw 12 ticks
     */
    private void drawImages() {
        if (mImages.size() > 0) {
            return;
        }
        // DEBUG
        ImageView image = (ImageView) findViewById(R.id.imageView1);

        Log.d("IMG X:  ", String.valueOf(image.getLeft()));
        Log.d("IMG Y:  ", String.valueOf(image.getTop()));
        Log.d("IMG Width:  ", String.valueOf(image.getWidth()));
        Log.d("IMG Height: ", String.valueOf(image.getHeight()));
        Log.d("Intr. Width:  ", String.valueOf(image.getDrawable().getIntrinsicWidth()));
        Log.d("Intr. Height: ", String.valueOf(image.getDrawable().getIntrinsicHeight()));
        Log.d("IMG parent: ", image.getParent().getClass().getName());
        Log.d("IMG context: ", image.getContext().toString());

        RelativeLayout background = (RelativeLayout) findViewById(R.id.background);
        Log.d("BG parent: ", background.getContext().toString());

        Log.d("BG X:  ", String.valueOf(background.getLeft()));
        Log.d("BG Y:  ", String.valueOf(background.getTop()));
        Log.d("BG W:  ", String.valueOf(background.getWidth()));
        Log.d("BG H:  ", String.valueOf(background.getHeight()));
        Log.d("BG pad L: ", String.valueOf(background.getPaddingLeft()));
        Log.d("BG pad T: ", String.valueOf(background.getPaddingTop()));

        // outer radius is the nearest distance from center to the border
        int radius = Math.min((background.getWidth() - background.getPaddingRight() - background.getPaddingLeft()) / 2,
                        (background.getHeight() - background.getPaddingBottom() - background.getPaddingTop()) / 2);
        // center the circle around center of the background, on the bottom
        int x = background.getWidth() / 2 - background.getPaddingLeft();
        int y = background.getHeight() - background.getPaddingTop() - background.getPaddingBottom() - radius;

        // 0 degrees is on the top
        float angleDeg = 0;
        Log.d("radius:  ", String.valueOf(radius));
        Log.d("calc X:  ", String.valueOf(x));
        Log.d("calc Y:  ", String.valueOf(y));

        for (int i = 0; i < TICK_COUNT; i++) {
            ImageView img = new ImageView(image.getContext());
            img.setImageResource(R.drawable.ic_test);
            background.addView(img);

            moveView(img, x, y, radius, angleDeg);
            mImages.add(img);

            angleDeg += 360.0f / TICK_COUNT;
        }
    }
}
