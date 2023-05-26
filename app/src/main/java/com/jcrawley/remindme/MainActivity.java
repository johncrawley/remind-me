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

import com.jcrawley.remindme.preferences.Settings;
import com.jcrawley.remindme.preferences.TimerPreferences;
import com.jcrawley.remindme.service.TimerService;
import com.jcrawley.remindme.view.ConfigDialogFragment;
import com.jcrawley.remindme.view.MainView;
import com.jcrawley.remindme.view.MainViewModel;

public class MainActivity extends AppCompatActivity implements MainView {

    private TextView currentCountdownMinutesText, currentCountdownSecondsText, currentCountdownDelimiterText;
    private TextView timesUpMessageText;
    private Button resetButton, startStopButton;
    private MainViewModel viewModel;
    private Animation displayTimesUpTextAnimation;
    private TimerService timerService;


    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            TimerService.LocalBinder binder = (TimerService.LocalBinder) service;
            timerService = binder.getService();
            timerService.setView(MainActivity.this);
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
        viewModel.initialMinutesLargeDigit = String.valueOf(settings.minutesLargeDigit);
        viewModel.initialMinutesSmallDigit = String.valueOf(settings.minutesSmallDigit);
        viewModel.initialSecondsLargeDigit = String.valueOf(settings.secondsLargeDigit);
        viewModel.initialSecondsSmallDigit = String.valueOf(settings.secondsSmallDigit);

        viewModel.initialSeconds = String.valueOf(settings.getSeconds());
        viewModel.initialMinutes = String.valueOf(settings.getMinutes());
        viewModel.reminderMessage = settings.getTimesUpMessage();
    }


    private void setupViews(){
        currentCountdownMinutesText = findViewById(R.id.currentCountdownMinutes);
        currentCountdownSecondsText = findViewById(R.id.currentCountdownSeconds);
        currentCountdownDelimiterText = findViewById(R.id.currentCountdownDelimiter);
        timesUpMessageText = findViewById(R.id.timesUpMessageText);
        startStopButton = findViewById(R.id.startStopButton);
        startStopButton.setOnClickListener((View v) -> timerService.startStop());
        resetButton = findViewById(R.id.resetButton);
        resetButton.setOnClickListener((View v)->{
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
        ConfigDialogFragment.newInstance().show(ft, tagName);
    }


    @Override
    public void notifyTimerStarted(){
        showPauseButton();
        showResetButton();
        changeCountdownColorOff();
    }


    @Override
    public void notifyPaused(){
        showResumeButton();
    }


    @Override
    public void notifyTimesUp(String timesUpMessage){
        showTimesUpText(timesUpMessage);
        hideStartStopButton();
        showStartButton();
        disableStartButton();
    }


    @Override
    public void notifyResetWhenTimerStopped() {
        showStartStopButton();
        this.startStopButton.setEnabled(true);
        this.startStopButton.setText(getResources().getString(R.string.button_start_label));
    }


    private void initAnimation(){
        displayTimesUpTextAnimation = new ScaleAnimation(0.1f, 1.1f, 0.1f, 1.1f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
        displayTimesUpTextAnimation.setDuration(500);
        displayTimesUpTextAnimation.setFillAfter(true);
    }


    private void hideTimesUpText(){
        timesUpMessageText.clearAnimation();
        timesUpMessageText.setVisibility(View.INVISIBLE);
    }


    @Override
    public void setCurrentCountdownValue(String currentMinutes, String currentSeconds, boolean isCritical){
        runOnUiThread(() -> {
            currentCountdownMinutesText.setText(currentMinutes);
            currentCountdownSecondsText.setText(currentSeconds);
            viewModel.isTimeLeftCritical = isCritical;
            updateTimerTextColor();
        });
    }


    @Override
    public void updateForRunningState(){
        showStartStopButton();
        showPauseButton();
        showResetButton();
    }


    @Override
    public void updateForReadyState(){
        showStartStopButton();
        showStartButton();
        showResetButton();
    }


    @Override
    public void updateForPausedState(){
        showStartStopButton();
        showResumeButton();
        showResetButton();
    }


    @Override
    public void updateForTimesUpState(String timesUpText){
        showResetButton();
        hideStartStopButton();
        displayTimesUpText(timesUpText);
    }



    public void assignSettings(int minutes, int seconds, String message) {
        if(timerService != null) {
            timerService.savePreferences(minutes, seconds, message);
        }
    }

    public void assignSettings(Settings settings) {
        if(timerService != null) {
            timerService.savePreferences(settings);
        }
    }


    private void showTimesUpText(String timesUpMessage){
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            displayTimesUpText(timesUpMessage);
            timesUpMessageText.startAnimation(displayTimesUpTextAnimation);
        });
    }


    private void displayTimesUpText(String timesUpMessage){
        timesUpMessageText.setText(timesUpMessage);
        timesUpMessageText.setVisibility(View.VISIBLE);
    }


    private void updateTimerTextColor(){
        int colorId = viewModel.isTimeLeftCritical ? R.color.dark_timer_text_critical : R.color.dark_timer_text_normal;
        setColorOnCountdownText(colorId);
    }


    private void setColorOnCountdownText(int colorId){
        setColorOn(currentCountdownMinutesText, colorId);
        setColorOn(currentCountdownSecondsText, colorId);
        setColorOn(currentCountdownDelimiterText, colorId);
    }


    private void setColorOn(TextView textView, int colorId){
        textView.setTextColor(getResources().getColor(colorId, null));
    }


    private void changeCountdownColorOff() {
        setColorOnCountdownText(R.color.colorTimerTextOff);
    }


    private void disableStartButton() {
        runOnUiThread(() -> startStopButton.setEnabled(false) );
    }


    private void showPauseButton(){
        startStopButton.setText(getString(R.string.button_pause_label));
    }


    private void showResetButton(){
        resetButton.setText(getString(R.string.button_reset_label));
    }


    private void showStartStopButton(){
        startStopButton.setVisibility(View.VISIBLE);
    }


    private void hideStartStopButton(){
        startStopButton.setVisibility(View.INVISIBLE);
    }


    private void showStartButton(){
        startStopButton.setText(getString(R.string.button_start_label));
    }


    private void showResumeButton(){
        startStopButton.setText(getString(R.string.button_resume_label));
    }

}