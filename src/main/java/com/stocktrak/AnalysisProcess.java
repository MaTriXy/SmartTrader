package com.stocktrak;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Douglas on 2/10/2015.
 */
public class AnalysisProcess {

    public static final LinkedBlockingQueue<Transaction> transactionQueue = new LinkedBlockingQueue();
    public static final AccountCash accountCash = new AccountCash();

    public AnalysisProcess() {
    }

    public static void main(String[] args) {
        Thread stockTrackerProcess = new StockTrackerProcess();
        stockTrackerProcess.start();
        Thread yahooFinanceScraperProcess = new YahooFinanceScraperProcess();
        yahooFinanceScraperProcess.start();
    }

}
