package com.jcrawley.remindme;

import android.app.Notification;
import android.content.Context;
import android.media.MediaPlayer;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CountdownTimer  {

    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    private final NotificationHelper notificationHelper;
    private final Context context;
    private MainView view;
    private int timerStartingValue;
    private int millisecondsRemaining;
    private enum TimerState {READY, RUNNING, PAUSED }
    private TimerState currentState = TimerState.READY;
    private Future<?> future;
    private boolean isAfterReset = true;
    private String currentTimeText = "";
    private String timesUpMessage = "";


    public CountdownTimer(Context context){
        this.context = context;
        notificationHelper = new NotificationHelper(context);
    }


    public void startStop(){
        if(currentState == TimerState.RUNNING){
            pauseTimer();
            return;
        }
        startTimer();
    }


    public Notification getInitialNotification(){
        return notificationHelper.createNotification(getStatusStr(), getCurrentTimeText());
    }


    public void setAndUpdateView(MainView view){
        this.view = view;
        updateCurrentTimeText();
        setCurrentCountdownValueOnMainView();
        switch (currentState){
            case READY: view.updateForReadyState(); break;
            case RUNNING: view.updateForRunningState(); break;
            case PAUSED: view.updateForPausedState();
        }
    }


    public void resetTime(){
        setMillisecondsRemaining();
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


    public void setTime(int minutes, int seconds, String message){
        int SECONDS_PER_MINUTE = 60;
        timerStartingValue = (minutes * SECONDS_PER_MINUTE) + seconds;
        updateCurrentSecondsIfTimerIsStopped();
        timesUpMessage = message;
    }


    private String getCurrentTimeText(){
        return currentTimeText;
    }


    private void updateCurrentTimeText(){
        int minutes = getMinutesPart();
        int seconds = getSecondsPart();
        currentTimeText = getClockStringFor(minutes) + " : " + getClockStringFor(seconds);
    }


    private void resetStartButton(){
        if(currentState != TimerState.RUNNING){
            view.notifyResetWhenTimerStopped();
        }
    }


    private void setMillisecondsRemaining(){
        millisecondsRemaining = timerStartingValue * 1000;
        updateCurrentTimeText();
    }


    private void updateCurrentSecondsIfTimerIsStopped(){
        log("Entered updateCurrentSecondsIfTimerIsStopped() timer state is: " + currentState);
        if(currentState == TimerState.READY){
            setMillisecondsRemaining();
            if(view != null) {
                log("view is not null, setting currentTimeText: " + currentTimeText);
                view.resetCurrentCountdownValue(getCurrentTimeText());
                updateNotification();
            }
        }
    }


    private void log(String msg){
        System.out.println("^^^ CountdownTimer: " + msg);
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


    private void startTimer() {
        initiateTask();
        currentState = TimerState.RUNNING;
        if(view == null){
            return;
        }
        view.notifyTimerStarted();
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
        view.notifyPaused();
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
        updateCurrentTimeText();
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
        notifyViewOfTimesUp();
        currentState = TimerState.READY;
        cancelCountdownTask();
        playSoundOnTimesUp();
        notificationHelper.sendTimesUpNotification(timesUpMessage);
    }


    private void notifyViewOfTimesUp(){
        if(view == null){
            return;
        }
        view.notifyTimesUp(timesUpMessage);
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