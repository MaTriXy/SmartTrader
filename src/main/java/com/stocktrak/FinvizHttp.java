package com.stocktrak;

import com.google.gson.internal.LinkedHashTreeMap;
import com.stocktrak.ticker.DJIA;
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

	private ArrayList<String> tickerSymbol = new ArrayList<String>(); //Symbol
	private ArrayList<String> tickerName = new ArrayList<String>(); //Company Name
	private ArrayList<String> tickerSector = new ArrayList<String>(); //Sector
	private ArrayList<String> tickerIndustry = new ArrayList<String>(); //Industry
	private static String aspxAuth;
    private DefaultHttpClient httpClient;
	private InputStreamReader inStream = null;

	public FinvizHttp(){
        httpClient = new DefaultHttpClient();
        tickersStarted = new HashSet();
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
            HttpResponse response = httpClient.execute(httpGet);
            HttpEntity responseEntity = response.getEntity();
            InputStream is = responseEntity.getContent();
            CSVParser parser = new CSVParser(new InputStreamReader(is), CSVFormat.EXCEL);
            List<CSVRecord> list = parser.getRecords();
            System.out.println(list);
            for(int i = 1; i < list.size(); i++) {

                CSVRecord record = list.get(i);
                long unixTime = System.currentTimeMillis();
                FileWriter writer = new FileWriter("src/main/resources/" +
                        record.get(1) + ".csv", true);
                if(!tickersStarted.contains(record.get(1))) {
                    tickersStarted.add(record.get(1));
                    writer.append("Price,Change,Volume,Time\n");
                }
                writer.append(record.get(8)+",");
                writer.append(record.get(9)+",");
                writer.append(record.get(10)+",");
                writer.append(""+unixTime);
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
