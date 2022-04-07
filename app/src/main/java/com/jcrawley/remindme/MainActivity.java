package com.jcrawley.remindme;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.jcrawley.remindme.service.TimerService;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, CustomDialogCloseListener {


    private TextView currentCountdownText;
    private TextView timesUpMessageText;
    private Button setButton, startStopButton;
    private CountdownTimer countdownTimer;
    private MainViewModel viewModel;
    private boolean isInFront;
    private Animation displayTimesUpTextAnimation;
    private TimesUpNotifier timesUpNotifier;
    private static final String ACTION_STRING_SERVICE = "TimerService";
    private static final String ACTION_STRING_ACTIVITY = "ToActivity";

    //STEP1: Create a broadcast receiver
    private final BroadcastReceiver activityReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            log("Broadcast receiver, message received!");
            Toast.makeText(getApplicationContext(), "received message in activity..!", Toast.LENGTH_SHORT).show();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupViews();
        setupViewModel();
        initAnimation();
        countdownTimer = new CountdownTimer(this, 5);
        countdownTimer.setTime(Integer.parseInt(viewModel.mins), Integer.parseInt(viewModel.secs));
        registerBroadcastReceiver();
        startService();
    }


    private void registerBroadcastReceiver(){
        log("Entered registerBroadcastReceiver()");
        if (activityReceiver != null) {
            log("activityReceiver is not null, creating the intent filter and registering the receiver!");
            //Create an intent filter to listen to the broadcast sent with the action "ACTION_STRING_ACTIVITY"
            IntentFilter intentFilter = new IntentFilter(ACTION_STRING_ACTIVITY);
            //Map the intent filter to the receiver
            registerReceiver(activityReceiver, intentFilter);
        }
        else{
            log("activity receiver is null, won't be doing any registering!");
        }
    }


    private void startService(){
        log("Entered startService()");
        //Start the service on launching the application
        startService(new Intent(this, TimerService.class));
        log("Service should have started around about now");
        findViewById(R.id.testButton).setOnClickListener(v -> {
            log("Sending broadcast to service");
            sendBroadcast();
        });
    }

    private void log(String msg){
        System.out.println("^^^ MainActivity: " + msg);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
      log( "onDestroy");
        //STEP3: Unregister the receiver
        unregisterReceiver(activityReceiver);
    }

    //send broadcast from activity to all receivers listening to the action "ACTION_STRING_SERVICE"
    private void sendBroadcast() {
        Intent new_intent = new Intent();
        new_intent.setAction(ACTION_STRING_SERVICE);
        sendBroadcast(new_intent);
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
        cancelNotification();
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
        showTimesUpText();
        playSound();
        if(isInFront){
            return;
        }
        runOnUiThread(() -> {
            timesUpNotifier = new TimesUpNotifier(MainActivity.this, viewModel);
            timesUpNotifier.issueNotification();

        });
    }


    public MainViewModel getViewModel(){
        return viewModel;
    }


    private void cancelNotification(){
        if(timesUpNotifier == null){
            return;
        }
        timesUpNotifier.dismissNotification();
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


    private void playSound(){
        MediaPlayer mediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.alert1);
        try{
            mediaPlayer.start();

        }catch(Exception e){e.printStackTrace();}
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


    public void enableSetButton() {
        setButton.setEnabled(true);
    }


    public void disableStartButton() {
        runOnUiThread(() ->startStopButton.setEnabled(false) );
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
            hideTimesUpText();
        }
        else if(id == R.id.startStopButton){
            countdownTimer.startStop();
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


    private String getTimeText(int timeValue){
        return timeValue > 9 ?
                "" + timeValue
                : "0" + timeValue;
    }


}