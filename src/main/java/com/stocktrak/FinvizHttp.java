package com.stocktrak;

import com.google.gson.internal.LinkedHashTreeMap;
import com.stocktrak.ticker.TickerInfo;
import com.stocktrak.ticker.TickerInfoBuffer;
import com.stocktrak.ticker.TickerMap;
import com.stocktrak.transactional.Transaction;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.commons.csv.CSVParser;

import java.io.*;
import java.lang.reflect.Array;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.*;

public class FinvizHttp {
	private static final String EMAIL = "qpcamp@gmail.com";
    private static final String PASSWORD = "Dresser5";
    private static final String BASE_URL = "http://finviz.com/";
    private static final String LOGIN_ACTION = "login_submit.ashx";
    private static final String DOWNLOAD_DJIA = "export.ashx?v=111&f=idx_dji";
//    private static final String SESSION_COOKIE = "ASP.NET_SessionId";
//    private static final String
    private HashSet<String> tickersStarted;
	private static String aspxAuth;
    private DefaultHttpClient httpClient;
	private InputStreamReader inStream = null;
    private TickerMap tickerMap;

	public FinvizHttp(int bufferSize){
        httpClient = new DefaultHttpClient();
        tickersStarted = new HashSet();
        tickerMap = new TickerMap(bufferSize);
	}

    public TickerMap getTickerMap() {
        return tickerMap;
    }

    public void login() {
        HttpPost httpPost = new HttpPost(BASE_URL + LOGIN_ACTION);
        List<NameValuePair> nameValuePairs = new ArrayList(2);
        nameValuePairs.add(new BasicNameValuePair("email", EMAIL));
        nameValuePairs.add(new BasicNameValuePair("password", PASSWORD));
        try {
            UrlEncodedFormEntity requestEntity = new UrlEncodedFormEntity(nameValuePairs);
            requestEntity.setContentType("application/x-www-form-urlencoded;charset=UTF-8");
            httpPost.setEntity(requestEntity);
            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity responseEntity = response.getEntity();
            List<Cookie> cookies = httpClient.getCookieStore().getCookies();
            for(Cookie c : cookies) {
                System.out.println(c);
            }
            EntityUtils.consumeQuietly(responseEntity);
        } catch(UnsupportedEncodingException e) {

        } catch(ClientProtocolException e) {

        } catch(IOException e) {

        }
    }

    private String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append((line + "\n"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }


	public void downloadDJIA(){
        HttpGet httpGet = new HttpGet(BASE_URL + DOWNLOAD_DJIA);
        httpGet.setHeader("Accept", "application/csv");
        try {
            int j = 0;
            HttpResponse response = httpClient.execute(httpGet);
            HttpEntity responseEntity = response.getEntity();
            InputStream is = responseEntity.getContent();
            CSVParser parser = new CSVParser(new InputStreamReader(is), CSVFormat.EXCEL);
            List<CSVRecord> list = parser.getRecords();
            for(int i = 1; i < list.size(); i++) {
                CSVRecord record = list.get(i);
                TickerInfo tickerInfo = TickerInfo.fromCsvRecord(record);
                String symbol = record.get(1);
                FileWriter writer = new FileWriter("src/main/resources/" +
                        symbol + ".csv", true);
                tickerMap.add(symbol, tickerInfo);
                if(!tickersStarted.contains(symbol)) {
                    tickersStarted.add(symbol);
                    writer.append("Price,Change,Volume,Time\n");
                }
                writer.append(tickerInfo.getPrice() + ",");
                writer.append(tickerInfo.getChange() + ",");
                writer.append(tickerInfo.getVolume() + ",");
                writer.append(""+tickerInfo.getTime());
                writer.append("\n");
                writer.flush();
                writer.close();
            }
            EntityUtils.consumeQuietly(responseEntity);
        } catch(ClientProtocolException e) {
            System.out.println(e);
        } catch(IOException e) {
            System.out.println(e);
        }
	}
}
