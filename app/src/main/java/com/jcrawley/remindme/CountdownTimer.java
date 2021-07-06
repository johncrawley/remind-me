package com.jcrawley.remindme;

import com.jcrawley.remindme.tasks.DebugTimerThreadRunner;
import com.jcrawley.remindme.tasks.TimerThreadRunner;

public class CountdownTimer  {

    private final MainActivity view;
    private final int A_SECOND = 1000;
    private final int A_MINUTE = 60;
    private final int MINUTES = A_MINUTE * A_SECOND;
    private final int TIMER_MIN = MINUTES;
    private int timerStartingValue;
    private TimerThreadRunner timerThreadRunner;
    private boolean isTimerRunning = false;
    private int currentSeconds;


    public CountdownTimer(MainActivity view){
        this.view = view;
        this.timerStartingValue = TIMER_MIN;
        view.setCurrentCountdownValue(getClockMinutes(TIMER_MIN), getClockSeconds(TIMER_MIN));
        //timerThreadRunner = new TimerThreadRunner();
        timerThreadRunner = new DebugTimerThreadRunner();
    }


    public void adjustTime() {
        final int INTERVAL_INCREMENTS = 5 * MINUTES;
        final int TIMER_MAX = 60 * MINUTES;
        timerStartingValue += INTERVAL_INCREMENTS;
        if(timerStartingValue > TIMER_MAX){
            timerStartingValue = TIMER_MIN;
        }
        view.setCurrentCountdownValue( timerStartingValue / MINUTES, 0);
    }


    public void startTimer() {
        isTimerRunning = true;
        timerThreadRunner.startTimer(timerStartingValue, this );
        view.enableStopButton();
        view.changeCountdownColorOff();
        view.disableSetButton();
    }



    public void startStop(){
        if(isTimerRunning){
            this.stopTimer();
            return;
        }
        this.startTimer();
    }


    public void stopTimer() {
        isTimerRunning = false;
        view.setCurrentCountdownValue(getClockMinutes(timerStartingValue), getClockSeconds(timerStartingValue));
        view.enableStartButton();
        view.enableSetButton();
        view.changeCountdownColorOff();
        timerThreadRunner.stopTimer();
    }


    public void setCurrentCountdownValue(int currentValue) {
        // this.currentCountdownValue = currentValue;
        int clockMinutes = getClockMinutes(currentValue);
        int clockSeconds = getClockSeconds(currentValue);
        view.setCurrentCountdownValue(clockMinutes, clockSeconds);
    }

    public void countdownOneSecond(){
        currentSeconds = currentSeconds < 0 ? 0 : currentSeconds -1;
        if(currentSeconds == 0){
            stopTimer();
        }
    }


    private String getClockString(int millis){
        int totalSeconds = millis / A_SECOND;
        int minutes = totalSeconds / A_MINUTE;
        int seconds = totalSeconds % minutes;
        return minutes + " : " + seconds;
    }


    private int getClockSeconds(int millis){
        return (millis / A_SECOND) % A_MINUTE;
    }

    private int getClockMinutes(int millis){
        return  millis / MINUTES;
    }


    public void onCountdownComplete(){
        view.issueNotification();
        stopTimer();
    }

}