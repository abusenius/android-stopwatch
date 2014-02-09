package org.alsoftlab.stopwatch;

import android.os.SystemClock;

public class TimerLogic {
    private boolean mStarted;
    private long mStartTime;
    private long mEndTime = 0;

    /**
     * Create timer. It is not started.
     */
    public TimerLogic() {
        mStarted = false;
        mStartTime = 0;
    }

    /**
     * @return true iff the timer is running
     */
    public boolean isRunning() {
        return mStarted;
    }

    /**
     * Start the timer if stopped and vice vers
     * 
     * @return true iff the timer was started
     */
    public boolean toggleRunning() {
        if (mStarted) {
            // stop

            mEndTime = SystemClock.elapsedRealtime();
            mStarted = false;
        } else {
            // start

            mStartTime = SystemClock.elapsedRealtime();
            mEndTime = 0;
            mStarted = true;
        }
        return mStarted;
    }

    /**
     * @return elapsed time on ms since start
     */
    public long getElapsedTime() {
        if (mStarted) {
            return SystemClock.elapsedRealtime() - mStartTime;
        } else {
            return mEndTime - mStartTime;
        }
    }
}