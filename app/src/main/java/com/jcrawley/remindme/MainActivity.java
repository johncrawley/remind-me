package com.jcrawley.remindme;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private TextView currentCountdownValue;
    private Button setButton, startStopButton;
    private CountdownTimer countdownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupViews();
        countdownTimer = new CountdownTimer(this, 5);
    }


    private void setupViews(){
        currentCountdownValue = findViewById(R.id.currentCountdownText);
        startStopButton = findViewById(R.id.startStopButton);
        setButton = findViewById(R.id.setButton);
        setButton.setOnClickListener(this);
        startStopButton.setOnClickListener(this);
    }

    public void issueNotification(){
        log("Entered issueNotification()");
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


    public void enableAndShowStartButton() {
        this.startStopButton.setEnabled(true);
        this.startStopButton.setText(getResources().getString(R.string.button_start_label));
    }


    public void changeCountdownColorOff() {
        setCountdownTextColor(R.color.colorTimerTextOff);
    }


   // public void changeCountdownColorOn() {
     //   setCountdownTextColor(R.color.colorTimerTextOn);
    //}


    public void enableSetButton() {
        setButton.setEnabled(true);
    }


    public void disableStartButton() {
        startStopButton.setEnabled(false);
    }


    public void showPauseButton(){
        startStopButton.setText(getString(R.string.button_pause_label));
    }


    public void showResetButton(){
        setButton.setText(getString(R.string.button_reset_label));
    }


    public void showStartButton(){
        startStopButton.setText(getString(R.string.button_start_label));
    }


    public void showResumeButton(){
        startStopButton.setText(getString(R.string.button_resume_label));
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

        if(id == R.id.setButton){
            //countdownTimer.adjustTime();
            countdownTimer.adjustTimeTest(4);
            return;
        }
        if(id == R.id.startStopButton){
            countdownTimer.startStop();

        }
    }


    private void log(String msg){
        System.out.println("RemindMe MainActivity: " +  msg);
        System.out.flush();
    }


}