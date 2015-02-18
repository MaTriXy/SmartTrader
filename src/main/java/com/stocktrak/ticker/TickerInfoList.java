package com.stocktrak.ticker;

import java.util.ArrayList;

/**
 * Created by Douglas on 2/16/2015.
 */
public class TickerInfoList extends ArrayList<TickerInfo> {
    private Analytics analytics;

    private int maxSize;

    public TickerInfoList(int maxSize) {
        super();
        this.maxSize = maxSize;
    }

    public int getMaxSize() {
        return maxSize;
    }

    public Analytics getAnalytics() {
        return analytics;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    @Override
    public boolean add(TickerInfo tickerInfo) {
        boolean ret = false;
        if(size() < maxSize) {
            ret = super.add(tickerInfo);
        }
        return ret;
    }
}