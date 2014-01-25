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

        final RelativeLayout background = (RelativeLayout) findViewById(R.id.background);
        ViewTreeObserver viewTreeObserver = background.getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
                @SuppressWarnings("deprecation")
                @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                @Override
                public void onGlobalLayout() {
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

            Log.d("CLICK", "CLICK");
        }
    }

    private void setCurrentElapsedTime() {
        long diff = SystemClock.elapsedRealtime() - mStartTime;
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
     *            x coordinate of the circle origin
     * @param y
     *            y coordinate of the circle origin
     * @param radius
     *            radius = distance from the circle origin to the center of the
     *            image
     * @param angleDeg
     *            angle in degree, clockwise from top
     */
    private void moveView(ImageView view, int x, int y, int radius,
            float angleDeg) {
        float tgt_x = Math.round(radius
                * Math.cos((angleDeg - 90) / 180.0 * Math.PI))
                + x;
        float tgt_y = Math.round(radius
                * Math.sin((angleDeg - 90) / 180.0 * Math.PI))
                + y;
        float img_w = view.getDrawable().getIntrinsicWidth(); // FIXME rotated!
        float img_h = view.getDrawable().getIntrinsicHeight();
        view.setTranslationX(tgt_x - img_w / 2);
        view.setTranslationY(tgt_y - img_h / 2);
        view.setRotation(angleDeg + 0);
    }

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
        Log.d("Intr. Width:  ",
                String.valueOf(image.getDrawable().getIntrinsicWidth()));
        Log.d("Intr. Height: ",
                String.valueOf(image.getDrawable().getIntrinsicHeight()));
        Log.d("IMG parent: ", image.getParent().getClass().getName());

        RelativeLayout background = (RelativeLayout) findViewById(R.id.background);

        Log.d("BG X:  ", String.valueOf(background.getLeft()));
        Log.d("BG Y:  ", String.valueOf(background.getTop()));
        Log.d("BG W:  ", String.valueOf(background.getWidth()));
        Log.d("BG H:  ", String.valueOf(background.getHeight()));
        Log.d("BG pad L: ", String.valueOf(background.getPaddingLeft()));
        Log.d("BG pad T: ", String.valueOf(background.getPaddingTop()));

        // radius to center of the image
        int radius = (background.getWidth() - background.getPaddingRight() - background
                .getPaddingLeft()) / 2 - image.getWidth() / 2;
        // center of the background
        int x = background.getWidth() / 2 - background.getPaddingLeft();
        int y = background.getHeight() / 2 - background.getPaddingTop();

        // 0 is on the top
        float angleDeg = 0;
        Log.d("radius:  ", String.valueOf(radius));
        Log.d("calc X:  ", String.valueOf(x));
        Log.d("calc Y:  ", String.valueOf(y));

        final int countImg = 12;
        for (int i = 0; i < countImg; i++) {
            ImageView img = new ImageView(image.getContext());
            img.setImageResource(R.drawable.ic_test);
            background.addView(img);

            moveView(img, x, y, radius, angleDeg);
            mImages.add(img);

            angleDeg += 360 / countImg;
        }
    }
}
