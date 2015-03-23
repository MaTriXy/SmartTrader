package com.stocktrak;

import com.stocktrak.ticker.TickerInfoBuffer;
import com.stocktrak.ticker.TickerMap;
import com.stocktrak.transactional.HoldingInfo;
import com.stocktrak.transactional.Transaction;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Douglas on 3/9/2015.
 */
public class FinvizProcess extends Thread {
    private static final long PERIOD_SIZE = 60000;
    private static final double DEFAULT_TRADE_PRICE = 160000;

    @Override
    public synchronized void start() {
        super.start();
    }

    @Override
    public void run() {
        super.run();
        long startOfBusinessDay = startOfBusinessDay();
        long endOfBusinessDay = endOfBusinessDay();
        FinvizHttp finvizHttp = new FinvizHttp();
        finvizHttp.login();
        long currentTime;
        while((currentTime = currentTime()) < startOfBusinessDay){
            try {
                sleep(PERIOD_SIZE);
            } catch(InterruptedException e) {
                System.out.println(e);
            }
        }
        System.out.println("Business day has begun.");
        while((currentTime = currentTime()) < endOfBusinessDay) {
            try {
                finvizHttp.downloadDJIA();
                System.out.println(finvizHttp.getTickerMap());
                determineTransactions(finvizHttp.getTickerMap());
                sleep(PERIOD_SIZE);
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
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

//    public long startOfBusinessDay() {
//        return currentTime();
//    }
//
//    public long endOfBusinessDay() {
//        return currentTime() + 20000;
//    }

    public void determineTransactions(TickerMap tickerMap) {
        for(String symbol : tickerMap.getTickers()) {
            TickerInfoBuffer tickerInfoBuffer = tickerMap.get(symbol);
            if(tickerInfoBuffer.atMaxSize()) {
                double previousMovingAverage = tickerInfoBuffer.getPreviousAnalytics().getAverage();
                double currentMovingAverage = tickerInfoBuffer.getCurrentAnalytics().getAverage();
                double previousPrice = tickerInfoBuffer.getPreviousTickerInfo().getPrice();
                double currentPrice = tickerInfoBuffer.getCurrentTickerInfo().getPrice();
                if (!AnalysisProcess.holdings.containsKey(symbol) &&
                        previousPrice > previousMovingAverage && currentPrice < currentMovingAverage) {
                    int quantity = (int) (DEFAULT_TRADE_PRICE / currentPrice);
                    double totalSalePrice = quantity * currentPrice;
                    Transaction transaction = new Transaction(quantity, symbol, Transaction.Type.BUY);
                    HoldingInfo holdingInfo = new HoldingInfo(totalSalePrice, quantity, currentPrice);
                    AnalysisProcess.transactionQueue.add(transaction);
                    AnalysisProcess.holdings.put(symbol, holdingInfo);
                    AnalysisProcess.accountCash.decreaseExpectedBy(totalSalePrice);
                } else if (AnalysisProcess.holdings.containsKey(symbol)) {
                    HoldingInfo holdingInfo = AnalysisProcess.holdings.get(symbol);
                    boolean crossedToSell = previousPrice < previousMovingAverage && currentPrice > currentMovingAverage;
                    boolean profitHighEnough = holdingInfo.getPriceSpent() < 0.999 * currentPrice;
                    if (crossedToSell || profitHighEnough) {
                        Transaction transaction = new Transaction(holdingInfo.getQuantity(), symbol, Transaction.Type.SELL);
                        AnalysisProcess.transactionQueue.add(transaction);
                    }
                }
            }
        }
    }
}
