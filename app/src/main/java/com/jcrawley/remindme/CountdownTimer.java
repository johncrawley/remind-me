package com.jcrawley.remindme;

import android.content.Context;
import android.media.MediaPlayer;

import com.jcrawley.remindme.tasks.TimerTaskRunner;

public class CountdownTimer  {

    private MainView view;
    private final int SECONDS_PER_MINUTE = 60;
    private boolean isTimerRunning = false;
    private final TimerTaskRunner timerTaskRunner;
    private int timerStartingValue;
    private int currentSeconds;
    public enum TimerState { STOPPED, RUNNING, PAUSED }
    private TimerState currentState = TimerState.STOPPED;
    private NotificationHelper notificationHelper;
    private Context context;


    public CountdownTimer(Context context, int initialMinutes){
        this.context = context;
        timerTaskRunner = new TimerTaskRunner();
        currentSeconds = initialMinutes * SECONDS_PER_MINUTE;
    }


    public void setAndUpdateView(MainView view){
        this.view = view;
        setCurrentCountdownValue(currentSeconds);
        view.setTimerRunningStatus(currentState);
    }


    public void setNotificationHelper(NotificationHelper notificationHelper){
        this.notificationHelper = notificationHelper;
    }


    public void resetTime(){
        if(isTimerRunning){
            stopTimer();
        }
        currentSeconds = timerStartingValue;
        if(view != null) {
            view.setCurrentCountdownValue(getMinutes(), getSeconds());
            resetStartButton();
        }
    }


    public void resetStartButton(){
        if(!isTimerRunning && view != null){
            view.enableAndShowStartButton();
        }
    }


    public String getCurrentTime(){
        int minutes = getMinutes();
        int seconds = getSeconds();

        return getClockStringFor(minutes) + ":" + getClockStringFor(seconds);
    }


    private String getClockStringFor(int number){
        String numberStr = String.valueOf(number);
        return number < 10 ? "0" + numberStr : numberStr;
    }

    public void setTime(int minutes, int seconds){
        timerStartingValue = (minutes * SECONDS_PER_MINUTE) + seconds;
        currentSeconds = timerStartingValue;
    }

    private int getMinutes(){
        return currentSeconds / 60;
    }

    private int getSeconds(){
        return currentSeconds % 60;
    }

    public void startTimer() {
        currentState = TimerState.RUNNING;
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


    private void playSoundOnTimesUp(){
        MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.alert1);
        try{
            mediaPlayer.start();

        }catch(Exception e){e.printStackTrace();}
    }


    private void pauseTimer() {
        currentState = TimerState.PAUSED;
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
        currentState = TimerState.STOPPED;
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
            playSoundOnTimesUp();
            notificationHelper.sendTimesUpNotification("Times up");

            return;
        }
        notificationHelper.updateNotification("counting down", getCurrentTime());
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
        notificationHelper.updateNotification("counting down", getCurrentTime());
        view.notifyTimesUp();
        view.showStartButton();
        view.disableStartButton();
    }

}