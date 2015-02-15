package com.stocktrak;
import java.util.concurrent.SynchronousQueue;

/**
 * Created by Douglas on 2/10/2015.
 */
public class AnalysisProcess {

    public static SynchronousQueue<Purchase> stockQueue = new SynchronousQueue();

    public AnalysisProcess() {
    }

    public static void main(String[] args) {
        Thread stockTrackerProcess = new StockTrackerProcess();
        stockTrackerProcess.start();
        Thread yahooFinanceScraperProcess = new YahooFinanceScraperProcess();
        yahooFinanceScraperProcess.start();
    }

}
