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

public class MainActivity extends AppCompatActivity implements MainView {


    private TextView currentCountdownText;
    private TextView timesUpMessageText;
    private Button setButton, startStopButton;
    private MainViewModel viewModel;
    private Animation displayTimesUpTextAnimation;
    private TimerService timerService;


    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            TimerService.LocalBinder binder = (TimerService.LocalBinder) service;
            timerService = binder.getService();
            timerService.setView(MainActivity.this);
            timerService.setTime(viewModel.initialMinutes, viewModel.initialSeconds);
            log("Service connected, time set from viewModel: " + viewModel.initialMinutes + ":" + viewModel.initialSeconds);
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
        updateTimerTextColor();
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
        super.onResume();
    }


    @Override
    public void onPause() {
        super.onPause();
    }


    private void setupViewModel(){
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        if(viewModel.hasBeenInitialized){
            return;
        }
        viewModel.hasBeenInitialized = true;
        TimerPreferences timerPreferences = new TimerPreferences(MainActivity.this);
        Settings settings = timerPreferences.getSettings();
        viewModel.initialSeconds = String.valueOf(settings.getSeconds());
        viewModel.initialMinutes = String.valueOf(settings.getMinutes());
        viewModel.reminderMessage = settings.getTimesUpMessage();
    }


    private void setupViews(){
        currentCountdownText = findViewById(R.id.currentCountdownText);
        timesUpMessageText = findViewById(R.id.timesUpMessageText);
        startStopButton = findViewById(R.id.startStopButton);
        startStopButton.setOnClickListener((View v) -> timerService.startStop());
        setButton = findViewById(R.id.setButton);
        setButton.setOnClickListener((View v)->{
            timerService.resetTime();
            hideTimesUpText();
        });
    }


    private void startConfigureDialogFragment(){
        String tagName = "RemindMeConfigDialog";
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag(tagName);
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        SettingsDialogFragment settingsDialogFragment = SettingsDialogFragment.newInstance();
        settingsDialogFragment.show(ft, tagName);
    }


    public void notifyTimesUp(String timesUpMessage){
        showTimesUpText(timesUpMessage);
        //playSound();
    }


    private void initAnimation(){
        displayTimesUpTextAnimation = new ScaleAnimation(1.0f,1.0f, 0.1f, 1.0f, 0.5f, 1f);
        displayTimesUpTextAnimation.setDuration(500);
        displayTimesUpTextAnimation.setFillAfter(true);
    }


    private void showTimesUpText(String timesUpMessage){
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            timesUpMessageText.setText(timesUpMessage);
            timesUpMessageText.setVisibility(View.VISIBLE);
            timesUpMessageText.startAnimation(displayTimesUpTextAnimation);
        });
    }


    private void hideTimesUpText(){
        timesUpMessageText.clearAnimation();
        timesUpMessageText.setVisibility(View.INVISIBLE);
    }


    public void resetCurrentCountdownValue(int currentMinutes, int currentSeconds) {
        setCurrentCountdownValue(currentMinutes, currentSeconds, false);
    }


    public void setCurrentCountdownValue(int currentMinutes, int currentSeconds, boolean isCritical) {
        runOnUiThread(() -> {
            String text = getTimeText(currentMinutes) + " : " + getTimeText(currentSeconds);
            currentCountdownText.setText(text);
            viewModel.isTimeLeftCritical = isCritical;
            updateTimerTextColor();
        });
    }


    public void enableAndShowStartButton() {
        this.startStopButton.setEnabled(true);
        this.startStopButton.setText(getResources().getString(R.string.button_start_label));
    }


    public void updateTimerTextColor(){
        int colorId = viewModel.isTimeLeftCritical ? R.color.dark_timer_text_critical : R.color.dark_timer_text_normal;
        currentCountdownText.setTextColor(getResources().getColor(colorId, null));
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
            case READY:
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


    public void assignSettings(int minutes, int seconds, String message) {
        if(timerService != null) {
            timerService.savePreferences(minutes, seconds, message);
        }
    }


    private String getTimeText(int timeValue){
        return timeValue > 9 ?
                "" + timeValue
                : "0" + timeValue;
    }


}