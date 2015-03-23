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

    private void updateMovingAverages() {

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
                stockData.add(symbol, new TickerInfo(price, change, volume, unixTime));
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
    /*
    https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.quote%20where%20symbol%20in%20(%22YHOO%22%2C%22AAPL%22%2C%22GOOG%22%2C%22MSFT%22)&format=json&diagnostics=true&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=
    {
 "query": {
  "count": 4,
  "created": "2015-02-17T14:37:16Z",
  "lang": "en-US",
  "diagnostics": {
   "url": [
    {
     "execution-start-time": "0",
     "execution-stop-time": "71",
     "execution-time": "71",
     "content": "http://www.datatables.org/yahoo/finance/quote/yahoo.finance.quote.xml"
    },
    {
     "execution-start-time": "74",
     "execution-stop-time": "152",
     "execution-time": "78",
     "content": "http://download.finance.yahoo.com/d/quotes.csv?f=aa2bb2b3b4cc1c3c4c6c8dd1d2ee1e7e8e9ghjkg1g3g4g5g6ii5j1j3j4j5j6k1k2k4k5ll1l2l3mm2m3m4m5m6m7m8nn4opp1p2p5p6qrr1r2r5r6r7ss1s7t1t7t8vv1v7ww1w4xy&s=YHOO,AAPL,GOOG,MSFT"
    }
   ],
   "publiclyCallable": "true",
   "query": {
    "execution-start-time": "73",
    "execution-stop-time": "153",
    "execution-time": "80",
    "params": "{url=[http://download.finance.yahoo.com/d/quotes.csv?f=aa2bb2b3b4cc1c3c4c6c8dd1d2ee1e7e8e9ghjkg1g3g4g5g6ii5j1j3j4j5j6k1k2k4k5ll1l2l3mm2m3m4m5m6m7m8nn4opp1p2p5p6qrr1r2r5r6r7ss1s7t1t7t8vv1v7ww1w4xy&s=YHOO,AAPL,GOOG,MSFT]}",
    "content": "select * from csv where url=@url and columns='Ask,AverageDailyVolume,Bid,AskRealtime,BidRealtime,BookValue,Change&PercentChange,Change,Commission,Currency,ChangeRealtime,AfterHoursChangeRealtime,DividendShare,LastTradeDate,TradeDate,EarningsShare,ErrorIndicationreturnedforsymbolchangedinvalid,EPSEstimateCurrentYear,EPSEstimateNextYear,EPSEstimateNextQuarter,DaysLow,DaysHigh,YearLow,YearHigh,HoldingsGainPercent,AnnualizedGain,HoldingsGain,HoldingsGainPercentRealtime,HoldingsGainRealtime,MoreInfo,OrderBookRealtime,MarketCapitalization,MarketCapRealtime,EBITDA,ChangeFromYearLow,PercentChangeFromYearLow,LastTradeRealtimeWithTime,ChangePercentRealtime,ChangeFromYearHigh,PercebtChangeFromYearHigh,LastTradeWithTime,LastTradePriceOnly,HighLimit,LowLimit,DaysRange,DaysRangeRealtime,FiftydayMovingAverage,TwoHundreddayMovingAverage,ChangeFromTwoHundreddayMovingAverage,PercentChangeFromTwoHundreddayMovingAverage,ChangeFromFiftydayMovingAverage,PercentChangeFromFiftydayMovingAverage,Name,Notes,Open,PreviousClose,PricePaid,ChangeinPercent,PriceSales,PriceBook,ExDividendDate,PERatio,DividendPayDate,PERatioRealtime,PEGRatio,PriceEPSEstimateCurrentYear,PriceEPSEstimateNextYear,Symbol,SharesOwned,ShortRatio,LastTradeTime,TickerTrend,OneyrTargetPrice,Volume,HoldingsValue,HoldingsValueRealtime,YearRange,DaysValueChange,DaysValueChangeRealtime,StockExchange,DividendYield'"
   },
   "javascript": {
    "execution-start-time": "73",
    "execution-stop-time": "177",
    "execution-time": "103",
    "instructions-used": "197622",
    "table-name": "yahoo.finance.quote"
   },
   "user-time": "178",
   "service-time": "149",
   "build-version": "0.2.396"
  },
  "results": {
   "quote": [
    {
     "symbol": "YHOO",
     "AverageDailyVolume": "19484000",
     "Change": "0.00",
     "DaysLow": null,
     "DaysHigh": null,
     "YearLow": "32.15",
     "YearHigh": "52.62",
     "MarketCapitalization": "42.760B",
     "LastTradePriceOnly": "44.42",
     "DaysRange": "N/A - N/A",
     "Name": "Yahoo! Inc.",
     "Symbol": "YHOO",
     "Volume": "5525",
     "StockExchange": "NasdaqNM"
    },
    {
     "symbol": "AAPL",
     "AverageDailyVolume": "55195700",
     "Change": "0.00",
     "DaysLow": null,
     "DaysHigh": null,
     "YearLow": "73.0471",
     "YearHigh": "127.48",
     "MarketCapitalization": "740.2B",
     "LastTradePriceOnly": "127.08",
     "DaysRange": "N/A - N/A",
     "Name": "Apple Inc.",
     "Symbol": "AAPL",
     "Volume": "258089",
     "StockExchange": "NasdaqNM"
    },
    {
     "symbol": "GOOG",
     "AverageDailyVolume": "2098180",
     "Change": "0.00",
     "DaysLow": null,
     "DaysHigh": null,
     "YearLow": "487.56",
     "YearHigh": "604.83",
     "MarketCapitalization": "373.7B",
     "LastTradePriceOnly": "549.01",
     "DaysRange": "N/A - N/A",
     "Name": "Google Inc.",
     "Symbol": "GOOG",
     "Volume": "865",
     "StockExchange": "NasdaqNM"
    },
    {
     "symbol": "MSFT",
     "AverageDailyVolume": "35902500",
     "Change": "0.00",
     "DaysLow": null,
     "DaysHigh": null,
     "YearLow": "37.19",
     "YearHigh": "50.05",
     "MarketCapitalization": "359.9B",
     "LastTradePriceOnly": "43.87",
     "DaysRange": "N/A - N/A",
     "Name": "Microsoft Corpora",
     "Symbol": "MSFT",
     "Volume": "11074",
     "StockExchange": "NasdaqNM"
    }
   ]
  }
 }
}
     */
}
