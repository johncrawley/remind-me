package com.jcrawley.remindme.deleteme;

import com.jcrawley.remindme.CountdownTimer;


class TimerThread implements Runnable {

    private int totalTime;
    private final CountdownTimer countdownTimer;

    public TimerThread(int totalTime, CountdownTimer countdownTimer) {

        this.totalTime = totalTime;
        this.countdownTimer = countdownTimer;
    }


    public void run() {
        final int ONE_SECOND = 1000;
        while (totalTime > 0) {
            try {
                Thread.sleep(ONE_SECOND);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            }
            totalTime -= ONE_SECOND;
            countdownTimer.setCurrentCountdownValue(totalTime);

        }
        countdownTimer.onCountdownComplete();
    }
}