package com.stocktrak.ticker;

import org.apache.commons.collections.buffer.CircularFifoBuffer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;

/**
 * Created by Douglas on 2/16/2015.
 */
public class TickerInfoBuffer implements Iterable<TickerInfo> {
    private Analytics analytics;
    private CircularFifoBuffer tickerInfoBuffer;
    private TickerInfo currentTickerInfo;
    private TickerInfo previousTickerInfo;
    private Analytics currentAnalytics;
    private Analytics previousAnalytics;
    private int maxSize;

    public TickerInfoBuffer() {
        this(10);
    }

    public TickerInfoBuffer(int maxSize) {
        this.maxSize = maxSize;
        tickerInfoBuffer = new CircularFifoBuffer(maxSize);
    }

    public int getMaxSize() {
        return maxSize;
    }

    public void updateAnalytics() {
        double sum = 0;
        for(TickerInfo tickerInfo : this) {
            sum += tickerInfo.getPrice();
        }
        double average = sum / tickerInfoBuffer.size();
        previousAnalytics = currentAnalytics;
        currentAnalytics = new Analytics(average, 0);
    }

    public Analytics getCurrentAnalytics() {
        return currentAnalytics;
    }

    public Analytics getPreviousAnalytics() {
        return previousAnalytics;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    public boolean add(TickerInfo tickerInfo) {
        boolean ret = tickerInfoBuffer.add(tickerInfo);
        if(ret) {
            previousTickerInfo = currentTickerInfo;
            currentTickerInfo = tickerInfo;
            updateAnalytics();
        }
        return ret;
    }

    public TickerInfo get() {
        return (TickerInfo)tickerInfoBuffer.get();
    }

    public TickerInfo get(int index) {
        if(index < tickerInfoBuffer.size()) {
            int i = 0;
            for(TickerInfo tickerInfo : this) {
                if(i++ == index) {
                    return tickerInfo;
                }
            }
        }
        return null;
    }

    public TickerInfo getCurrentTickerInfo() {
        return currentTickerInfo;
    }

    public TickerInfo getPreviousTickerInfo() {
        return previousTickerInfo;
    }

    public TickerInfo remove() {
        return (TickerInfo)tickerInfoBuffer.remove();
    }

    @Override
    public Iterator iterator() {
        return tickerInfoBuffer.iterator();
    }

    @Override
    public void forEach(Consumer action) {

    }

    @Override
    public Spliterator spliterator() {
        return null;
    }


    @Override
    public String toString() {
        return "TickerInfoBuffer{" +
                "analytics=" + analytics +
                ", tickerInfoBuffer=" + tickerInfoBuffer +
                '}';
    }
}