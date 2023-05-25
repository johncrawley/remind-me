package com.jcrawley.remindme.service;

import static android.content.Context.POWER_SERVICE;

import android.app.Service;
import android.os.PowerManager;


public class WakeLockHelper {

    private PowerManager.WakeLock wakeLock;
    private Service service;

    public WakeLockHelper(Service service){
        this.service = service;
    }


    public void acquireCpuWakeLock(){
        PowerManager powerManager = (PowerManager) service.getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "jCrawley_RemindMe::TimerWakeLock");
        wakeLock.acquire(100*60*1000L /*10 minutes*/);
    }


    public void releaseCpuWakeLock(){
        if(wakeLock.isHeld()){
            wakeLock.release();
        }
    }


    public void destroy(){
        releaseCpuWakeLock();
        this.service = null;
    }

}
