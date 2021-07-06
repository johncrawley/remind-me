package com.jcrawley.remindme;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private TextView currentCountdownValue;
    private Button setButton, startStopButton;
    private CountdownTimer controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        currentCountdownValue = findViewById(R.id.currentCountdownText);
        startStopButton = findViewById(R.id.startStopButton);
        setButton = findViewById(R.id.setButton);
        controller = new CountdownTimer(this);
        setCurrentCountdownValue(15,0);
        setButton.setOnClickListener(this);
        startStopButton.setOnClickListener(this);
    }


    public void setCurrentTime(String currentTime) {
        currentCountdownValue.setText(currentTime);
    }


    public void issueNotification(){
        TimesUpNotifier timesUpNotifier = new TimesUpNotifier(MainActivity.this);
        timesUpNotifier.issueNotification();
    }


    public void setCurrentCountdownValue(int currentMinutes, int currentSeconds) {
        final String currentSecondsText =  currentSeconds > 9 ? "" + currentSeconds : "0" + currentSeconds;
        final String currentMinutesText =  currentMinutes > 9 ? "" + currentMinutes : "0" + currentMinutes;
        runOnUiThread(() -> {
            String text = currentMinutesText + " : " + currentSecondsText;
            currentCountdownValue.setText(text);});
    }


    public void enableStartButton() {
        this.startStopButton.setText(getResources().getString(R.string.button_start_label));

    }
    public void enableStopButton(){
        this.startStopButton.setText(getResources().getString(R.string.button_stop_label));
    }


    public void changeCountdownColorOff() {
        setCountdownTextColor(R.color.colorTimerTextOff);
    }


    public void changeCountdownColorOn() {
        setCountdownTextColor(R.color.colorTimerTextOn);
    }


    public void enableSetButton() {
        setButton.setEnabled(true);
    }


    private void setCountdownTextColor(int colorId){
        currentCountdownValue.setTextColor(getResources().getColor(colorId, null));
    }


    public void disableSetButton() {
        setButton.setEnabled(false);
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();

        log("Click registered! view id  = " + id);
        if(id == R.id.setButton){
            controller.adjustTime();
            log("set button clicked");
            return;
        }
        if(id == R.id.startStopButton){
            controller.startStop();

            log("start/stop button clicked");

        }

    }

    private void log(String msg){
        Log.i("MainActivity", msg);
    }
}