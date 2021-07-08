package com.jcrawley.remindme.tasks;

import com.jcrawley.remindme.CountdownTimer;
import com.jcrawley.remindme.tasks.CountdownTimerTask;

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
        log("Entered startTimer()");
        future = scheduledExecutorService.scheduleWithFixedDelay(new CountdownTimerTask(countdownTimer), 0, 1, TimeUnit.SECONDS);
    }


    public void stopTimer(){
        log("Entered stopTimer()");
        if(!future.isCancelled()){
            future.cancel(true);
        }
    }

    void log(String msg){
        System.out.println("RemindMe TimerThreadRunner: " + msg);
        System.out.flush();
    }

}
