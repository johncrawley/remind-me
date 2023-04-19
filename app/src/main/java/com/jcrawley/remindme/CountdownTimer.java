package com.jcrawley.remindme;

import com.jcrawley.remindme.tasks.TimerTaskRunner;

public class CountdownTimer  {

    private MainActivity view;
    private final int SECONDS_PER_MINUTE = 60;
    private boolean isTimerRunning = false;
    private final TimerTaskRunner timerTaskRunner;
    private int timerStartingValue;
    private int currentSeconds;


    public CountdownTimer(int initialMinutes){
        timerTaskRunner = new TimerTaskRunner();
        currentSeconds = initialMinutes * SECONDS_PER_MINUTE;
    }


    public void setView(MainActivity mainActivity){
        this.view = mainActivity;
    }


    public void resetTime(){
        if(isTimerRunning){
            stopTimer();
        }
        currentSeconds = timerStartingValue;
        int minutes = timerStartingValue / 60;
        int seconds = timerStartingValue % 60;
        if(view != null) {
            view.setCurrentCountdownValue(minutes, seconds);
            resetStartButton();
        }
    }


    public void resetStartButton(){
        if(!isTimerRunning && view != null){
            view.enableAndShowStartButton();
        }
    }


    public void setTime(int minutes, int seconds){
        timerStartingValue = (minutes * SECONDS_PER_MINUTE) + seconds;
        currentSeconds = timerStartingValue;
    }


    public void startTimer() {
        isTimerRunning = true;
        timerTaskRunner.startTimer(this);
        if(view == null){
            return;
        }
        view.showPauseButton();
        view.showResetButton();
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
        timerTaskRunner.stopTimer();
        if(view == null){
            return;
        }
        view.showResumeButton();
        view.enableSetButton();
        view.changeCountdownColorOff();
    }


    private void stopTimer(){
        isTimerRunning = false;
        timerTaskRunner.stopTimer();
        if(view == null){
            return;
        }
        view.enableSetButton();
        view.changeCountdownColorOff();
        view.enableAndShowStartButton();
    }


    public void setCurrentCountdownValue(int currentValue) {
        int minutes = getClockMinutes(currentValue);
        int seconds = getClockSeconds(currentValue);
        if(view == null){
            return;
        }
        view.setCurrentCountdownValue(minutes, seconds);
    }


    public void countdownOneSecond(){
        currentSeconds = currentSeconds <= 0 ? 0 : currentSeconds -1;
        setCurrentCountdownValue(currentSeconds);
        if(currentSeconds == 0){
            onCountdownComplete();
            stopTimer();
        }
    }


    private int getClockSeconds(int seconds){
        return seconds % SECONDS_PER_MINUTE;
    }

    private int getClockMinutes(int seconds){
        return  seconds / SECONDS_PER_MINUTE;
    }


    private void onCountdownComplete(){
        if(view == null){
            return;
        }
        view.issueNotification();
        view.showStartButton();
        view.disableStartButton();
    }

}