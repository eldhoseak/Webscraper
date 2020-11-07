package com.ws.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WSParser {

	private static int PAGE_LEVEL = 4;
	static List<String> pairs = new ArrayList<String>();
	static List<String> wordsList = new ArrayList<String>();

	/**
	 * This is the starting method for performing web scraping.
	 * In this method we will first try to get the complete list of internal URL
	 * upto level 4 in a web page. Then each URL is traversed to identify the words,
	 * and pair words. This will ensure that we have a full list of all words in the 
	 * webpage. Later we will count the web pages and print those in console.
	 * @param webPage - URL of the page.
	 */
	public void printWordsCount(String webPage){
		try {

			// Check if a valid URL is passed.
			if(webPage == null 
					|| webPage.equals("") 
					|| getPageAsDocument(webPage)== null){
				System.out.println("Please pass a valid URL !!.");
				return;
			}

			// Retrieve the URLs in the page upto depth 4 .
			List<String> masterUrlList = new ArrayList<String>();
			retrieveChildUrls(webPage, webPage, masterUrlList);

			//Get the words and pair words, add those into lists.
			masterUrlList.parallelStream().forEach(WSParser::findWords); 

			// Count words
			List<Map.Entry<String, Long>> wordCount= countWords(wordsList);
			System.out.println("word count -> "+ wordCount);

			// Count pair words.
			List<Map.Entry<String, Long>> wordPairCount= countWords(pairs);
			System.out.println("word pair count -> "+ wordPairCount);

		} catch (Exception e) {
			System.out.println("Exception occured "+ e.getMessage());
		}
	}

	/**
	 * Method to find the words, pair words in the URL send.
	 * Add those into master list.
	 * @param url
	 */
	private static void findWords(String url) {

		// Get the web page as a document.
		Document doc = getPageAsDocument(url);
		if(doc != null){
			String result = doc.body().text(); // Get the HTML body.

			// Get the words.
			String[] words = result.split("\\s+");  
			wordsList.addAll(Arrays.asList(words));

			// Get pair words.
			for (int i = 0; i < words.length-1; ++i) {
				pairs.add(words[i] + " " + words[i+1]);
			}
		}
	}

	/**
	 * This method will recursively find out the sub urls in the page. Upto depth 4.
	 * Then add those URLs to master URL list.
	 * @param baseUrl
	 * @param url
	 * @param masterUrlList
	 */
	private static void  retrieveChildUrls(String baseUrl, String url, 
			List<String> masterUrlList) {

		Document doc = getPageAsDocument(url);
		if(doc != null){

			// Find all URLs in the page.
			Elements elements = doc.select("a");
			for(Element element : elements){

				// Consider only internal URLs,and unique URls as there could be
				// many cross references.
				if(!element.absUrl("href").equals(url) 
						&& element.absUrl("href").startsWith(baseUrl)  
						&& !element.absUrl("href").endsWith("#") 
						&& !masterUrlList.contains(element.absUrl("href"))){

					System.out.println(element.absUrl("href"));
					masterUrlList.add(element.absUrl("href")); 
					PAGE_LEVEL --;
					if(PAGE_LEVEL >0){
						retrieveChildUrls(baseUrl, element.absUrl("href"), masterUrlList );
					}else{
						PAGE_LEVEL =4;	
					}
				}
			}
		}
	}

	/**
	 * Method to get the web page as a document using Jsoup library.
	 * Skip all those URLs results in 404 or any other exceptions.
	 * @param url
	 * @return
	 */
	private static Document getPageAsDocument(String url){
		Document doc = null;
		try{
			Connection conn = Jsoup.connect(url);
			doc = conn.get();
		}
		catch(HttpStatusException hse){
			System.out.println("Obtained 404 for url "+url+" so skipping");
		}catch (IOException e) {
			System.out.println("Exception occured "+ e.getMessage());
		}
		return doc;
	}

	/**
	 * Method to count all words and pair words.
	 * @param words
	 * @return
	 */
	private List<Map.Entry<String, Long>> countWords(List<String> words){

		Map<String, Long> map = words.stream()
				.collect(Collectors.groupingBy(w -> w, Collectors.counting()));
		return map.entrySet().stream()
				.sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
				.limit(10)
				.collect(Collectors.toList());
	}
}
