/*
 * Aris David
 * 7-July-2013
 * FinvizHttp Class
 */
package com.stocktrak;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class FinvizHttp {
	
	private ArrayList<String> tickerSymbol = new ArrayList<String>(); //Symbol
	private ArrayList<String> tickerName = new ArrayList<String>(); //Company Name
	private ArrayList<String> tickerSector = new ArrayList<String>(); //Sector
	private ArrayList<String> tickerIndustry = new ArrayList<String>(); //Industry
	
	private URL url = null;
	private URLConnection urlconn = null;
	private InputStreamReader inStream = null;
	
	public FinvizHttp(String urlStr){
		try {
			//Open a Connection
			this.url = new URL(urlStr);
			this.urlconn = this.url.openConnection();
			//Start Reading
			this.inStream = new InputStreamReader(urlconn.getInputStream());
			BufferedReader buffreader = new BufferedReader(inStream);
			String stringLine;
			buffreader.readLine(); //read the first line
			
			while((stringLine = buffreader.readLine())!= null)
			{
				String [] finvizData = stringLine.split("\\,");
				this.tickerSymbol.add(finvizData[1]);
				this.tickerName.add(finvizData[2]);
				this.tickerSector.add(finvizData[3]);
				this.tickerIndustry.add(finvizData[4]);
				
			}
			
		} catch (IOException e) {
			e.getMessage();
		}
	}
	
	//return data
	public ArrayList<String> getTickerSymbol(){
		return tickerSymbol;
	}
	public ArrayList<String> getTickerName(){
		return tickerName;
	}
	public ArrayList<String> getTickerSector(){
		return tickerSector;
	}
	public ArrayList<String> getTickerIndustry(){
		return tickerIndustry;
	}


}
