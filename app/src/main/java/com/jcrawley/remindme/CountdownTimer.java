package com.jcrawley.remindme;

import com.jcrawley.remindme.tasks.TimerTaskRunner;

public class CountdownTimer  {

    private final MainActivity view;
    private final int SECONDS_PER_MINUTE = 60;
    private boolean isTimerRunning = false;
    private final TimerTaskRunner timerTaskRunner;
    private int timerStartingValue;
    private int currentSeconds;


    public CountdownTimer(MainActivity view, int initialMinutes){
        this.view = view;
        timerTaskRunner = new TimerTaskRunner();
        view.setCurrentCountdownValue(getClockMinutes(SECONDS_PER_MINUTE), getClockSeconds(SECONDS_PER_MINUTE));
        currentSeconds = initialMinutes * SECONDS_PER_MINUTE;
        view.setCurrentCountdownValue(initialMinutes,0);
    }


    public void resetTime(){
        if(isTimerRunning){
            stopTimer();
        }
        currentSeconds = timerStartingValue;
        int minutes = timerStartingValue / 60;
        int seconds = timerStartingValue % 60;
        view.setCurrentCountdownValue( minutes, seconds);
        view.enableAndShowStartButton();
    }


    public void setTime(int minutes, int seconds){
        timerStartingValue = (minutes * SECONDS_PER_MINUTE) + seconds;
        currentSeconds = timerStartingValue;
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


    public void setCurrentCountdownValue(int currentValue) {
        int minutes = getClockMinutes(currentValue);
        int seconds = getClockSeconds(currentValue);
        view.setCurrentCountdownValue(minutes, seconds);
    }


    public void countdownOneSecond(){
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


    private void onCountdownComplete(){
        view.issueNotification();
        view.showStartButton();
        view.disableStartButton();
    }

}