package com.jcrawley.remindme.tasks;

import com.jcrawley.remindme.CountdownTimer;
import com.jcrawley.remindme.tasks.TimerThread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by john on 11/01/18.
 */

public class TimerThreadRunner {

    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    private Future<?> future;


    public void startTimer(int millis, CountdownTimer countdownTimer){

        future = scheduledExecutorService.scheduleWithFixedDelay(new CountdownTimerTask(countdownTimer), 0, 1, TimeUnit.SECONDS);
    }


    public void stopTimer(){
        if(!future.isCancelled()){
            future.cancel(true);
        }
    }


}
