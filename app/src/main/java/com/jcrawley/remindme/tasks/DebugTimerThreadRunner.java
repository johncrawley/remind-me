package com.jcrawley.remindme.tasks;

import com.jcrawley.remindme.CountdownTimer;

public class DebugTimerThreadRunner extends TimerThreadRunner {
    
    public void startTimer(int millis, CountdownTimer countdownTimer){
        super.startTimer(3000, countdownTimer);
    }
}
