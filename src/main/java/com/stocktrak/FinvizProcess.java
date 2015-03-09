package com.stocktrak;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Douglas on 3/9/2015.
 */
public class FinvizProcess extends Thread {

    @Override
    public synchronized void start() {
        super.start();
    }

    @Override
    public void run() {
        super.run();
        FinvizHttp finvizHttp = new FinvizHttp();
        finvizHttp.login();
        while(currentTime() < startOfBusinessDay()){
            try {
                wait(60000);
            } catch(InterruptedException e) {
                System.out.println(e);
            }
        }
        System.out.println("Business day has begun.");
        while(currentTime() < endOfBusinessDay()){
            try {
                finvizHttp.downloadDJIA();
                wait(60000);
            } catch(InterruptedException e) {
                System.out.println(e);
            }
        }
    }

    public long currentTime() {
        return Calendar.getInstance().getTimeInMillis();
    }
    public long startOfBusinessDay() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 9);
        cal.set(Calendar.MINUTE, 30);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    public long endOfBusinessDay() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 16);
        cal.set(Calendar.MINUTE, 30);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }
}
