package com.jcrawley.remindme.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.jcrawley.remindme.CountdownTimer;

public class TimerService extends Service {

    private final Context context;
    public static final String QUERIES = "queries";
    private final IBinder mBinder = new MyBinder();
    private static final String ACTION_STRING_SERVICE = "TimerService";
    private static final String ACTION_STRING_ACTIVITY = "ToActivity";
    public static final String ACTION_START_TIMER = "StartTimer";
    public static final String ACTION_STOP_TIMER = "StopTimer";
    public static final String ACTION_RESET_TIMER = "ResetTimer";

    public static final String TAG_MINUTES = "TimerServiceMinutes";
    public static final String TAG_SECONDS = "TimerServiceSeconds";

    private CountdownTimer countdownTimer;
    private String minutes, seconds;

    public TimerService() {
        context = TimerService.this;
        //countdownTimer = new CountdownTimer(this, 5);
        //countdownTimer.setTime(Integer.parseInt(minutes), Integer.parseInt(seconds));
    }


    private final BroadcastReceiver serviceReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(getApplicationContext(), "Message from service!", Toast.LENGTH_SHORT).show();
            log("Sending broadcast to activity");
            sendBroadcast();
            }
        };


        @Override
        public IBinder onBind(Intent arg0) {
            return null;
        }


        @Override
        public void onCreate() {
            super.onCreate();
            log("Entered onCreate");
            //STEP2: register the receiver
            IntentFilter intentFilter = new IntentFilter(ACTION_STRING_SERVICE);
            registerReceiver(serviceReceiver, intentFilter);
        }


        private void log(String msg){
            System.out.println("^^^ TimerService: " + msg);
            System.out.flush();
        }


        @Override
        public void onDestroy() {
            super.onDestroy();
            unregisterReceiver(serviceReceiver);
        }

        //send broadcast from activity to all receivers listening to the action "ACTION_STRING_ACTIVITY"
        private void sendBroadcast() {
            log("Entered sendBroadcast()");
            Intent new_intent = new Intent();
            new_intent.setAction(ACTION_STRING_ACTIVITY);
            sendBroadcast(new_intent);
        }


        /*
            Service.START_STICKY - service is restarted if terminated, intent passed in has null value
            Service.START_NOT_STICKY - service is not restarted
            Service.START_REDELIVER_INTENT - service is restarted if terminated, original intent is passed in

         */
        @Override
        public int onStartCommand(Intent intent, int flags, int startId){

            //WebsiteQuery[] queries =(WebsiteQuery[]) intent.getParcelableArrayExtra("queries");
            //notifier.createNotification("message for you: you have been notified! ", "title");
            //for(WebsiteQuery query : queries){
            //   Log.i("FirstService", " msg: " + query.getSuccessMessage());
            //}

            log(" hello! onStartCommand() initiated!");
            //new Notifier(context, "Hello there", "Title!!");
            return Service.START_REDELIVER_INTENT;
        }


        public class MyBinder extends Binder {
            TimerService getService(){
                return TimerService.this;
            }
        }

    }

