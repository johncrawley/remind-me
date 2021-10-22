package com.jcrawley.remindme;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, CustomDialogCloseListener {


    private TextView currentCountdownText;
    private Button setButton, startStopButton;
    private CountdownTimer countdownTimer;
    private MainViewModel viewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupViews();
        setupViewModel();
        countdownTimer = new CountdownTimer(this, 5);
    }


    private void setupViewModel(){
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
    }


    private void setupViews(){
        currentCountdownText = findViewById(R.id.currentCountdownText);
        startStopButton = findViewById(R.id.startStopButton);
        setButton = findViewById(R.id.setButton);
        setClickListener(setButton, startStopButton, currentCountdownText);
    }


    public void onResume(){
        FragmentTransaction ft =  getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        ft.commit();
        super.onResume();
    }


    private void printMessageToLog(){
        System.out.println("message: " + viewModel.getMessage().getValue());
    }


    private void setClickListener(View... views){
        for(View v : views){
            v.setOnClickListener(this);
        }
    }


    private void startDialogFragment(){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        ConfigureDialogFragment configureDialogFragment = ConfigureDialogFragment.newInstance();
        configureDialogFragment.show(ft, "dialog");
    }


    public void issueNotification(){
        runOnUiThread(() -> {
            TimesUpNotifier timesUpNotifier = new TimesUpNotifier(MainActivity.this, viewModel);
            timesUpNotifier.issueNotification();
        });
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


    @Override
    public void onClick(View view) {
        int id = view.getId();

        if(id == R.id.setButton){
            countdownTimer.resetTime();
        }
        else if(id == R.id.startStopButton){
            countdownTimer.startStop();
            printMessageToLog();
        }
        else if(id == R.id.currentCountdownText){
            startDialogFragment();
        }
    }


    @Override
    public void handleDialogClose(DialogInterface dialogInterface, int minutes, int seconds) {
        setCurrentCountdownValue(minutes, seconds);
        if(countdownTimer != null) {
            countdownTimer.setTime(minutes, seconds);
        }
    }
}