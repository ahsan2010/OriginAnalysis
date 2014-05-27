package com.srlab.line;

import java.util.ArrayList;
import java.util.HashMap;


import uk.ac.shef.wit.simmetrics.similaritymetrics.CosineSimilarity;
import uk.ac.shef.wit.simmetrics.similaritymetrics.DiceSimilarity;
import uk.ac.shef.wit.simmetrics.similaritymetrics.Levenshtein;

/**
 * 
 * @author parvez
 * This class implements the a variant of matching algorithm originally proposed by Reiss. It consider the indentation exist
 * in the source file 
 */
public class W_BEST_LINE_MATCHING extends AbstractMatching {
	public static final float THRESHOLD = 0.40f;
	public static final float CONTEXT_LENGTH = 3;
	
	
	protected HashMap hmLeftContext;
	protected HashMap hmRightContext;

	public W_BEST_LINE_MATCHING(){
	
		this.hmLeftContext  = new HashMap();
		this.hmRightContext = new HashMap();
	}
	
	@Override
	public void match() {
		// TODO Auto-generated method stub
		this.clearMapping();
		this.buildContext();
	}
	
	protected void buildContext(){

	}
	protected float calcMatchingScore(int orgLineNum, int revLineNum){
		String leftContent  = this.getOrglLines().get(orgLineNum);			
		String rightContent = this.getRevLines().get(revLineNum);
			
		//float contextSimilarity = new  Levenshtein().getSimilarity((String)this.hmLeftContext.get(orgLineNum),(String)this.hmRightContext.get(revLineNum));
		float contextSimilarity = new  CosineSimilarity().getSimilarity((String)this.hmLeftContext.get(orgLineNum),(String)this.hmRightContext.get(revLineNum));

		float contentSimilarity = new  Levenshtein().getSimilarity(leftContent,rightContent);
		//float contentSimilarity = new  DiceSimilarity().getSimilarity(leftContent,rightContent);
		
		return 0.6f*contentSimilarity+0.4f*contextSimilarity;
	}
	
	public static void main(String args[]){
		System.out.println(""+new  Levenshtein().getSimilarity("I am a boy test", "I am a boy"));
	}
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "W_BEST_LINE_MATCHING";
	}
	
}
