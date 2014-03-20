package com.microposts.searchWiki;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

import Variables.Variables;

public class RemoveDisambiguationPages {

	HashSet<String> hs;

	public RemoveDisambiguationPages() {

		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(Variables.disamPages));

			String line = "";
			hs = new HashSet<>();
			while( (line = br.readLine())!=null){
				hs.add(line);
				//System.out.println(line);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public boolean isDisamPage(String title){
		if(hs.contains(title))
			return true;
		else
			return false;

	}
	
	public HashMap<String, Double> removeDisamPages(HashMap<String, Double> res){
		HashMap<String, Double> nerRes = new HashMap<>();
		for(String i:res.keySet()){
			if(!isDisamPage(i)){
				nerRes.put(i, res.get(i));
			}
		}
		return nerRes;
	}

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		RemoveDisambiguationPages rm = new RemoveDisambiguationPages();
		System.out.println(rm.isDisamPage("Aquarius"));
	}

}
