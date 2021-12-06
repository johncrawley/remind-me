package com.jcrawley.remindme;

import android.content.Context;
import android.content.SharedPreferences;

public class Preferences {

    private final Context context;
    private final String PREFERENCES_NAME = "reminderPreferences";
    private final String PREF_NAME_MINUTES= "minutes";
    private final String PREF_NAME_SECONDS= "seconds";
    private final String PREF_NAME_TIMES_UP_MESSAGE= "times_up_message";

    Preferences(Context context){
        this.context = context;
    }


    public Settings getSettings(){
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCES_NAME,0);
        int seconds = prefs.getInt(PREF_NAME_SECONDS, 0);
        int minutes = prefs.getInt(PREF_NAME_MINUTES, 5);
        String message = prefs.getString(PREF_NAME_TIMES_UP_MESSAGE, "Times UP!");
        return new Settings(seconds, minutes, message);
    }


    public void saveSettings(int seconds, int minutes, String message){
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFERENCES_NAME,0).edit();
        editor.putInt(PREF_NAME_SECONDS, seconds );
        editor.putInt(PREF_NAME_MINUTES, minutes );
        editor.putString(PREF_NAME_TIMES_UP_MESSAGE, message );
        editor.apply();
    }

}
