package com.jcrawley.remindme;

import androidx.lifecycle.ViewModel;

public class MainViewModel extends ViewModel {

    public String mins = "5";
    public String secs = "0";
    public String reminderMessage = "Time's Up!";
    public boolean doesTimerStopOnReset;

    public boolean hasBeenInitialized = false;

}
