package com.marge.split;

import java.util.HashMap;
import java.util.Map;

public class MapMethod {
	
	
	javaxt.io.File[] curRev;
	javaxt.io.File[] nextRev;
	Map <String,Integer> map1 = new HashMap<String,Integer>();
	Map <String,Integer> map2 = new HashMap<String,Integer>();
	
	//Map <Pair,Integer> map3 = new HashMap<Pair,Integer>();
	
	public MapMethod( javaxt.io.File[] curRev , javaxt.io.File[] nextRev,
			Map <String,Integer> map1,Map <String,Integer> map2 ){
		this.curRev = curRev ;
		this.nextRev = nextRev;
		this.map1 = map1;
		this.map2 = map2;
	}
	public void listMethod () {
		
		int curSize = curRev.length;
		int nextSize = nextRev.length;
		
		for ( int i = 0 ; i < curSize ; i++ ){
			map1.put(curRev[i].getName(), i);
		}
		for ( int i = 0 ; i < nextSize ; i++ ){
			map2.put(nextRev[i].getName(), i);
		}
		
	}
}
