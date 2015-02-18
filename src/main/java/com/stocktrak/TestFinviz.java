package com.stocktrak;

/*
 * Aris David
 * 7-July-2013
 * To test the FinvizHttp
 */

public class TestFinviz {
	public static void main(String[] args) {
		
		String urlStr = "http://finviz.com/export.ashx?v=111";
		FinvizHttp finviz = new FinvizHttp(urlStr);
		
		//Print Results
		System.out.println(finviz.getTickerSymbol()); //Symbol
		//System.out.println(finviz.getTickerName()); //Company Name
		//System.out.println(finviz.getTickerSector()); //Sector
		//System.out.println(finviz.getTickerIndustry()); //Industry

	}

}
