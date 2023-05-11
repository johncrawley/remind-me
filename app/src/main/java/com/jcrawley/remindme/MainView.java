package com.jcrawley.remindme;

public interface MainView {

        void showPauseButton();
        void showResetButton();
        void setCurrentCountdownValue(int minutes, int seconds, boolean isCritical);
        void setCurrentCountdownValue(String currentTimeText, boolean isCritical);
        void resetCurrentCountdownValue(int minutes, int seconds);
        void notifyTimesUp(String timesUpMessage);
        void showStartButton();
        void disableStartButton();
        void enableAndShowStartButton();
        void showResumeButton();
        void enableSetButton();
        void changeCountdownColorOff();

        void updateForReadyState();
        void updateForRunningState();
        void updateForPausedState();
}
