package com.srlab.line;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import uk.ac.shef.wit.simmetrics.similaritymetrics.JaccardSimilarity;
import uk.ac.shef.wit.simmetrics.tokenisers.InterfaceTokeniser;
import uk.ac.shef.wit.simmetrics.wordhandlers.InterfaceTermHandler;

//Shingling are same to n gram except they don not conrain redundant token
public class ShingleTokenizer implements InterfaceTokeniser{

	private int shingleSize;
	private boolean ignorespace;
	private InterfaceTokeniser installTokeniser;

	public ShingleTokenizer(int _shingleSize, boolean _ignorespace){
		this.shingleSize = _shingleSize;
		this.ignorespace = _ignorespace;
	}
	public ShingleTokenizer(int _shingleSize, boolean _ignorespace, InterfaceTokeniser tokenizer){
		this.shingleSize = _shingleSize;
		this.ignorespace = _ignorespace;
		this.installTokeniser = tokenizer;
	}
	@Override
	public String getDelimiters() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getShortDescriptionString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InterfaceTermHandler getStopWordHandler() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setStopWordHandler(InterfaceTermHandler arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ArrayList<String> tokenizeToArrayList(String arg0) {
		// TODO Auto-generated method stub
		
		ArrayList<String> tokenList = new ArrayList<String>();
		HashMap<String,Integer> hmTokenList = new HashMap<String,Integer>();
		
		//Step-1 remove newline chanracters
		String input = arg0.replaceAll("\\r\\n|\\n|\\r"," ");
		
		if(this.ignorespace){
			input = input.replaceAll("\\s+", "");
		}
		
		if(input.length()%this.shingleSize!=0){
			//add padding
			int remainder = input.length()%this.shingleSize;
			StringBuffer padding = new StringBuffer();
			for(int i=0;i<remainder;i++){
				padding.append("_");
			}
			input = input+ padding.toString();
		}
		for(int i=0, count =1;i+this.shingleSize<input.length();i++,count++){
			hmTokenList.put(input.substring(i,i+this.shingleSize),count);
		}
		
		//System.out.println(" Shingles : "+hmTokenList.keySet());
		return new ArrayList(hmTokenList.keySet());
	}

	@Override
	public Set<String> tokenizeToSet(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public static void main(String args[]){
		JaccardSimilarity jsd = new JaccardSimilarity(new ShingleTokenizer(3,true));
		System.out.println("Similarity = "+jsd.getSimilarity("abstractVoidAction avd","abstracFieldAction = new arrayList()"));

	}

}
