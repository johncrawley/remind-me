package com.jcrawley.remindme;

public class Settings {
    private final int seconds;
    private final int minutes;
    private final String timesUpMessage;

    public Settings(int seconds, int minutes, String timesUpMessage){
        this.seconds = seconds;
        this.minutes = minutes;
        this.timesUpMessage = timesUpMessage;
    }


    public int getSeconds(){
        return this.seconds;
    }


    public int getMinutes(){
        return this.minutes;
    }


    public String getTimesUpMessage(){
        return this.timesUpMessage;
    }
}
