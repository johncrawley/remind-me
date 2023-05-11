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
    private int timerStartingValue;
    private int millisecondsRemaining;
    private enum TimerState {READY, RUNNING, PAUSED }
    private TimerState currentState = TimerState.READY;
    private NotificationHelper notificationHelper;
    private final Context context;
    private boolean isInitialized;
    private TimerService timerService;
    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    private Future<?> future;
    private boolean isAfterReset = true;
    private String currentTimeText = "";


    public CountdownTimer(Context context){
        this.context = context;
    }


    public void setAndUpdateView(MainView view, TimerService timerService){
        this.view = view;
        this.timerService = timerService;
        setCurrentTimeText();
        setCurrentCountdownValueOnMainView();
        switch (currentState){
            case READY: view.updateForReadyState(); break;
            case RUNNING: view.updateForRunningState(); break;
            case PAUSED: view.updateForPausedState();
        }
    }


    public void setNotificationHelper(NotificationHelper notificationHelper){
        this.notificationHelper = notificationHelper;
    }


    public boolean isInitialized(){
        return isInitialized;
    }


    public void resetTime(){
        setMillisecondsRemaining();
        setCurrentTimeText();
        isAfterReset = true;
        if(view != null) {
            view.setCurrentCountdownValue(getCurrentTimeText(), false);
            resetStartButton();
        }
        if(currentState == TimerState.RUNNING){
            cancelCountdownTask();
            startTimer();
            return;
        }
        currentState = TimerState.READY;
        updateNotification();
    }


    public String getCurrentTimeText(){
        return currentTimeText;
    }


    public void setCurrentTimeText(){
        int minutes = getMinutesPart();
        int seconds = getSecondsPart();
        currentTimeText = getClockStringFor(minutes) + ":" + getClockStringFor(seconds);
    }


    public void setTime(int minutes, int seconds){
        isInitialized = true;
        int SECONDS_PER_MINUTE = 60;
        timerStartingValue = (minutes * SECONDS_PER_MINUTE) + seconds;
        updateCurrentSecondsIfTimerIsStopped(minutes, seconds);
    }


    private void resetStartButton(){
        if(currentState != TimerState.RUNNING && view != null){
            view.enableAndShowStartButton();
        }
    }


    private void setMillisecondsRemaining(){
        millisecondsRemaining = timerStartingValue * 1000;
    }


    private void updateCurrentSecondsIfTimerIsStopped(int minutes, int seconds){
        if(currentState == TimerState.READY){
            setMillisecondsRemaining();
            if(view != null) {
                view.resetCurrentCountdownValue(minutes, seconds);
                updateNotification();
            }
        }
    }


    private int getMinutesPart(){
        return getAllSeconds() / 60;
    }


    private int getSecondsPart(){
        return getAllSeconds() % 60;
    }


    private int getAllSeconds(){
        return millisecondsRemaining / 1000;
    }


    public void startStop(){
        if(currentState == TimerState.RUNNING){
            pauseTimer();
            return;
        }
        startTimer();
    }


    private void startTimer() {
        initiateTask();
        currentState = TimerState.RUNNING;
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
        cancelCountdownTask();
        if(view == null){
            return;
        }
        view.showResumeButton();
        view.enableSetButton();
        updateNotification();
    }


    private void updateNotification(){
       notificationHelper.updateNotification(getStatusStr(), getCurrentTimeText());
    }


    private String getStatusStr(){
        int statusResId = currentState == TimerState.READY ? R.string.notification_state_ready
                : currentState == TimerState.PAUSED ? R.string.notification_state_paused
                : R.string.notification_state_counting_down;
        return getStr(statusResId);
    }


    private void stopTimer(){
        currentState = TimerState.READY;
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


    private boolean isCritical(int currentTimeLeft){
        return currentTimeLeft <= 5
                || (timerStartingValue >= 60 && currentTimeLeft <= 15)
                || (timerStartingValue >= 600 && currentTimeLeft <= 30);
    }


    private void countDownAHundredMilliseconds(){
        decrementMillisecondsRemaining();
        setCurrentTimeText();
        setCurrentCountdownValueOnMainView();
        updateCountdownNotification();
        handleTimesUp();
    }


    private void decrementMillisecondsRemaining(){
        millisecondsRemaining = Math.max(millisecondsRemaining - 100, 0);
    }


    private void setCurrentCountdownValueOnMainView() {
        if(view == null){
            return;
        }
        view.setCurrentCountdownValue(getCurrentTimeText(), isCritical(getAllSeconds()));
    }


    private void updateCountdownNotification(){
        if(millisecondsRemaining >= 1000) {
            notificationHelper.updateCountdown(getStr(R.string.notification_state_counting_down), getCurrentTimeText());
        }
    }


    private String getClockStringFor(int number){
        String numberStr = String.valueOf(number);
        return number < 10 ? "0" + numberStr : numberStr;
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