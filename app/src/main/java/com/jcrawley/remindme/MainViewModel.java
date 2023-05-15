package com.jcrawley.remindme;

import androidx.lifecycle.ViewModel;

public class MainViewModel extends ViewModel {

    public String initialMinutes = "5";
    public String initialSeconds = "0";

    public String initialMinutesLargeDigit = "0";
    public String initialMinutesSmallDigit = "5";
    public String initialSecondsLargeDigit = "0";
    public String initialSecondsSmallDigit = "0";

    public String reminderMessage = "Time's Up!";
    public boolean isTimeLeftCritical = false;

    public boolean hasBeenInitialized = false;

}
