package com.jcrawley.remindme;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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
        // Create the observer which updates the UI.
        final Observer<String> nameObserver = new Observer<String>() {
            @Override
            public void onChanged(@Nullable final String newName) {
                // Update the UI, in this case, a TextView.
                currentCountdownText.setText(newName);
            }
        };

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.getMessage().observe(this, nameObserver);
    }


    public MainViewModel getViewModel(){
        return viewModel;
    }


    private void setupViews(){
        currentCountdownText = findViewById(R.id.currentCountdownText);
        startStopButton = findViewById(R.id.startStopButton);
        setButton = findViewById(R.id.setButton);
        setClickListener(setButton, startStopButton, currentCountdownText);
    }

    public void onResume(){
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
        FragmentManager fm = getSupportFragmentManager();
        ConfigureDialogFragment dialogFragment = new ConfigureDialogFragment();
        dialogFragment.show(fm, "tag");
       // dialogFragment.
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

    public void setCurrentCountdownText(String countdownText){
        int seconds, minutes;
        if(!countdownText.contains(".")){
            int countdownNumber = Integer.parseInt(countdownText);
            seconds = countdownNumber % 60;
            minutes = countdownNumber / 60;
        }
        else{


        }

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
            countdownTimer.resetTime();
            return;
        }
        if(id == R.id.startStopButton){
            countdownTimer.startStop();
            printMessageToLog();
        }
        else if(id == R.id.currentCountdownText){
           // startActivity(new Intent(this, ConfigureDialogActivity.class));
            startDialogFragment();
        }
    }


    private void log(String msg){
        System.out.println("RemindMe MainActivity: " +  msg);
        System.out.flush();
    }


    @Override
    public void handleDialogClose(DialogInterface dialogInterface, int minutes, int seconds) {
        setCurrentCountdownValue(minutes, seconds);
        if(countdownTimer != null) {
            countdownTimer.setTime(minutes, seconds);
        }
    }
}