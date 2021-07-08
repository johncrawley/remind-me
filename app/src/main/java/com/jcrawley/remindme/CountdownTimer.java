package com.jcrawley.remindme;

import com.jcrawley.remindme.deleteme.DebugTimerTaskRunner;
import com.jcrawley.remindme.tasks.TimerTaskRunner;

public class CountdownTimer  {

    private final MainActivity view;
    private final int SECONDS_PER_MINUTE = 60;
    private final TimerTaskRunner timerTaskRunner;
    private boolean isStarted;
    private boolean isTimerRunning = false;
    private int timerStartingValue;
    private int currentSeconds;


    public CountdownTimer(MainActivity view, int initialMinutes){
        this.view = view;

        view.setCurrentCountdownValue(getClockMinutes(SECONDS_PER_MINUTE), getClockSeconds(SECONDS_PER_MINUTE));
        timerTaskRunner = new DebugTimerTaskRunner();
        currentSeconds = initialMinutes * SECONDS_PER_MINUTE;
        view.setCurrentCountdownValue(initialMinutes,0);
    }
    
    public void adjustTimeTest(int seconds){
        if(isTimerRunning){
            stopTimer();
            resetTimer();
        }
        timerStartingValue = seconds;
        currentSeconds = seconds;
        view.setCurrentCountdownValue( 0, seconds);
        view.enableAndShowStartButton();
    }


    public void adjustTime() {

        final int TIMER_MIN = 5 * SECONDS_PER_MINUTE;
        final int INTERVAL_INCREMENTS = 5 * SECONDS_PER_MINUTE;
        final int TIMER_MAX = 60 * SECONDS_PER_MINUTE;
        timerStartingValue += INTERVAL_INCREMENTS;
        if(timerStartingValue > TIMER_MAX){
            timerStartingValue = TIMER_MIN;
        }
        view.setCurrentCountdownValue( timerStartingValue / SECONDS_PER_MINUTE, 0);
    }


    private void setTimeOnView(){
        view.setCurrentCountdownValue( timerStartingValue / SECONDS_PER_MINUTE, 0);
    }


    public void startTimer() {
        isTimerRunning = true;
        view.showPauseButton();
        view.showResetButton();
        timerTaskRunner.startTimer(this );
        view.changeCountdownColorOff();
    }



    public void startStop(){
        if(isTimerRunning){
            pauseTimer();
            return;
        }
        startTimer();
    }


    private void pauseTimer() {
        isTimerRunning = false;
        view.showResumeButton();
        view.enableSetButton();
        view.changeCountdownColorOff();
        timerTaskRunner.stopTimer();
    }


    private void stopTimer(){
        isTimerRunning = false;
        view.enableSetButton();
        view.changeCountdownColorOff();
        timerTaskRunner.stopTimer();
    }



    public void resetTimer(){
        if(isTimerRunning){
            stopTimer();
            setTimeOnView();
        }
        view.enableAndShowStartButton();
    }


    public void setCurrentCountdownValue(int currentValue) {
        int minutes = getClockMinutes(currentValue);
        int seconds = getClockSeconds(currentValue);
        view.setCurrentCountdownValue(minutes, seconds);
    }


    public void countdownOneSecond(){
        log("Entered countdownOneSecond()");
        currentSeconds = currentSeconds <= 0 ? 0 : currentSeconds -1;
        log("Current seconds :" + currentSeconds);
        setCurrentCountdownValue(currentSeconds);
        if(currentSeconds == 0){
            onCountdownComplete();
            stopTimer();
        }
    }


    private void log(String msg){
        System.out.println("RemindMe CountdownTimer: " + msg);
        System.out.flush();
    }


    private int getClockSeconds(int seconds){
        return seconds % SECONDS_PER_MINUTE;
    }

    private int getClockMinutes(int seconds){
        return  seconds / SECONDS_PER_MINUTE;
    }


    public void onCountdownComplete(){
        log("Entered onCountdownComplete()");
        view.showStartButton();
        view.disableStartButton();
        view.issueNotification();
    }

}