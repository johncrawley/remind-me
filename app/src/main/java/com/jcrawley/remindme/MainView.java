package com.jcrawley.remindme;

public interface MainView {

 void setCurrentCountdownValue(String currentTimeText, boolean isCritical);
 void resetCurrentCountdownValue(String time);

 void notifyTimerStarted();
 void notifyPaused();
 void notifyResetWhenTimerStopped();
 void notifyTimesUp(String timesUpMessage);

 void updateForReadyState();
 void updateForRunningState();
 void updateForPausedState();
}
