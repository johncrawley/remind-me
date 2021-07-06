package com.jcrawley.remindme.tasks;

import com.jcrawley.remindme.CountdownTimer;

public class CountdownTimerTask implements Runnable{

    private final CountdownTimer countdownTimer;

    public CountdownTimerTask(CountdownTimer countdownTimer){
        this.countdownTimer = countdownTimer;
    }

    public void run(){
        countdownTimer.countdownOneSecond();
    }

}
