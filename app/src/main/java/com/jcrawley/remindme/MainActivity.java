package com.jcrawley.remindme;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;


import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.TextView;
import com.jcrawley.remindme.service.TimerService;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, MainView {


    private TextView currentCountdownText;
    private TextView timesUpMessageText;
    private Button setButton, startStopButton;
    private MainViewModel viewModel;
    private boolean isInFront;
    private Animation displayTimesUpTextAnimation;
    private TimerService timerService;


    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            TimerService.LocalBinder binder = (TimerService.LocalBinder) service;
            timerService = binder.getService();
            timerService.setView(MainActivity.this);
            timerService.setTime(Integer.parseInt(viewModel.mins), Integer.parseInt(viewModel.secs));
            log("Service connected");
        }
        @Override public void onServiceDisconnected(ComponentName arg0) {}
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupViews();
        setupViewModel();
        initAnimation();
        startForegroundService();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_buttons, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.menu_button_configure) {
            startConfigureDialogFragment();
        }
        return super.onOptionsItemSelected(item);
    }


    private void startForegroundService(){
        Intent mediaPlayerServiceIntent = new Intent(this, TimerService.class);
        getApplicationContext().startForegroundService(mediaPlayerServiceIntent);
        getApplicationContext().bindService(mediaPlayerServiceIntent, serviceConnection, 0);
    }


    private void log(String msg){
        System.out.println("^^^ MainActivity: " + msg);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

    }


    @Override
    public void onResume(){
        isInFront = true;
        FragmentTransaction ft =  getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        ft.commit();
        super.onResume();
    }


    @Override
    public void onPause() {
        super.onPause();
        isInFront = false;
    }


    private void setupViewModel(){
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        if(viewModel.hasBeenInitialized){
            return;
        }
        viewModel.hasBeenInitialized = true;
        Preferences preferences = new Preferences(MainActivity.this);
        Settings settings = preferences.getSettings();
        viewModel.secs = "" + settings.getSeconds();
        viewModel.mins = "" + settings.getMinutes();
        viewModel.reminderMessage = settings.getTimesUpMessage();
        setCurrentCountdownValue(Integer.parseInt(viewModel.mins), Integer.parseInt(viewModel.secs));
    }


    private void setupViews(){
        currentCountdownText = findViewById(R.id.currentCountdownText);
        timesUpMessageText = findViewById(R.id.timesUpMessageText);
        startStopButton = findViewById(R.id.startStopButton);
        setButton = findViewById(R.id.setButton);
        setClickListener(setButton, startStopButton, currentCountdownText);
    }


    private void setClickListener(View... views){
        for(View v : views){
            v.setOnClickListener(this);
        }
    }


    private void startConfigureDialogFragment(){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        ConfigureDialogFragment configureDialogFragment = ConfigureDialogFragment.newInstance();
        configureDialogFragment.show(ft, "dialog");
    }


    public void notifyTimesUp(){
        showTimesUpText();
        //playSound();
    }


    private void initAnimation(){
        displayTimesUpTextAnimation = new ScaleAnimation(1.0f,1.0f, 0.1f, 1.0f, 0.5f, 1f);
        displayTimesUpTextAnimation.setDuration(500);
        displayTimesUpTextAnimation.setFillAfter(true);
    }


    private void showTimesUpText(){
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            timesUpMessageText.setVisibility(View.VISIBLE);
            timesUpMessageText.startAnimation(displayTimesUpTextAnimation);
        });
    }


    private void hideTimesUpText(){
        timesUpMessageText.clearAnimation();
        timesUpMessageText.setVisibility(View.INVISIBLE);
    }


    public void setCurrentCountdownValue(int currentMinutes, int currentSeconds) {
        runOnUiThread(() -> {
            String text = getTimeText(currentMinutes) + " : " + getTimeText(currentSeconds);
            currentCountdownText.setText(text);
        });
    }


    public void enableAndShowStartButton() {
        this.startStopButton.setEnabled(true);
        this.startStopButton.setText(getResources().getString(R.string.button_start_label));
    }


    public void changeCountdownColorOff() {
        setCountdownTextColor(R.color.colorTimerTextOff);
    }


    @Override
    public void setTimerRunningStatus(CountdownTimer.TimerState timerState) {
        switch (timerState){
            case RUNNING:
                showPauseButton();
                enableSetButton();
                showResetButton();
                break;
            case STOPPED:
                showStartButton();
                showResetButton();
                break;

            case PAUSED:
                showResumeButton();
                enableSetButton();
                showResetButton();
        }
    }


    public void enableSetButton() {
        setButton.setEnabled(true);
    }


    public void disableStartButton() {
        runOnUiThread(() -> startStopButton.setEnabled(false) );
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
            timerService.resetTime();
            hideTimesUpText();
        }
        else if(id == R.id.startStopButton){
            timerService.startStop();
        }
    }


    public void handleDialogClose(int minutes, int seconds) {
        setCurrentCountdownValue(minutes, seconds);
        if(timerService != null) {
            timerService.setTime(minutes, seconds);
        }
    }


    private String getTimeText(int timeValue){
        return timeValue > 9 ?
                "" + timeValue
                : "0" + timeValue;
    }


}