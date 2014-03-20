package com.microposts.searchTweets;

/*
 * Copyright 2007 Yusuke Yamamoto
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.util.ArrayList;
import java.util.List;

import Variables.Variables;

/**
 * @author Yusuke Yamamoto - yusuke at mac.com
 * @since Twitter4J 2.1.7
 */
public class SearchTweets {
	Twitter twitter =null;

	public static void main(String args[])
	{
		SearchTweets sc =new SearchTweets();
		System.out.println(sc.getTopTweets("reading"));
	}

	public SearchTweets()
	{
		if(Variables.isProxy){
			System.setProperty("http.proxyHost", "proxy.iiit.ac.in");
			System.setProperty("http.proxyPort", "8080");
		}
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setJSONStoreEnabled(true);
		cb.setDebugEnabled(true);
		cb.setOAuthConsumerKey("KJX5xGQ4HqSGWG8GfioI6w");
		cb.setOAuthConsumerSecret("nAJy5IRa4TKpI4aSryQH35tSEHatfP6AUvAHXAnX4bM");
		cb.setOAuthAccessToken("240974125-45UNENap7Zvv6COLOxYRa98I7rGqjhFrZqod160q");
		cb.setOAuthAccessTokenSecret("z2XXebjqUmWOUrjUDoV7GgdL87ahocJRrfbcEHjw");
		cb.setUseSSL(true);
		twitter = new TwitterFactory(cb.build()).getInstance();
	}
	/**
	 * Usage: java twitter4j.examples.search.SearchTweets [query]
	 *
	 * @param args
	 */
	public ArrayList<String> getWordsForActions(String action){
		ArrayList<String> topTweets=new ArrayList<String>();
		try {
			Query query = new Query(action);
			query.setResultType("mixed");
			query.setLang("en");
			query.setCount(50);
			QueryResult result;
			result = twitter.search(query);
			int count=0;
			List<Status> tweets = result.getTweets();
			for (Status tweet : tweets) {
				topTweets.add((String) tweet.getText().replaceAll("\n", " ").trim());
				count++;
				if(count==5)
					break;
			}


		} catch (TwitterException te) {
			te.printStackTrace();
			System.out.println("Failed to search tweets: " + te.getMessage());
			//System.exit(-1);
		}
		return topTweets;
	}

	public ArrayList<String> getTopTweets(String queryword) {
		ArrayList<String> topTweets=new ArrayList<String>();
		try {
			Query query = new Query(queryword);
			query.setResultType("mixed");
			query.setLang("en");
			query.setCount(5);
			QueryResult result;
			result = twitter.search(query);
			int count=0;
			List<Status> tweets = result.getTweets();
			for (Status tweet : tweets) {
				topTweets.add((String) tweet.getText().replaceAll("\n", " ").trim());
				count++;
				if(count==5)
					break;
			}


		} catch (TwitterException te) {
			te.printStackTrace();
			System.out.println("Failed to search tweets: " + te.getMessage());
			//System.exit(-1);
		}
		return topTweets;
	}
}