package com.jcrawley.remindme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private TextView currentCountdownText;
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
        currentCountdownText = findViewById(R.id.currentCountdownText);
        startStopButton = findViewById(R.id.startStopButton);
        setButton = findViewById(R.id.setButton);
        setClickListener(setButton, startStopButton, currentCountdownText);
    }


    private void setClickListener(View... views){
        for(View v : views){
            v.setOnClickListener(this);
        }
    }


    public void issueNotification(){
        runOnUiThread(() -> {
            TimesUpNotifier timesUpNotifier = new TimesUpNotifier(MainActivity.this);
            timesUpNotifier.issueNotification();
        });
    }


    public void showKeyboard(){
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }


    public void closeKeyboard(){
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }


    public void setCurrentCountdownValue(int currentMinutes, int currentSeconds) {
        final String currentSecondsText =  currentSeconds > 9 ? "" + currentSeconds : "0" + currentSeconds;
        final String currentMinutesText =  currentMinutes > 9 ? "" + currentMinutes : "0" + currentMinutes;
        runOnUiThread(() -> {
            String text = currentMinutesText + " : " + currentSecondsText;
            currentCountdownText.setText(text);});
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
        currentCountdownText.setTextColor(getResources().getColor(colorId, null));
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
        else if(id == R.id.currentCountdownText){
            startActivity(new Intent(this, ConfigureDialogActivity.class));
        }
    }


    private void log(String msg){
        System.out.println("RemindMe MainActivity: " +  msg);
        System.out.flush();
    }


}