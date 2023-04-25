package com.jcrawley.remindme.tasks;

import com.jcrawley.remindme.CountdownTimer;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by john on 11/01/18.
 */

public class TimerTaskRunner {

    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    private Future<?> future;


    public void startTimer(CountdownTimer countdownTimer){
        future = scheduledExecutorService.scheduleWithFixedDelay(countdownTimer::countDownOneSecond, 0, 1, TimeUnit.SECONDS);
    }


    public void stopTimer(){
        if(!future.isCancelled()){
            future.cancel(true);
        }
    }

}
