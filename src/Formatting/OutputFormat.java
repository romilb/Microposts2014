package Formatting;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.TreeMap;

import Variables.Variables;

public class OutputFormat {
	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader("features.txt"));
		BufferedReader br2 = new BufferedReader(new FileReader("hjfjf"));
		BufferedReader br3 = new BufferedReader(new FileReader(Variables.trainingPath));
		String tweet = "";
		int currId = 1;
		int nextId = 0;
		String rankedResults = "";
		String prevLine = "";
		int countLines = 0;
		HashMap<String,HashMap<String,String>> tweetResults = new HashMap<>();
		HashMap<String,String> idTweet = new HashMap<>();

		TreeMap<Double, Integer> rankMap = new TreeMap<>();
		int noMentions = 0;
		while((rankedResults = br2.readLine())!=null){

			nextId = Integer.parseInt(rankedResults.split("\t")[0]);
			if(currId == nextId){
				noMentions++;
				rankMap.put(Double.parseDouble(rankedResults.split("\t")[2]), Integer.parseInt(rankedResults.split("\t")[1]));
			}
			else{
				//rankMap.clear();
				String features = "";
				//System.out.println(rankMap);
				int result = rankMap.pollLastEntry().getValue();
				//System.out.println(noMentions+"\t"+result+"\t"+rankMap);
				for(int i =0; i<noMentions;i++){
					if(i==result){
						features = br.readLine();
					}
					else{
						br.readLine();
					}
				}
				//System.err.println(features);
				String targetTweetId = features.split("#")[1].trim();

				HashMap<String, String> mantionEntity;
				if(tweetResults.containsKey(targetTweetId))
					mantionEntity = tweetResults.get(targetTweetId);
				else{
					mantionEntity = new HashMap<>();
				}
				mantionEntity.put(features.split("#")[2].trim(), features.split("#")[3].trim());
				tweetResults.put(targetTweetId, mantionEntity);
				currId = nextId;
				prevLine = rankedResults;
				rankMap.clear();
				rankMap.put(Double.parseDouble(prevLine.split("\t")[2]), Integer.parseInt(prevLine.split("\t")[1]));
				noMentions = 1;
				continue;
			}
		}
		String features = "";
		int result = rankMap.pollLastEntry().getValue();
		for(int i =0; i<noMentions;i++){
			if(i==result){
				features = br.readLine();
			}
			else{
				br.readLine();
			}
		}
		String targetTweetId = features.split("#")[1].trim();

		HashMap<String, String> mantionEntity;
		if(tweetResults.containsKey(targetTweetId))
			mantionEntity = tweetResults.get(targetTweetId);
		else{
			mantionEntity = new HashMap<>();
		}
		mantionEntity.put(features.split("#")[2].trim(), features.split("#")[3].trim());
		tweetResults.put(targetTweetId, mantionEntity);


		for(String i:tweetResults.keySet()){
			//System.out.println(i+"  "+tweetResults.get(i));
		}


		while((tweet = br3.readLine())!=null){
			TreeMap<Integer,String> tweetEntity =new TreeMap<Integer,String>();
			String tweetId = tweet.split("\t")[0].trim();
			String tweetText = tweet.split("\t")[1].trim();

			try{
				for(String i:tweetResults.get(tweetId).keySet()){
					int index = tweetText.indexOf(i);
					boolean removes = false;
					if(index < 0){
						index = tweet.indexOf(i.substring(0,4));
					}
					if(index < 0){
						index = tweet.indexOf(i.substring(0,3));
					}
					if(index < 0){
						index = tweet.indexOf(i.substring(0,2));
					}
					if(i.endsWith("'s")&&!tweetResults.get(tweetId).get(i).contains("'s")){
						removes = true;
						//i= i.replace("'s", "");
					}
					//if(tweetText.toLowerCase().contains(tweetResults.get(tweetId).get(i).replaceAll("_", " ").toLowerCase()))
					//i = tweetText.substring(tweetText.toLowerCase().indexOf(tweetResults.get(tweetId).get(i).replaceAll("_", " ").toLowerCase(),tweetText.toLowerCase().indexOf(tweetResults.get(tweetId).get(i).replaceAll("_", " ").toLowerCase())+tweetResults.get(tweetId).get(i).replaceAll("_", " ").length()));
					if(!removes)
						tweetEntity.put(index, i+"\t"+"http://dbpedia.org/resource/"+tweetResults.get(tweetId).get(i));
					else
						tweetEntity.put(index, i.replace("'s", "")+"\t"+"http://dbpedia.org/resource/"+tweetResults.get(tweetId).get(i));
					int next = 1;
					while(next > 0){
						//System.out.println(tweetText + " " + i + " " + index);
						next = tweetText.substring(index+i.length()).indexOf(i+ " ");
						index = index + next;
						if(next > 0){
							tweetEntity.put(index, i+"\t"+"http://dbpedia.org/resource/"+tweetResults.get(tweetId).get(i));
						}
					}

					//check for multiple mentions??
				}
			}
			catch(Exception e){

			}
			String finalString = tweetId;
			for(int i : tweetEntity.keySet()){
				finalString +="\t"+tweetEntity.get(i);
			}
			System.out.println(finalString);

		}
		br.close();
		br2.close();
		br3.close();

	}
}

