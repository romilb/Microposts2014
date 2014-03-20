package com.microposts.searchWiki;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;

import org.xml.sax.SAXException;

import Variables.Variables;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class WikiCategories {

	public static HashMap<String,Double> getDisambiguations(String query) throws SAXException, IOException {
		if(Variables.isProxy){
			System.setProperty("http.proxyHost", "proxy.iiit.ac.in");
			System.setProperty("http.proxyPort", "8080");
		}
		if(query.trim().replaceAll("[0-9]", "").trim().length()<=0&&query.trim().length()<4){
			query += " number";
		}
		query=URLEncoder.encode(query);
		String uri = "https://en.wikipedia.org/w/api.php?action=query&list=search&srsearch="+query+"&srwhat=text&srlimit=50&format=json&srprop=score&srredirects=false";
		String result = null;
		JsonParser parser = new JsonParser();
		result = RestUtil.makeRestCall(uri);
		JsonArray ret = parser.parse(result).getAsJsonObject().get("query").getAsJsonObject().get("search").getAsJsonArray();
		HashMap<String,Double> results = new HashMap<String,Double>();
		double count = 0;
		for(JsonElement j :ret){
			count ++;
			results.put(j.getAsJsonObject().get("title").getAsString().replaceAll(" ", "_"), 1/count);
		}
		return results;
	}

	public static void main(String[] args) throws SAXException, IOException {
		
		//System.getProperties().put("proxySet", "true");
		if(Variables.isProxy){
			System.setProperty("http.proxyHost", "proxy.iiit.ac.in");
			System.setProperty("http.proxyPort", "8080");
		}
		//BufferedReader reader = new BufferedReader( new FileReader(args[0]));
		System.out.println(getDisambiguations("4"));
		//getURLs("sachin");
		//getAllPages("sachin");
		
	}

}
