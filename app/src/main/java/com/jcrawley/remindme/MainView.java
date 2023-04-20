package com.jcrawley.remindme;

public interface MainView {
    
    
        void showPauseButton();
        void showResetButton();
        void setCurrentCountdownValue(int minutes, int seconds);
        void issueNotification();
        void showStartButton();
        void disableStartButton();
        void enableAndShowStartButton();
        void showResumeButton();
        void enableSetButton();
        void changeCountdownColorOff();
        void setTimerRunningStatus(CountdownTimer.TimerState timerState);
}
