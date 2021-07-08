package com.jcrawley.remindme.deleteme;

import com.jcrawley.remindme.CountdownTimer;
import com.jcrawley.remindme.tasks.TimerTaskRunner;

public class DebugTimerTaskRunner extends TimerTaskRunner {


    public void startTimer(int millis, CountdownTimer countdownTimer){
        super.startTimer( countdownTimer);
    }


}
