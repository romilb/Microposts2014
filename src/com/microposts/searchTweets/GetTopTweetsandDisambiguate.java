package com.microposts.searchTweets;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TreeSet;

import com.microposts.MentionDetection.GetString;
import com.microposts.searchGCD.Search;

public class GetTopTweetsandDisambiguate {

	public static <K,V extends Comparable<? super V>>
	SortedSet<Map.Entry<K,V>> entriesSortedByValues(Map<K,V> map) {
		SortedSet<Map.Entry<K,V>> sortedEntries = new TreeSet<Map.Entry<K,V>>(
				new Comparator<Map.Entry<K,V>>() {
					@Override public int compare(Map.Entry<K,V> e1, Map.Entry<K,V> e2) {
						if(e2.getValue().compareTo(e1.getValue())==0 || e2.getValue().compareTo(e1.getValue())>0)
							return 1;
						else
							return -1;
					}
				}
				);
		sortedEntries.addAll(map.entrySet());
		return sortedEntries;
	}

	public static HashMap<String,Double> getPopularityDisamb(String mention) throws Exception{
		SearchTweets sc=new SearchTweets();
		GetString gs =new GetString();
		Search search=new Search();
		ArrayList<String> toptweets=sc.getTopTweets(mention);
		HashSet<String> topEntities = new HashSet<String>();
		HashMap<String, Double> results = new HashMap<>();
		for(String i:toptweets){
			//System.err.println(i);
			ArrayList<String> contextWords=gs.tokenizeNouns(i);
			String context = "";
			for(String j:contextWords)
				context += j.trim() + " ";
			//sc.searching("Bluesfest",context);
			//err here
			SortedSet<Map.Entry<String,Double>> s3=entriesSortedByValues(search.searching(mention,context));
			Iterator<Map.Entry<String,Double>> it1=s3.iterator();
			int count=0;
			while(it1.hasNext())
			{
				//System.out.println(it1.next());
				Entry<String, Double> key = it1.next();
				topEntities.add(key.getKey());
				if(results.containsKey(key.getKey()))
					results.put(key.getKey(), key.getValue() + results.get(key.getKey()));
				else
					results.put(key.getKey(), key.getValue());
				count++;
				if(count==20)
					break;
			}
			//System.out.println("end of tweet");
		}
		return results;
	}

	public static void main(String[] args) throws Exception {

		GetString gs =new GetString();
		SearchTweets sc1=new SearchTweets();
		Search sc=new Search();
		String tweet="Thereâs a woman in Floridas with an addiction to eating couches.";
		ArrayList<String> tokens=gs.tokenize(tweet);
		String token="";
		for(String i:tokens)
		{
			token+=i+" AND ";
		}

		token = token.substring(0, token.length()-4);
		token = "s";
		//System.out.println(token);
		ArrayList<String> toptweets=sc1.getTopTweets(token);
		String context = "";
		for(String i:toptweets){
			//System.err.println(i);
			ArrayList<String> contextWords=gs.tokenizeNouns(i);

			for(String j:contextWords)
				context += j.trim() + " ";
			//sc.searching("Bluesfest",context);
			SortedSet<Map.Entry<String,Double>> s3=entriesSortedByValues(sc.searching("Bluesfest",context));
			Iterator<Map.Entry<String,Double>> it1=s3.iterator();
			int count=0;
			while(it1.hasNext())
			{
				System.out.println(it1.next());
				count++;
				if(count==10)
					break;
			}
			System.out.println("end of tweet");
			context = "";
		}
		// find longest ngram as entity.
		// specify both and or condition while querying multiple entities.
		// get disambiguated by GCD or AIDA tool 
		//System.out.println(sc.searching("Justin",context));
	}

}
