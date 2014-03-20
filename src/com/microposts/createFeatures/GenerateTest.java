package com.microposts.createFeatures;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import Variables.Variables;

import com.microposts.MentionDetection.GetString;
import com.microposts.searchGCD.Search;
import com.microposts.searchTweets.GetTopTweetsandDisambiguate;
import com.microposts.searchWiki.RemoveDisambiguationPages;
import com.microposts.searchWiki.WikiCategories;

public class GenerateTest {
	public static final double wikiWeight = 25;
	public static final double gcdWeight = 10;
	public static final double tweetWeight = .1;

	public static double returnScore(HashMap<String, Double> res , String key){
		if(res.containsKey(key))
			return res.get(key);
		else
			return -1;
	}

	public static int returnRank(HashMap<String, Double> res , String key){
		SortedSet<Map.Entry<String, Double>> sort = GetTopTweetsandDisambiguate.entriesSortedByValues(res);
		Iterator<Map.Entry<String,Double>> it1=sort.iterator();
		int count=0;
		while(it1.hasNext())
		{
			count ++;
			//System.err.println("AsdasdASD"+it1.next().getKey());
			if(key.equals(it1.next().getKey()))
				return count;
		}
		return 10000;
	}

	public static HashSet<String> printTopErrors(HashMap<String, Double> wikis,  HashMap<String, Double> gcds, HashMap<String, Double> tweets, int qid,String i,String id, HashSet<String> entities){
		HashSet<String> results = new HashSet<>();
		HashMap<String, Double> scores = new HashMap<>();
		String ret = "";
		SortedSet<Map.Entry<String, Double>> wiki = GetTopTweetsandDisambiguate.entriesSortedByValues(wikis);
		SortedSet<Map.Entry<String, Double>> gcd = GetTopTweetsandDisambiguate.entriesSortedByValues(gcds);
		SortedSet<Map.Entry<String, Double>> tweet = GetTopTweetsandDisambiguate.entriesSortedByValues(tweets);

		Iterator<Map.Entry<String,Double>> it1=wiki.iterator();
		int count=0;
		while(it1.hasNext()&&count<3)
		{
			count ++;

			String err = it1.next().getKey();
			double score = wikis.get(err)*wikiWeight;
			if(entities.contains(err))
				ret = "1";
			else ret = "0";
			ret += " qid:"+qid+" ";
			ret += "1:" +count+ " ";
			//ret += "1:"+wikis.get(err)+" ";
			Iterator<Map.Entry<String,Double>> it2=gcd.iterator();
			Iterator<Map.Entry<String,Double>> it3=tweet.iterator();
			int count1 = 0;
			boolean flag = false;
			while(it2.hasNext()){
				count1 ++;
				if(err.equals(it2.next().getKey())){
					flag = true;
					break;
				}
			}
			if(flag){
				ret += "2:"+count1+" ";
				//ret += "2:"+gcds.get(err)+" ";
				score += gcds.get(err)*gcdWeight;
			}
			else{
				ret += "2:0 ";
			}

			count1 = 0;
			flag = false;
			while(it3.hasNext()){
				count1 ++;
				if(err.equals(it3.next().getKey())){
					flag = true;
					break;
				}
			}
			if(flag){
				ret += "3:"+count1+" ";
				//ret += "3:"+tweets.get(err)+" ";
				score += tweets.get(err)*tweetWeight;
			}
			else
				ret += "3:0 ";
			results.add(ret.trim() + " #"+id+"#"+i+"#"+err);
			scores.put(err, score);
			//ret +="\n";

		}


		it1=gcd.iterator();
		count=0;
		while(it1.hasNext()&&count<3)
		{
			count ++;
			String err = it1.next().getKey();
			double score = gcds.get(err)*gcdWeight;
			if(entities.contains(err))
				ret = "1";
			else ret = "0";
			ret += " qid:"+qid+" ";

			Iterator<Map.Entry<String,Double>> it2=wiki.iterator();
			Iterator<Map.Entry<String,Double>> it3=tweet.iterator();
			int count1 = 0;
			boolean flag = false;
			while(it2.hasNext()){
				count1 ++;
				if(err.equals(it2.next().getKey())){
					flag = true;
					break;
				}
			}
			if(flag){
				ret += "1:"+count1+" ";
				//ret += "1:"+wikis.get(err)+" ";
				score += wikis.get(err)*wikiWeight;
			}
			else
				ret += "1:0 ";

			ret += "2:" +count+ " ";
			//ret += "2:"+gcds.get(err)+" ";

			count1 = 0;
			flag = false;
			while(it3.hasNext()){
				count1 ++;
				if(err.equals(it3.next().getKey())){
					flag = true;
					break;
				}
			}
			if(flag){
				ret += "3:"+count1+" ";
				//ret += "3:"+tweets.get(err)+" ";
				score += tweets.get(err)*tweetWeight;
			}
			else
				ret += "3:0 ";
			results.add(ret.trim() + " #"+id+"#"+i+"#"+err);
			//ret +="\n";

			scores.put(err, score);

		}

		it1=tweet.iterator();
		count=0;
		while(it1.hasNext()&&count<3)
		{
			count ++;
			String err = it1.next().getKey();
			double score = tweets.get(err)*tweetWeight;
			if(entities.contains(err))
				ret = "1";
			else ret = "0";
			ret += " qid:"+qid+" ";

			Iterator<Map.Entry<String,Double>> it2=wiki.iterator();
			Iterator<Map.Entry<String,Double>> it3=gcd.iterator();
			int count1 = 0;
			boolean flag = false;
			while(it2.hasNext()){
				count1 ++;
				if(err.equals(it2.next().getKey())){
					flag = true;
					break;
				}
			}
			if(flag){
				ret += "1:"+count1+" ";
				//ret += "1:"+wikis.get(err)+" ";
				score += wikis.get(err)*wikiWeight;
			}
			else
				ret += "1:0 ";


			count1 = 0;
			flag = false;
			while(it3.hasNext()){
				count1 ++;
				if(err.equals(it3.next().getKey())){
					flag = true;
					break;
				}
			}
			if(flag){
				ret += "2:"+count1+" ";
				//ret += "2:"+gcds.get(err)+" ";
				score += gcds.get(err)*gcdWeight;
			}
			else
				ret += "2:0 ";

			ret += "3:" +count+ " ";
			//ret += "3:"+tweets.get(err)+" ";

			results.add(ret.trim()+ " #"+id+"#"+i+"#"+err);
			scores.put(err, score);
			//ret +="\n";

		}
		//System.out.println(scores);
		double max = -1;
		String answer = "";
		for(String j:scores.keySet()){
			if((max<scores.get(j))){
				max = scores.get(j);
				answer = j;
			}
		}
		System.out.println(answer);
		//System.out.println(ret);
		return results;
	}

	public static String returnBest(HashMap<String, Double> res){
		double max = -1;
		String name = "";
		for(String i : res.keySet()){
			if(max<res.get(i)){
				name = i;
				max = res.get(i);
			}
		}
		return name;
	}

	public static void main(String[] args) throws Exception {

		boolean debug = false;
		boolean test = false;
		BufferedReader br =new BufferedReader(new FileReader(Variables.trainingPath));
		BufferedWriter bw = new BufferedWriter(new FileWriter(Variables.feature));
		String tweet_text= "";
		GetString ps=new GetString();
		int count = 0;
		Search sc=new Search();
		int qid = 0;
		while((tweet_text=br.readLine())!=null){
			Thread.sleep(50);
			String tweet_id = tweet_text.split("\t")[0];
			HashSet<String> entities = new HashSet<>();
			if(tweet_text.split("\t").length>2){
				for(int i = 3;i < tweet_text.split("\t").length; i+=2){
					entities.add(tweet_text.split("\t")[i].replaceAll("http://dbpedia.org/resource/", ""));
				}
			}
			System.out.println(entities);
			tweet_text = tweet_text.split("\t")[1].substring(1, tweet_text.split("\t")[1].length()-1);
			System.out.println(tweet_text);
			ArrayList<String> tokens = ps.tokenize(tweet_text);
			String value = "";
			for(String i : tokens){
				//System.out.println(i);
				HashMap<String, Double> gcdResults = sc.searching(i,ps.tokenizeNounVerb(tweet_text));
				HashMap<String, Double> wikiResults;
				HashMap<String, Double> twitResults;
				try{
					if(i.trim().length()>0)
						wikiResults = WikiCategories.getDisambiguations(i.replaceAll("#", ""));
					else
						wikiResults = new HashMap<String, Double>();
				}catch(Exception e){
					wikiResults = new HashMap<String, Double>();
				}
				try{
					if(i.trim().length()>0)
						twitResults = GetTopTweetsandDisambiguate.getPopularityDisamb(i);
					else
						twitResults = new HashMap<String, Double>();
				}catch(Exception e){
					twitResults = new HashMap<String, Double>();
				}
				Set<String> gcdArray = gcdResults.keySet();
				//wikiResults.retainAll(gcdArray);
				RemoveDisambiguationPages rs = new RemoveDisambiguationPages();
				qid+=1;
				//System.err.println(returnBest(rs.removeDisamPages(wikiResults)) + " " + returnBest(rs.removeDisamPages(gcdResults))+ " " + returnBest(rs.removeDisamPages(twitResults)));

				HashSet<String> errors = new HashSet<>();
				errors.addAll(printTopErrors(rs.removeDisamPages(wikiResults),rs.removeDisamPages(gcdResults),rs.removeDisamPages(twitResults),qid,i,tweet_id,entities));
				for(String ll:errors){
					//System.out.println(ll);
					bw.append(ll);
					bw.newLine();
					bw.flush();
				}
			}
		}
		br.close();
		bw.close();
	}

}
