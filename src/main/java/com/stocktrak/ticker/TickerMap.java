package com.stocktrak.ticker;

import java.util.HashMap;

/**
 * Created by Douglas on 2/16/2015.
 */
public class TickerMap {

    private HashMap<String, TickerInfoList> map;
    private int bufferSize;

    public TickerMap(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    public void add(String key, TickerInfo info) {
        if(map.get(key) == null) {
            map.put(key, new TickerInfoList(bufferSize));
        }

        map.get(key).add(info);
    }

    public String toString() {
        return map.toString();
    }
}
