package com.stocktrak;
import com.stocktrak.transactional.AccountCash;
import com.stocktrak.transactional.HoldingsMap;
import com.stocktrak.transactional.Transaction;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Douglas on 2/10/2015.
 */
public class AnalysisProcess {

    public static final LinkedBlockingQueue<Transaction> transactionQueue = new LinkedBlockingQueue();
    public static final AccountCash accountCash = new AccountCash();
    public static final HoldingsMap holdings = new HoldingsMap();

    public AnalysisProcess() {
    }

    public static void main(String[] args) {
        Thread stockTrackerProcess = new StockTrackerProcess();
        stockTrackerProcess.start();
        Thread finvizProcess = new FinvizProcess();
        finvizProcess.start();
    }

}
