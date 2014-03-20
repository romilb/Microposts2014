package com.microposts.MentionDetection;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.StringTokenizer;

import Variables.Variables;
import cmu.arktweetnlp.Tagger;
import cmu.arktweetnlp.Tagger.TaggedToken;

public class GetString {
	String url_string=Variables.ritterSystem;
	Tagger t1;
	HashMap<String,String> entitymap=new HashMap<String, String>();
	ArrayList<String> stopwords;


	public GetString() throws Exception {
		t1 = new Tagger();
		try {
			t1.loadModel(Variables.modelPath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		stopwords = new ArrayList<>();
		try{
			BufferedReader br = new BufferedReader(new FileReader(Variables.stopwordList));
			String line = "";
			while((line = br.readLine())!=null){
				stopwords.add(line.trim());
			}
		}
		catch(Exception e){

		}
	}


	public static void main(String args[]) throws Exception
	{
		GetString ps=new GetString();
		System.out.println(ps.tokenize("RT @Leo_Tweets I am going to visit Sachin,Dravid"));
		System.out.println(ps.tokenize("RT @Leo_Tweets #Leo's have the most powerful sexual appeal of the entire Zodiac"));
	}

	public ArrayList<String> tokenizeNouns(String tweet) throws IOException {
		//String entityname=entityid;
		String l = tweet.replaceAll("\\s+", " ");
		String l1 = l.replaceAll("[^\\x00-\\x7F]", "");
		List<TaggedToken> pos_list = t1.tokenizeAndTag(l1);
		ArrayList<String> arr = new ArrayList<String>();
		String curr = "";
		for(int i=0;i<pos_list.size();i++){
			String tag1=pos_list.get(i).tag;
			String word1=pos_list.get(i).token;
			//System.out.println(word1 + " " + tag1);
			if(tag1.compareTo("Z")==0 || tag1.compareTo("#")==0 ||  tag1.compareTo("^")==0  ||tag1.compareTo("S")==0 ||tag1.compareTo("N")==0){
				curr+=word1+" ";
			}
			else if(!curr.equals("")){
				arr.add(curr.trim());
				curr = "";
			}
		}
		if(!curr.equals("")){
			arr.add(curr.trim());
			curr = "";
		}
		return arr;
	}

	public String tokenizeNounVerb(String tweet) throws IOException {
		//String entityname=entityid;
		String l = tweet.replaceAll("\\s+", " ");
		String l1 = l.replaceAll("[^\\x00-\\x7F]", "");
		List<TaggedToken> pos_list = t1.tokenizeAndTag(l1);
		ArrayList<String> arr = new ArrayList<String>();
		String curr = "";
		for(int i=0;i<pos_list.size();i++){
			String tag1=pos_list.get(i).tag;
			String word1=pos_list.get(i).token;
			//System.out.println(word1 + " " + tag1);
			if(tag1.compareTo("Z")==0 || tag1.compareTo("#")==0 ||  tag1.compareTo("^")==0  ||tag1.compareTo("S")==0 ||tag1.compareTo("N")==0 || tag1.compareTo("V")==0){
				curr+=word1+" ";
			}
			else if(!curr.equals("")){
				arr.add(curr.trim());
				curr = "";
			}
		}
		if(!curr.equals("")){
			arr.add(curr.trim());
			curr = "";
		}
		String newTweet = "";
		for(String i:arr)
			newTweet += i + " ";
		//System.out.println(newTweet);
		return newTweet.trim();
	}

	public ArrayList<String> tokenize(String tweet) throws IOException {
		return tokenize(tweet, 0);
	}

	public ArrayList<String> tokenize(String tweet, int method) throws IOException {
		/*
		 * Get Mentions Using Twical
		 * 0 = Both; 1 = Twical; 2 = ARK
		 */
		tweet = tweet.replace("&", "and").replaceAll(";", ":");
		ArrayList<String> twical = named_entities(tweet);
		//System.out.println("Twical: "+twical);
		
		if(method==1)
			return twical;

		/*
		 * Get Mentions using ARK
		 * 
		 */
		tweet = tweet.replaceAll("#", " ");
		String l = tweet.replaceAll("\\s+", " ");
		String l1 = l.replaceAll("[^\\x00-\\x7F]", " ");
		List<TaggedToken> pos_list = t1.tokenizeAndTag(l1);
		ArrayList<String> arr = new ArrayList<String>();
		String curr = "";
		for(int i=0;i<pos_list.size();i++){
			String tag1=pos_list.get(i).tag;
			String word1=pos_list.get(i).token;
			//System.out.println(word1 + " " + tag1);
			if(tag1.compareTo("Z")==0 || tag1.compareTo("#")==0 ||  tag1.compareTo("^")==0  ||tag1.compareTo("S")==0 ||tag1.compareTo("$")==0){
				if(tag1.compareTo("$")==0 && (word1.replaceAll("[0-9a-zA-Z]", "").trim().length()>0)){

				}
				else if(!stopwords.contains(word1.toLowerCase()))
					curr+=word1+" ";

			}
			else if(!curr.equals("") && tag1.compareTo("$")==0){
				curr+=word1+" ";
			}
			else if(!curr.equals("")){
				arr.add(curr.trim());
				curr = "";
			}
		}

		if(!curr.equals("")){
			arr.add(curr.trim());
			curr = "";
		}
		//System.out.println(tweet);
		//System.out.println("MyParser: "+arr);
		if(method==2)
			return arr;
		/*
		 * Merging mentions from 2 systems
		 */
		HashSet<String> merge = new HashSet<>();
		ArrayList<String> merged = new ArrayList<>();
		for(String x:arr){
			boolean flag = false;
			for(String y:twical){
				if(x.contains(y)){
					merge.add(x);
					flag = true;
				}
				else if(y.contains(x)){
					merge.add(y);
					flag = true;
				}
			}
			if(!flag && !stopwords.contains(x)){
				merge.add(x);
			}
		}

		for(String x:twical){
			boolean flag = false;
			for(String y:arr){
				if(x.contains(y)){
					merge.add(x);
					flag = true;
				}
				else if(y.contains(x)){
					merge.add(y);
					flag = true;
				}
			}
			if(!flag && !stopwords.contains(x)){
				merge.add(x);
			}
		}
		//merge = enhance(merge,tweet);
		
		merged.addAll(merge);
		System.out.println("Merged: "+merge);

		return merged;
		//return arr;

	}
	
	public ArrayList<String> named_entities(String tweet)
	{
		String part="";
		ArrayList<String> named_entities=new ArrayList<String>();
		try {
			tweet=tweet.replaceAll("#","");
			String value=parse(tweet);
			//System.out.println(value);
			StringTokenizer st = new StringTokenizer(value," ",true);
			int count=0;
			while (st.hasMoreTokens()) {
				String tokens[] = st.nextToken().split("\\/");

				if(tokens.length==4)
				{
					//System.out.println(tokens[1]);
					if((tokens[1].startsWith("B-")||tokens[2].startsWith("NNP"))&&!stopwords.contains(tokens[0].trim().toLowerCase()))
					{
						count=1;
						part+=tokens[0]+" ";
					}
					else if(tokens[1].startsWith("I-")&&!stopwords.contains(tokens[0].trim().toLowerCase())){
						part+=tokens[0]+" ";
					}
					else if(tokens[1].startsWith("B-ENTITY")){
						if(count==1)
						{
							count=0;
							named_entities.add(part.trim());
							part="";
						}
						count=1;
						part+=tokens[0]+" ";
					}
					else if(tokens[1].startsWith("I-ENTITY")){
						part+=tokens[0]+" ";
						count = 1;
					}
					else{
						if(count==1)
						{
							count=0;
							named_entities.add(part.trim());
							part="";
						}

					}
				}

			}
			if(count==1)
			{
				count=0;
				named_entities.add(part.trim());
				part="";
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return named_entities;
	}
	
	
	public String parse(String Tweet) throws IOException {
		String parse_string="";
		try {
			URL url = new URL(url_string);
			HttpURLConnection uc = (HttpURLConnection) url.openConnection();
			uc.setRequestMethod("POST");
			uc.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(uc.getOutputStream());
			wr.writeBytes("tweet="+Tweet);
			wr.flush();
			//uc.setRequestProperty( "Content-type", "text/xml" );
			//uc.setRequestProperty( "Accept", "text/xml" );
			int rspCode = uc.getResponseCode();
			if (rspCode == 200) {
				InputStream is = uc.getInputStream();
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr);
				parse_string = br.readLine();
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return parse_string;

	}
}
