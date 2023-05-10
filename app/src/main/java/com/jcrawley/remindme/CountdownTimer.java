package com.jcrawley.remindme;

import android.content.Context;
import android.media.MediaPlayer;

import com.jcrawley.remindme.service.TimerService;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CountdownTimer  {

    private MainView view;
    private final int SECONDS_PER_MINUTE = 60;
    private boolean isTimerRunning = false;
    private int timerStartingValue;
    private int millisecondsRemaining;
    public enum TimerState { STOPPED, RUNNING, PAUSED }
    private TimerState currentState = TimerState.STOPPED;
    private NotificationHelper notificationHelper;
    private final Context context;
    private boolean isInitialized;
    private TimerService timerService;
    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    private Future<?> future;
    private boolean isAfterReset = true;


    public CountdownTimer(Context context){
        this.context = context;
    }


    public void setAndUpdateView(MainView view, TimerService timerService){
        this.view = view;
        this.timerService = timerService;
        setCurrentCountdownValue(getSecondsRemaining());
        view.setTimerRunningStatus(currentState);
    }


    public void setNotificationHelper(NotificationHelper notificationHelper){
        this.notificationHelper = notificationHelper;
    }


    public boolean isInitialized(){
        return isInitialized;
    }


    public void resetTime(){
        setMillisecondsRemaining();
        isAfterReset = true;
        if(view != null) {
            view.setCurrentCountdownValue(getMinutes(), getSeconds(), false);
            resetStartButton();
        }
        if(currentState == TimerState.RUNNING){
            cancelCountdownTask();
            startTimer();
            return;
        }
        notificationHelper.updateNotification(getStr(R.string.notification_state_ready), getCurrentTime());
    }


    public void resetStartButton(){
        if(!isTimerRunning && view != null){
            view.enableAndShowStartButton();
        }
    }


    private void setMillisecondsRemaining(){
        millisecondsRemaining = timerStartingValue * 1000;
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
            setMillisecondsRemaining();
            if(view != null) {
                view.resetCurrentCountdownValue(minutes, seconds);
            }
        }
    }


    private int getMinutes(){
        return getSeconds() / 60;
    }


    private int getSecondsRemaining(){
        return getSeconds() % 60;
    }


    private int getSeconds(){
        return millisecondsRemaining / 1000;
    }


    public void startStop(){
        if(isTimerRunning){
            pauseTimer();
            return;
        }
        startTimer();
    }


    private void startTimer() {
        initiateTask();
        currentState = TimerState.RUNNING;
        isTimerRunning = true;
        if(view == null){
            return;
        }
        view.showPauseButton();
        view.showResetButton();
        view.changeCountdownColorOff();
    }


    private void initiateTask(){
        long initialDelay = isAfterReset ? 1000 : 0;
        isAfterReset = false;
        future = scheduledExecutorService.scheduleWithFixedDelay(this::countDownAHundredMilliseconds, initialDelay, 100, TimeUnit.MILLISECONDS);
    }


    private void pauseTimer() {
        currentState = TimerState.PAUSED;
        isTimerRunning = false;
        cancelCountdownTask();
        if(view == null){
            return;
        }
        view.showResumeButton();
        view.enableSetButton();
    }


    private void stopTimer(){
        currentState = TimerState.STOPPED;
        isTimerRunning = false;
        cancelCountdownTask();
        if(view == null){
            return;
        }
        view.enableSetButton();
        view.enableAndShowStartButton();
    }


    private void cancelCountdownTask(){
        if(!future.isCancelled()){
            future.cancel(true);
        }
    }


    public void setCurrentCountdownValue(int millisecondsRemaining) {
        int totalSecondsLeft = millisecondsRemaining / 1000;
        int minutes = getClockMinutes(totalSecondsLeft);
        int seconds = getClockSeconds(totalSecondsLeft);
        if(view == null){
            return;
        }
        view.setCurrentCountdownValue(minutes, seconds, isCritical(totalSecondsLeft));
    }


    private boolean isCritical(int currentTimeLeft){
        return currentTimeLeft <= 5
                || (timerStartingValue >= 60 && currentTimeLeft <= 15)
                || (timerStartingValue >= 600 && currentTimeLeft <= 30);
    }


    public void countDownAHundredMilliseconds(){
        millisecondsRemaining = Math.max(millisecondsRemaining - 100, 0);
        setCurrentCountdownValue(millisecondsRemaining);
        updateCountdownNotification();
        handleTimesUp();
    }


    private void updateCountdownNotification(){
        if(millisecondsRemaining >= 1000) {
            notificationHelper.updateCountdown(getStr(R.string.notification_state_counting_down), getCurrentTime());
        }
    }


    private void handleTimesUp(){
        if(millisecondsRemaining > 0) {
            return;
        }
        onCountdownComplete();
        stopTimer();
        playSoundOnTimesUp();
        notificationHelper.sendTimesUpNotification();
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