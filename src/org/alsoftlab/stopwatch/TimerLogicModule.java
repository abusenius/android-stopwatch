package org.alsoftlab.stopwatch;

import dagger.*;

@Module(injects = TimerActivity.class)
public class TimerLogicModule {
    @Provides
    TimerLogic provideTimerLogic() {
        return new TimerLogic();
    }
}
