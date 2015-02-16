package com.stocktrak;

import com.stocktrak.transactional.AccountCash;
import com.stocktrak.transactional.Transaction;
import com.stocktrak.ticker.TickerInfo;
import com.stocktrak.ticker.TickerMap;

import java.io.*;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Douglas on 2/13/2015.
 */
public class YahooFinanceScraperProcess extends Thread {
    private static final String LOG_TAG = "YahooFinanceScraperProcess";
    private static final String DATA_FORMAT = "sl1d1t1c1hgv";
    private static final String TICKERS_PATH = "src/main/resources/tickers.csv";
    private static final String BASE_URL = "http://download.finance.yahoo.com/d/quotes.csv?";
    private static final String FILE_EXT = ".csv";
    private static final String LINE_DELIMITER = "\r\n";
    private static final String TIME_DATE_PATTERN = "\"m/d/yyyy\" \"h:mma\"";

    private Queue<Transaction> transactionQueue = AnalysisProcess.transactionQueue;
    private AccountCash accountCash = AnalysisProcess.accountCash;
    private Set<String> symbols;
    private TickerMap stockData;

    public YahooFinanceScraperProcess() {
        stockData = new TickerMap(AnalysisProcess.BUFFER_SIZE);
        symbols = new HashSet();
    }

    public void run() {
        int i = 0;
        updateSymbols();
        updateData();

//        while(i++ < 2) {
//            stockQueue.offer(new Transaction(1, "AAPL", BUY));
//            stockQueue.offer(new Transaction(1, "AAPL", SELL));
//        }

    }

    private void buy(String symbol, int quantity, double totalPrice) {
        transactionQueue.offer(new Transaction(quantity, symbol, Transaction.Type.BUY));
        accountCash.decreaseExpectedBy(totalPrice);
    }

    private void sell(String symbol, int quantity, double totalPrice) {
        transactionQueue.offer(new Transaction(quantity, symbol, Transaction.Type.SELL));
        accountCash.increaseExpectedBy(totalPrice);
    }

    private void updateSymbols() {
        try {
            BufferedReader br = new BufferedReader(new FileReader(TICKERS_PATH));
            String line = br.readLine();
            while (line != null) {
                symbols.add(line);
                line = br.readLine();
            }
            br.close();
        } catch(IOException e) {
            log(e);
        } finally {
            log(symbols);
        }
    }

    private void updateData() {
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(BASE_URL);

        urlBuilder.append("s=");
        for(String ticker : symbols) {
            urlBuilder.append(ticker);
            if(symbols.iterator().hasNext()) {
                urlBuilder.append(",");
            }
        }
        urlBuilder.append("&f=" + DATA_FORMAT);
        urlBuilder.append("&e=" + FILE_EXT);
        InputStream is = null;
        try {
            URL url = new URL(urlBuilder.toString());
            is = url.openStream();
            Scanner scanner = new Scanner(is).useDelimiter(LINE_DELIMITER);
            while(scanner.hasNext()) {
                String next = scanner.next();
                String[] row = next.split(",");
                SimpleDateFormat sdf = new SimpleDateFormat(TIME_DATE_PATTERN);
                Date date = sdf.parse(row[2] + " " + row[3]);
                long unixTime = date.getTime();
                String symbol = row[0];
                double price = Double.parseDouble(row[1]);
                double change = Double.parseDouble(row[4]);
                int volume = Integer.parseInt(row[7]);
                double dayHigh = Double.parseDouble(row[5]);
                double dayLow = Double.parseDouble(row[6]);
                stockData.add(symbol, new TickerInfo(price, change, volume, dayHigh, dayLow, unixTime));
            }
            is.close();
            scanner.close();
        } catch(IOException e) {
            log(e);
        } catch(ParseException e) {
            log(e);
        } finally {
            log(stockData);
        }
    }

    private void log(Object str) {
        System.out.println(LOG_TAG + ": " + (str != null ? str.toString() : null));
    }
}
