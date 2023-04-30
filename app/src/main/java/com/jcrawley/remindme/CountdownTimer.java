package com.jcrawley.remindme;

import android.content.Context;
import android.media.MediaPlayer;

import com.jcrawley.remindme.service.TimerService;
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
    private final Context context;
    private boolean isInitialized;
    private TimerService timerService;


    public CountdownTimer(Context context){
        this.context = context;
        timerTaskRunner = new TimerTaskRunner();
    }


    public void setAndUpdateView(MainView view, TimerService timerService){
        this.view = view;
        this.timerService = timerService;
        setCurrentCountdownValue(currentSeconds);
        view.setTimerRunningStatus(currentState);
    }


    public void setNotificationHelper(NotificationHelper notificationHelper){
        this.notificationHelper = notificationHelper;
    }


    public boolean isInitialized(){
        return isInitialized;
    }


    public void resetTime(){
        currentSeconds = timerStartingValue;
        if(view != null) {
            view.setCurrentCountdownValue(getMinutes(), getSeconds(), false);
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
        isInitialized = true;
        timerStartingValue = (minutes * SECONDS_PER_MINUTE) + seconds;
        updateCurrentSecondsIfTimerIsStopped(minutes, seconds);
    }


    private void updateCurrentSecondsIfTimerIsStopped(int minutes, int seconds){
        if(currentState == TimerState.STOPPED){
            currentSeconds = timerStartingValue;
            if(view != null) {
                view.resetCurrentCountdownValue(minutes, seconds);
            }
        }
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


    private void pauseTimer() {
        currentState = TimerState.PAUSED;
        isTimerRunning = false;
        timerTaskRunner.stopTimer();
        if(view == null){
            return;
        }
        view.showResumeButton();
        view.enableSetButton();
    }


    private void stopTimer(){
        currentState = TimerState.STOPPED;
        isTimerRunning = false;
        timerTaskRunner.stopTimer();
        if(view == null){
            return;
        }
        view.enableSetButton();
        view.enableAndShowStartButton();
    }


    public void setCurrentCountdownValue(int currentValue) {
        int minutes = getClockMinutes(currentValue);
        int seconds = getClockSeconds(currentValue);
        if(view == null){
            return;
        }
        view.setCurrentCountdownValue(minutes, seconds, isCritical(currentValue));
    }


    private boolean isCritical(int currentTimeLeft){
        return currentTimeLeft <= 5
                || (timerStartingValue >= 60 && currentTimeLeft <= 15)
                || (timerStartingValue >= 600 && currentTimeLeft <= 30);
    }


    public void countDownOneSecond(){
        currentSeconds = currentSeconds <= 0 ? 0 : currentSeconds -1;
        setCurrentCountdownValue(currentSeconds);
        if(currentSeconds == 0){
            onCountdownComplete();
            stopTimer();
            playSoundOnTimesUp();
            notificationHelper.sendTimesUpNotification();
            return;
        }
        notificationHelper.updateNotification(getStr(R.string.menu_button_counting_down_message), getCurrentTime());
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
        view.notifyTimesUp(timerService.getTimesUpMessage());
        view.showStartButton();
        view.disableStartButton();
    }


    private String getStr(int id){
        return context.getString(id);
    }


    private void playSoundOnTimesUp(){
        MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.alert1);
        try{
            mediaPlayer.start();

        }catch(Exception e){e.printStackTrace();}
    }

}