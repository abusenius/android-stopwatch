/**
 * 
 */
package org.alsoftlab.stopwatch;

import android.app.Application;
import android.util.Log;

/**
 * @author al
 *
 */
public class TimerApp extends Application {
    
    @Override
    public void onCreate() {
        super.onCreate();
        
        Log.d("TimerApp", "onCreate");
        
        Injector.init(new TimerLogicModule());
    }

}
