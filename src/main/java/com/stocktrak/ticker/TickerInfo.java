package com.stocktrak.ticker;

import org.apache.commons.csv.CSVRecord;

import java.sql.Time;

/**
 * Created by Douglas on 2/15/2015.
 */
public class TickerInfo {
    private double price;
    private double change;
    private int volume;
    private long time;

    public TickerInfo(double price, double change, int volume, long time) {
        this.price = price;
        this.change = change;
        this.volume = volume;
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

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public static TickerInfo fromCsvRecord(CSVRecord record) {
        double price = Double.parseDouble(record.get(8));
        double change = Double.parseDouble(record.get(9).replace("%", "")) / 100;
        int volume = Integer.parseInt(record.get(10));
        return new TickerInfo(price, change, volume, System.currentTimeMillis());
    }

    @Override
    public String toString() {
        return "TickerInfo{" +
                "price=" + price +
                ", change=" + change +
                ", volume=" + volume +
                ", time=" + time +
                '}';
    }
}
