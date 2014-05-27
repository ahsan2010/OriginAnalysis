package com.srlab.line;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import com.marge.split.MatchLines;

/* Abstract matching class hash two function that need to be override. One is the getName 
 * and another is the match function. The first one returns the name of the matching technique
 * All line numbers start with 1. This is done by adding a dummy line when input files have been
 * converted to the list of lines. See utility class @FileToLines for better understanding
 */

public abstract class AbstractMatching {
	File orgFile;
	File revFile;
	private long time;
	private HashMap<Integer,HashMap<Integer,Integer>> fwdMapping;
	int line1 ;
	int line2 ;
	private   ArrayList<String> orglLines;
	private   ArrayList<String> revLines;
	
	public void init(String fbody, String sbody){
		//this.orgFile = new File(_orgFile);
		//this.revFile = new File(_revFile);
		
		this.fwdMapping = new HashMap<Integer, HashMap<Integer,Integer>>();
		
		this.orglLines  = FileToLines.convert(fbody);
		this.revLines   = FileToLines.convert(sbody); 
		this.time=-1;
	}
	
	public void setOrglLines(ArrayList<String> orglLines) {
		this.orglLines = orglLines;
	}

	public void setRevLines(ArrayList<String> revLines) {
		this.revLines = revLines;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public long getTime() {
		return time;
	}

	public File getOrgFile() {
		return orgFile;
	}

	public File getRevFile() {
		return revFile;
	}

	public HashMap<Integer, HashMap<Integer,Integer>> getFwdMapping() {
		return fwdMapping;
	}

	public ArrayList<String> getOrglLines() {
		return orglLines;
	}

	public ArrayList<String> getRevLines() {
		return revLines;
	}

	/**
	 * Clear the content of the mapping. 
	 */
	public void clearMapping(){
		if( this.getFwdMapping().size() >0 )
			this.getFwdMapping().clear();
	}
	public void add(int key, int value){
		/* We assume that a line can be split, thus for a key we have a HashMap that contains the
		 * split line numbers. Since a HashMap itself contains key-value pair we set the same line 
		 * number as both key and value
		 * IMPORTANT NOTE: This approach cannot detect line merging 
		 */
		
		if(this.getFwdMapping().containsKey(key)){
			HashMap<Integer,Integer> hm = (HashMap<Integer,Integer>)this.getFwdMapping().get(key);
			hm.put(value, value);
			this.getFwdMapping().put(key, hm);
		}
		else{
			HashMap<Integer,Integer> hm = new HashMap<Integer,Integer>();
			hm.put(value, value);
			this.getFwdMapping().put(key, hm);
		}
	}
	/*
	 * Implement the following abstract method
	 */
	public abstract String getName();
	public abstract void match();
	
	
	public void print(){
		//System.out.println("Old File: "+ this.getOrgFile().getAbsolutePath());
		//System.out.println("New File: "+ this.getRevFile().getAbsolutePath());
		
		for(int i=1;i<this.getOrglLines().size();i++){
			if(this.getFwdMapping().containsKey(i)){
				
				Set<Integer> set = this.getFwdMapping().get(i).keySet();
				ArrayList<Integer> list = new ArrayList<Integer>(set);
				if(list.size()==1 && list.get(0)==-1){
					return;
				}
				
				Iterator<Integer> iterator = set.iterator();
				
				while(iterator.hasNext()){
					
					Integer key   = i;  
					Integer value = (Integer)iterator.next();
					
					System.out.println(key+"->"+value);
				}
			}
		}
	}
	

	
	public ArrayList<MatchLines> matchLines () {
		int tot = 0 ;
		
		ArrayList<MatchLines> matchLine = new ArrayList<MatchLines>();
		
		for(int i=1;i<this.getOrglLines().size();i++){
			if(this.getFwdMapping().containsKey(i)){
				
				Set<Integer> set = this.getFwdMapping().get(i).keySet();
				ArrayList<Integer> list = new ArrayList<Integer>(set);
				
			
			
				Iterator<Integer> iterator = set.iterator();
				if(list.size()==1 && list.get(0)==-1){
					//System.out.println(String.format("%-35s ->%s","["+(i)+"]"+this.getOrglLines().get(i),"DEL"));	
					//break;
				}
				else{
					while(iterator.hasNext()){
						
						Integer key   = i;  
						Integer value = (Integer)iterator.next();
						MatchLines match = new MatchLines( i , value);
						//System.out.println(key+"->"+value);
						matchLine.add(match);
					}
				}
			}
		}
		
		return matchLine ;
	}
	
	public void printLine(){
				
		System.out.println("Print Line");
		for(int i=1;i<this.getOrglLines().size();i++){ //line number start with 1
			
			if(this.getFwdMapping().containsKey(i))
			{
				Set<Integer> set = this.getFwdMapping().get(i).keySet();
				ArrayList<Integer> list = new ArrayList<Integer>(set);
				if(list.size()==1 && list.get(0)==-1){
					System.out.println(String.format("%-35s ->%s","["+(i)+"]"+this.getOrglLines().get(i),"DEL"));	
				}
				else{
					Iterator<Integer> iterator = set.iterator();
					while(iterator.hasNext()){
						Integer key   = i;  
						Integer value = (Integer)iterator.next();												
						System.out.println(String.format("%-35s ->%s","["+(i)+"]"+this.getOrglLines().get(i),"["+(value)+"]"+this.getRevLines().get(value)));
					}
				}
			 }
			else{
				System.out.println(String.format("%-35s ->%s","["+(i)+"]"+this.getOrglLines().get(i),"UN+DEL"));		
			}
		 }
	 }
}
