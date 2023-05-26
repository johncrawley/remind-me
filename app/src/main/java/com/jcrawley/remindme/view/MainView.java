package com.jcrawley.remindme.view;

public interface MainView {

 void setCurrentCountdownValue(String currentMinutes, String currentSeconds, boolean isCritical);

 void notifyTimerStarted();
 void notifyPaused();
 void notifyResetWhenTimerStopped();
 void notifyTimesUp(String timesUpMessage);

 void updateForReadyState();
 void updateForRunningState();
 void updateForPausedState();
 void updateForTimesUpState(String timesUpText);
}
