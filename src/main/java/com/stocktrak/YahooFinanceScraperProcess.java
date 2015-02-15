package com.stocktrak;

import static com.stocktrak.AnalysisProcess.stockQueue;

/**
 * Created by Douglas on 2/13/2015.
 */
public class YahooFinanceScraperProcess extends Thread {

    public YahooFinanceScraperProcess() {

    }

    public void run() {
        //        String baseUrl = "http://query.yahooapis.com/v1/public/yql?q=";
//        String query = "select * from upcoming.events where location='San Francisco' and search_text='dance'";
//        InputStream is = null;
//        try {
//            String fullUrlStr = baseUrl + URLEncoder.encode(query, "UTF-8") + "&format=json";
//
//            URL fullUrl = new URL(fullUrlStr);
//            is = fullUrl.openStream();
//
//            JSONTokener tok = new JSONTokener(is);
//            JSONObject result = new JSONObject(tok);
//            is.close();
//        } catch(UnsupportedEncodingException e) {
//        } catch(MalformedURLException e) {
//        } catch(IOException e) {
//        } catch(JSONException e) {
//        }
    }
}
