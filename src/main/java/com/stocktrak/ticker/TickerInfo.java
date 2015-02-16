package com.stocktrak.ticker;

import java.sql.Time;

/**
 * Created by Douglas on 2/15/2015.
 */
public class TickerInfo {
    private double price;
    private double change;
    private int volume;
    private double dayHigh;
    private double dayLow;
    private long time;

    public TickerInfo(double price, double change, int volume, double dayHigh, double dayLow, long time) {
        this.price = price;
        this.change = change;
        this.volume = volume;
        this.dayHigh = dayHigh;
        this.dayLow = dayLow;
        this.time = time;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getChange() {
        return change;
    }

    public void setChange(double change) {
        this.change = change;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public double getDayHigh() {
        return dayHigh;
    }

    public void setDayHigh(double dayHigh) {
        this.dayHigh = dayHigh;
    }

    public double getDayLow() {
        return dayLow;
    }

    public void setDayLow(double dayLow) {
        this.dayLow = dayLow;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "Price: " + price +
                ", Change: " + change +
                ", Volume: " + volume +
                ", Day's High: " + dayHigh +
                ", Day's low: " + dayLow +
                ", Time: " + time;

    }
}
