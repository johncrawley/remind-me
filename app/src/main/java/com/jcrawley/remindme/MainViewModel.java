package com.jcrawley.remindme;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MainViewModel extends ViewModel {


    private MutableLiveData<String> message;
    private MutableLiveData<String> minutes;
    private MutableLiveData<String> seconds;

    public String mins;
    public String secs;
    public String reminderMessage;

    public MutableLiveData<String> getMessage(){
        if(message == null){
            message = new MutableLiveData<>();
        }
        return message;
    }

    public MutableLiveData<String> getMinutes(){
        if(minutes == null){
            minutes = new MutableLiveData<>();
        }
        return minutes;
    }

    public MutableLiveData<String> getSeconds(){
        if(seconds == null){
            seconds = new MutableLiveData<>();
        }
        return seconds;
    }
}
