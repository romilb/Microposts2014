package com.microposts.searchGCD;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.json.simple.JSONObject;

import Variables.Variables;


public class Search{

	static String path=null;
	static int required;
	public Search()
	{
		path=Variables.googleConcepts;
		required=100;
	}

	@SuppressWarnings("deprecation")
	public static void main(String [] args) throws IOException, ParseException 
	{
		Search sc=new Search();
		//System.out.println(sc.searching("concept:Sport"));
		System.out.println(sc.searching("Sports", "Sports"));
		//sc.searching("Leo^3 Friendship");
	}


	<K,V extends Comparable<? super V>>
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

	public HashMap<String,Double> searching(String ne, String q) throws IOException
	{ 
		ne = ne.replaceAll("#", " ");
		q=q.replaceAll("[\"():/,\t^?:*&%$#@!=+';-\\]\\[]"," ");
		for(String i:ne.split(" ")){
			if(i.trim().length() > 1)
				q = q + " " +i+"^3 ";
		}
		q = q.trim();
		IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(path)));
		//TFIDFSimilarity tfidf = new DefaultSimilarity();
		//searcher.setSimilarity(tfidf);
		IndexSearcher searcher = new IndexSearcher(reader);
		Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_40);
		QueryParser parser = new QueryParser(Version.LUCENE_40, "text", analyzer);

		Query query;
		try {
			query = parser.parse(q);
			//System.out.println(query);
			HashMap<String,Double> hm = new HashMap<String,Double>();
			TopDocs results = searcher.search(query, 1000);
			//TopDocs results = searcher.search(fuzzy, 1000);
			ScoreDoc[] hits = results.scoreDocs;
			int numTotalHits = results.totalHits>10000?10000:results.totalHits;
			if(numTotalHits > 0)
				hits = searcher.search(query, numTotalHits).scoreDocs;
			//System.out.println(numTotalHits + " total matching documents");
			int count=1;
			for(int i=0;i<numTotalHits;i++)
			{
				Document doc = searcher.doc(hits[i].doc);
				//System.out.println("Document "+(i+1)+" : "+doc.get("text")+" -> "+doc.get("concept")+" -> "+doc.get("probability"));

				if(hm.containsKey(doc.get("concept"))){
					//Double val = hm.get(doc.get("concept")) + 1.0/count+ computeScore(ne, doc.get("text"));
					Double val = hm.get(doc.get("concept")) + computeScore(ne, doc.get("text"));
					hm.put(doc.get("concept"), val);

				}
				else{
					hm.put(doc.get("concept"), computeScore(ne, doc.get("text")));
					//hm.put(doc.get("concept"), 1.0/count+computeScore(ne, doc.get("text")));
				}
				count++;
				if(count>required)
					break;
			}
			return hm;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return new HashMap<String,Double>();
	}

	String getBest(HashMap<String,Double> hm){
		double max = -1;
		String mapped = "";
		for(String i:hm.keySet()){
			if(max < hm.get(i)){
				max = hm.get(i);
				mapped = i;
			}
		}
		return mapped;
	}

	HashMap<String,Double> rerank(HashMap<String,Double> hm ){
		return hm;
	}

	Double computeScore(String entity,String document){
		entity  = entity.toLowerCase();
		document = document.toLowerCase();
		entity = entity.replaceAll("_", " ");
		document = document.replaceAll("_", " ");
		String[] entityWords = entity.split(" ");
		String[] docWords = document.split(" ");
		HashSet<String> entityhs = new HashSet<>();
		HashSet<String> dochs = new HashSet<>();
		HashSet<String> union = new HashSet<>();
		for(String i:entityWords){
			entityhs.add(i);
			union.add(i);
		}
		for(String i:docWords){
			dochs.add(i);
			union.add(i);
		}
		entityhs.retainAll(dochs);
		Double score = entityhs.size()*1.0/union.size();
		//System.out.println(entity+" "+ document+" "+score);
		return score;
	}

}


