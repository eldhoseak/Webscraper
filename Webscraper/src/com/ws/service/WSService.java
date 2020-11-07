package com.ws.service;

/**
 * Class to test the Web scraper parser.
 *
 */
public class WSService {

	public static void main(String[] args) {
		WSParser parser = new WSParser();
		parser.printWordsCount("https://www.314e.com/");
	}

}
