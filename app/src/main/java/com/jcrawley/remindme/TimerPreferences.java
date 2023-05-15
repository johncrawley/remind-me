package com.jcrawley.remindme;

import android.content.Context;
import android.content.SharedPreferences;

public class TimerPreferences {

    private final Context context;
    private final String PREFERENCES_NAME = "reminderPreferences";
    private final String PREF_NAME_MINUTES= "minutes";
    private final String PREF_NAME_MINUTES_LARGE_DIGIT= "minutes_large_digit";
    private final String PREF_NAME_MINUTES_SMALL_DIGIT= "minutes_small_digit";
    private final String PREF_NAME_SECONDS= "seconds";
    private final String PREF_NAME_SECONDS_LARGE_DIGIT= "seconds_large_digit";
    private final String PREF_NAME_SECONDS_SMALL_DIGIT= "seconds_small_digit";
    private final String PREF_NAME_TIMES_UP_MESSAGE= "times_up_message";

    public TimerPreferences(Context context){
        this.context = context;
    }


    public Settings getSettings(){
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCES_NAME,0);
        int seconds = prefs.getInt(PREF_NAME_SECONDS, 0);
        int minutes = prefs.getInt(PREF_NAME_MINUTES, 5);
        String message = prefs.getString(PREF_NAME_TIMES_UP_MESSAGE, "Times Up!");
        Settings settings = new Settings(seconds, minutes, message);

        settings.minutesLargeDigit = prefs.getInt(PREF_NAME_MINUTES_LARGE_DIGIT, 0);
        settings.minutesSmallDigit = prefs.getInt(PREF_NAME_MINUTES_SMALL_DIGIT, 0);
        settings.secondsLargeDigit = prefs.getInt(PREF_NAME_SECONDS_LARGE_DIGIT, 0);
        settings.secondsSmallDigit = prefs.getInt(PREF_NAME_SECONDS_SMALL_DIGIT, 0);
        return settings;
    }


    public void saveSettings(int seconds, int minutes, String message){
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFERENCES_NAME,0).edit();
        editor.putInt(PREF_NAME_SECONDS, seconds );
        editor.putInt(PREF_NAME_MINUTES, minutes );
        editor.putString(PREF_NAME_TIMES_UP_MESSAGE, message );
        editor.apply();
    }


    public void saveSettings(Settings settings){
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFERENCES_NAME,0).edit();
        editor.putInt(PREF_NAME_SECONDS, settings.getSeconds() );
        editor.putInt(PREF_NAME_MINUTES, settings.getMinutes() );
        editor.putString(PREF_NAME_TIMES_UP_MESSAGE, settings.getTimesUpMessage() );

        editor.putInt(PREF_NAME_MINUTES_LARGE_DIGIT, settings.minutesLargeDigit);
        editor.putInt(PREF_NAME_MINUTES_SMALL_DIGIT, settings.minutesSmallDigit);
        editor.putInt(PREF_NAME_SECONDS_LARGE_DIGIT, settings.secondsLargeDigit);
        editor.putInt(PREF_NAME_SECONDS_SMALL_DIGIT, settings.secondsSmallDigit);
        editor.apply();
    }

}
