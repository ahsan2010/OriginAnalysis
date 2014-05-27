
/**
 *
 */
package com.srlab.line;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;

import com.marge.split.MatchLines;

import uk.ac.shef.wit.simmetrics.similaritymetrics.CosineSimilarity;
import uk.ac.shef.wit.simmetrics.similaritymetrics.DiceSimilarity;
import uk.ac.shef.wit.simmetrics.similaritymetrics.JaccardSimilarity;
import uk.ac.shef.wit.simmetrics.similaritymetrics.Levenshtein;

//import com.srlab.matching.ldiff.Pair;
//import com.srlab.matching.ldiff.Pair;
//import com.srlab.matching.ldiff.PairComparator;
 
/**
 * @author Muhammad Asaduzzaman
 *
 */
public class HDiffSHMatching extends AbstractMatching {


        private boolean enableLSHeuristic;
        private boolean ignoreBlankLines;

        private float lineTH;
        private float structualTH;
        private float contextTH;
        private int contextLength;

        private AbstractMatching lineMtTechnique;
        private AbstractMatching contextMtTechnique;
        
        private HashMap hmMap;
        private HashMap diffMap;
        
        public HDiffSHMatching() {
                // TODO Auto-generated constructor stub
        	this.hmMap   = new HashMap();
        	this.diffMap = new HashMap(); 
        }

        public HDiffSHMatching(String _orgFile, String _revFile) {
                this.init(_orgFile, _revFile);
        }

        public HDiffSHMatching(boolean _ignoreBlankLines, boolean
_enableLSHeuristic, float _lineTH, float _structuralTH,float
_contextTH, int _contextLength,
                        String _orgFile, String _revFile, AbstractMatching lineMtTechnique,
AbstractMatching contextMtTechnique){
                this.init(_orgFile, _revFile);

                this.ignoreBlankLines = _ignoreBlankLines;
                this.enableLSHeuristic = _enableLSHeuristic;
                this.lineTH = _lineTH;
                this.contextTH = _contextTH;
                this.structualTH = _structuralTH;
                this.contextLength = _contextLength;
                this.lineMtTechnique = lineMtTechnique;
                this.contextMtTechnique = contextMtTechnique;
        }

        /* (non-Javadoc)
         * @see com.srlab.matching.AbstractMatching#match()
         */
        @Override
        public void match() {
        	this.diffMap.clear();
                ArrayList<Pair> pairList = new ArrayList<Pair>();

                // TODO Auto-generated method stub
                int leftIndex  = 0; // track the lines in the original, starting from 0
                int rightIndex = 0; // track the lines in the revised , starting from 0

                ArrayList<Hunk> lhunkList = new ArrayList<Hunk>();
                ArrayList<HunkPair> lhunkPairList = new ArrayList<HunkPair>();

                ArrayList<Hunk> rhunkList = new ArrayList<Hunk>();
                ArrayList<HunkPair> rhunkPairList = new ArrayList<HunkPair>();
                ArrayList<Integer> lLineList= new ArrayList<Integer>();
                ArrayList<Integer> rLineList= new ArrayList<Integer>();
                //System.out.println("File= "+this.getOrgFile().getName());

                Diff diff = new
Diff(this.getOrglLines().toArray(),this.getRevLines().toArray());
                Diff.change script = diff.diff_2(false);
                ChangeDescriptor changeDescriptor = new
ChangeDescriptor(script,this.getOrglLines(),this.getRevLines());

                ArrayList<HunkPair> hunkPairs = changeDescriptor.getChanges();

                for(HunkPair hp: hunkPairs){

                        if(hp.getChangeType() == ChangeDescriptor.LINE_ADDED){
//                              System.out.println("Added");
//                              if(hp.getLeftHunk()!=null) hp.getLeftHunk().print();
//                              System.out.println("-----");
//                              if(hp.getRightHunk()!=null) hp.getRightHunk().print();

                                int rightStartLine = hp.getRightHunk().getStartLine();
                                /* Add the top unchanged lines */
                                for (; rightIndex < rightStartLine; leftIndex++, rightIndex++) {
                                        this.add(leftIndex, rightIndex);
                                        this.diffMap.put(leftIndex, rightIndex);
                                        //this.getBwdMapping().put(rightIndex, leftIndex);
                                     //System.out.println("add LeftKey="+leftIndex+"  add value="+rightIndex);
                                }
                                rhunkList.add(hp.getRightHunk());
                                rhunkPairList.add(hp);
                                rightIndex = hp.getRightHunk().getStartLine() +
hp.getRightHunk().getLines().size();
                                ArrayList ad = new ArrayList();
                                for(int i=rightStartLine;i<rightStartLine+hp.getRightHunk().getLines().size();i++){
                                        //String line = this.getRevLines().get(i).trim();
                                        //if(line.matches("\\s+|\\{|\\}|\\(|\\)")==false)
                                        rLineList.add(i);
                                        
                                        ad.add(i);
                                }
                               // System.out.println("Add: "+ad);
                        }

                        else if(hp.getChangeType() == ChangeDescriptor.LINE_DELETED){
//                              System.out.println("Deleted");
//                              if(hp.getLeftHunk()!=null) hp.getLeftHunk().print();
//                              System.out.println("-----");
//                              if(hp.getRightHunk()!=null) hp.getRightHunk().print();

                                int leftStartLine  = hp.getLeftHunk().getStartLine(); //for the left hunk

                                /* Add the top unchanged lines */
                                for (; leftIndex < leftStartLine; leftIndex++, rightIndex++) {
                                        this.add(leftIndex, rightIndex);
                                        this.diffMap.put(leftIndex, rightIndex);
                                        //this.getBwdMapping().put(rightIndex, leftIndex);
                                        //System.out.println("Del LeftKey="+leftIndex+"  Del value="+rightIndex);
                                }
                                lhunkList.add(hp.getLeftHunk());
                                lhunkPairList.add(hp);
                                leftIndex = hp.getLeftHunk().getStartLine() +
hp.getLeftHunk().getLines().size();
                                ArrayList del = new ArrayList();

                                for(int i=leftStartLine;i<leftStartLine+hp.getLeftHunk().getLines().size();i++){
                                        //String line = this.getOrglLines().get(i).trim();
                                        //if(line.matches("\\s+|\\{|\\}|\\(|\\)")==false)
                                        lLineList.add(i);
                                        del.add(i);

                                }
                                //System.out.println("del: "+del);



                        }

                        else if(hp.getChangeType() == ChangeDescriptor.LINE_CHANGED){
                                //System.out.println("Changed");
                                //if(hp.getLeftHunk()!=null) hp.getLeftHunk().print();
                                //System.out.println("-----");
                                //if(hp.getRightHunk()!=null) hp.getRightHunk().print();

                                int leftStartLine  = hp.getLeftHunk().getStartLine() ; //for the left hunk
                                int rightStartLine = hp.getRightHunk().getStartLine(); //for the right hunk

                                /* Add the top unchanged lines */
                                for (; leftIndex < leftStartLine; leftIndex++, rightIndex++) {
                                        this.add(leftIndex, rightIndex);
                                        this.diffMap.put(leftIndex, rightIndex);
                                        //this.getBwdMapping().put(rightIndex, leftIndex);
                                        //System.out.println("changed LeftKey="+leftIndex+"  changedvalue="+rightIndex);
                                }
                                //System.out.println("LeftChangedHunk: ="+hp.getLeftHunk().getLines().size());
                                //System.out.println("RightChangedHunk: ="+hp.getRightHunk().getLines().size());

                                //this.mapChangedHunk(hp, leftIndex, rightIndex, lLineList, rLineList);
                                /*int i=leftIndex;
                                int j=rightIndex;
                                for(;i<leftIndex+hp.getLeftHunk().getLines().size() &&
j<rightIndex+hp.getRightHunk().getLines().size();i++,j++){
                                        this.getFwdMapping().put(i, j);
                                        this.getBwdMapping().put(j,i);
                                }
                                if(hp.getLeftHunk().getLines().size()>hp.getRightHunk().getLines().size()){
                                        for(;i<hp.getLeftHunk().getLines().size();i++){
                                                lLineList.add(i);
                                        }
                                }

                                if(hp.getLeftHunk().getLines().size()<hp.getRightHunk().getLines().size()){
                                        for(;i<hp.getRightHunk().getLines().size();i++){
                                                rLineList.add(i);
                                        }

                                }
                                /*for(int i=leftIndex,j=rightIndex;i<leftIndex+hp.getLeftHunk().getLines().size()
&& j<rightIndex+hp.getRightHunk().getLines().size();i++,j++){
                                        //this.getFwdMapping().put(i, j);
                    //this.getBwdMapping().put(j, i);
                    lLineList.add(i);
                    rLineList.add(j);
                                        System.out.println("LeftKey="+leftIndex+"  value="+rightIndex);
                                }*/


                                for(int i=leftStartLine;i<leftStartLine+hp.getLeftHunk().getLines().size();i++){
                                        //String line = this.getOrglLines().get(i).trim();
                                        //if(line.length()==1 && line.matches("\\s+|\\{|\\}|\\(|\\)")==false)


                                        lLineList.add(i);
                                }
                                for(int i=rightStartLine;i<rightStartLine+hp.getRightHunk().getLines().size();i++){
                                        //String line = this.getRevLines().get(i).trim();
                                        //if(line.matches("\\s+|\\{|\\}|\\(|\\)")==false)


                                        rLineList.add(i);
                                }

                                leftIndex  = leftIndex  + hp.getLeftHunk().getLines().size();
                                rightIndex = rightIndex + hp.getRightHunk().getLines().size();
                        }
                }

                for (; leftIndex < this.getOrglLines().size(); leftIndex++, rightIndex++) {
                	this.diffMap.put(leftIndex, rightIndex);
                        this.add(leftIndex, rightIndex);
                        //this.getBwdMapping().put(rightIndex, leftIndex);
                        //System.out.println("changed LeftKey="+leftIndex+"  changedvalue="+rightIndex);
                }
                for(int i=leftIndex; i< this.getOrglLines().size() ; i++){
                        lLineList.add(i);
                }
                for(int i=rightIndex; i< this.getRevLines().size(); i++){
                        rLineList.add(i);
                }

                /* Now we have only those lines that are added or deleted and we
need to map them*/

                 //System.out.println("L_Line_List  "+lLineList.size());
                 //System.out.println("R_Line_List  "+rLineList.size());
                    ArrayList<Integer> left;
                    ArrayList<Integer> right;

                    //Step-1: generate simhash for eachLine
                    HashMap  hmLeftContext = new HashMap();
                    HashMap  hmRightContext = new HashMap();

                    HashMap  hmLeftContent = new HashMap();
                    HashMap  hmRightContent = new HashMap();
                    
                    HashMap hmTopLeft = new HashMap();
                    HashMap hmBottomLeft = new HashMap();
                    HashMap hmTopRight = new HashMap();
                    HashMap hmBottomRight = new HashMap();
                    
                    
                    /******check whther 723 is present in lLine list*/
                    /*for(Integer l:lLineList){
                    	if(l==723)
                    		System.out.println("++723 is here");
                    	
                    }
                    for(Integer l:rLineList){
                    	if(l==1003)
                    		System.out.println("++1003 is here");
                    	
                    }*/
                    
                    
                    //JenKinsHash jkh = new JenKinsHash();
                    for(int i=0;i<this.getOrglLines().size();i++){
                        //String str1 = this.getTopContext(i, this.getOrglLines());
                        //String str2 = this.getBottomContext(i, this.getOrglLines());
                        String str = this.getContext(i, this.getOrglLines());

                      hmLeftContext.put(i, SimHash.genFasterSimHash(str));
                      hmLeftContent.put(i,SimHash.genFasterSimHash(this.getOrglLines().get(i)));
                      
                      hmTopLeft.put(i, SimHash.genFasterSimHash(this.getTopContext(i,this.getOrglLines())));
                      hmBottomLeft.put(i, SimHash.genFasterSimHash(this.getBottomContext(i,this.getOrglLines())));
                     
                     
                      
                       //hmLeftContext.put(i, SimHash.genSimHash(str));
                       //hmLeftContent.put(i,SimHash.genSimHash(this.getOrglLines().get(i)));
                      
                        //hmLeftContext.put(i, ApacheHash.lookup3ycs64(str,0,str.length(), 0));
                        //hmLeftContent.put(i,ApacheHash.lookup3ycs64(this.getOrglLines().get(i),0,this.getOrglLines().get(i).length(),0));
                        
                        //hmLeftContext.put(i, jkh.hash32(str.getBytes()));
                        //hmLeftContent.put(i,jkh.hash32(this.getOrglLines().get(i).getBytes()));
                        
                        
                        //byte bytes[] = new byte[128];
                        //byte b1[]=SimHash.genSimHash(str1);
                        //byte b2[]=SimHash.genSimHash(str2);

                        //for(int k=0,l=0;k<64;k++,l++){
                        //      bytes[k]=b1[l];
                        //}
                        //for(int k=64,l=0;k<128;k++,l++){
                        //      bytes[k]=b2[l];
                        //}

                        //hmLeftContext.put(i, bytes);
                        //hmLeftContent.put(i, SimHash.genSimHash(this.getOrglLines().get(i)));

                    }

                    for(int i=0;i<this.getRevLines().size();i++){
                        String str = this.getContext(i, this.getRevLines());
                        hmRightContext.put(i, SimHash.genFasterSimHash(str));
                        hmRightContent.put(i,SimHash.genFasterSimHash(this.getRevLines().get(i)));
                        
                        hmTopRight.put(i, SimHash.genFasterSimHash(this.getTopContext(i,this.getRevLines())));
                        hmBottomRight.put(i, SimHash.genFasterSimHash(this.getBottomContext(i,this.getRevLines())));
                        
                        //hmRightContext.put(i, SimHash.genSimHash(str));
                        //hmRightContent.put(i,SimHash.genSimHash(this.getRevLines().get(i)));
                       
                        //hmRightContext.put(i, ApacheHash.lookup3ycs64(str,0,str.length(), 0));
                        //hmRightContent.put(i,ApacheHash.lookup3ycs64(this.getRevLines().get(i),0,this.getRevLines().get(i).length(),0));
                       
                        
                        //hmRightContext.put(i, jkh.hash32(str.getBytes()));
                        //hmRightContent.put(i,jkh.hash32(this.getRevLines().get(i).getBytes()));
                    
                        //  hmRightContext.put(i, SimHash.genSimHash(str));
                      //        hmRightContent.put(i,SimHash.genSimHash(this.getRevLines().get(i)));

                        String str1 = this.getTopContext(i, this.getRevLines());
                        String str2 = this.getBottomContext(i, this.getRevLines());

                        //      hmLeftContext.put(i, SimHash.genFasterSimHash(str));
                   //   hmLeftContent.put(i,SimHash.genFasterSimHash(this.getOrglLines().get(i)));
                        //byte bytes[] = new byte[128];
                        //byte b1[]=SimHash.genSimHash(str1);
                        //byte b2[]=SimHash.genSimHash(str2);

                        //for(int k=0,l=0;k<64;k++,l++){
                        //      bytes[k]=b1[l];
                        //}
                        //for(int k=64,l=0;k<128;k++,l++){
                        //      bytes[k]=b2[l];
                        //}

                        //hmRightContext.put(i, bytes);
                        //hmRightContent.put(i, SimHash.genSimHash(this.getRevLines().get(i)));

                    }
                    //STep:2 compute similarity


                   left = new ArrayList<Integer>();
                   right = new ArrayList<Integer>();

                    HashMap hm = new HashMap();

                    ArrayList<Pair> fpairList = new ArrayList<Pair>();

                    for(int i=0;i<lLineList.size();i++){
                    	
                        int lIndex = lLineList.get(i);
                        //System.out.println("File = "+this.getOrgFile().getName()+" List = "+lLineList);
                        //System.out.println("File = "+this.getOrgFile().getName()+" List = "+rLineList);
                        
                        //float bestScore =  0.40f;
                        float bestScore =-1;
                        int bestIndex = -1;
                        ArrayList possibleList = new ArrayList();
                        
                        for(int j=0;j<rLineList.size();j++){
                                        int rIndex = rLineList.get(j);
                                        if(hm.containsKey(rIndex)){
                                             continue;
                                        }
                                        
                                //if(this.getBwdMapping().containsKey(rIndex)) continue;

                                        //int score1 = SimHash.hamming((byte[])hmLeftContext.get(lIndex),((byte[])hmRightContext.get(rIndex)));//this.calcMatchingScore1(lIndex,rIndex);
                                        //int score2 =SimHash.hamming((byte[])hmLeftContent.get(lIndex),((byte[])hmRightContent.get(rIndex)));//this.calcMatchingScore1(lIndex,rIndex);

                                        
                                        //int score1 = SimHash.hamming((Long)hmLeftContext.get(lIndex),((Long)hmRightContext.get(rIndex)));//this.calcMatchingScore1(lIndex,rIndex);
                                        //int score2 =SimHash.hamming((Long)hmLeftContent.get(lIndex),((Long)hmRightContent.get(rIndex)));//this.calcMatchingScore1(lIndex,rIndex);

                                        
                                        //int score1_1 = SimHash.hamming((Long)hmTopLeft.get(lIndex),((Long)hmTopRight.get(rIndex)));//this.calcMatchingScore1(lIndex,rIndex);
                                        //int score1_2 = SimHash.hamming((Long)hmBottomLeft.get(lIndex),((Long)hmBottomRight.get(rIndex)));//this.calcMatchingScore1(lIndex,rIndex);
                                        
                                        int score1 = SimHash.hamming((Long)hmLeftContext.get(lIndex),((Long)hmRightContext.get(rIndex)));//this.calcMatchingScore1(lIndex,rIndex);
                                        int score2 =SimHash.hamming((Long)hmLeftContent.get(lIndex),((Long)hmRightContent.get(rIndex)));//this.calcMatchingScore1(lIndex,rIndex);

                                        //int score1 = SimHash.hamming32((byte[])hmLeftContext.get(lIndex),((byte[])hmRightContext.get(rIndex)));//this.calcMatchingScore1(lIndex,rIndex);
                                        //int score2 =SimHash.hamming32((byte[])hmLeftContent.get(lIndex),((byte[])hmRightContent.get(rIndex)));//this.calcMatchingScore1(lIndex,rIndex);

                                        //int score1 = SimHash.hamming32((Integer)hmLeftContext.get(lIndex),((Integer)hmRightContext.get(rIndex)));//this.calcMatchingScore1(lIndex,rIndex);
                                        //int score2 =SimHash.hamming32((Integer)hmLeftContent.get(lIndex),((Integer)hmRightContent.get(rIndex)));//this.calcMatchingScore1(lIndex,rIndex);

                                        //System.out.println("Score2 = "+score2);
                                        //float score = (0.40f*score1+ 0.60f*score2);
                                        //float score = score1 + score2;
              
                                        float score = 0.40f*(score1/32.0f)+0.60f*(score2/32.0f);
                                        //System.out.println("LIndex= "+lIndex+"rIndex= "+rIndex+"Score1 = "+score1+"  Score2= "+score2+"  Score= "+score);
                                        
                                        //System.out.println("Score = "+score);
                                        if(lIndex==723){
                                                //System.out.println("LINDEX400="+rIndex+"  bestIndex="+bestIndex+ " score="+score +"   bestScore = "+bestScore);
                                                //System.out.println("lIndex="+lIndex+"   rIndex = "+rIndex +"Score = "+score+" [score1= "+score1+"  score2= "+score2+"]"+"  score1_1"+score1_1+"  score1_2="+score1_2);
                                                
                                        }
                                        
                                        Pair pair = new Pair(lIndex,rIndex,score);
                                        possibleList.add(pair);
                                        
                                        //if(score>=bestScore ){
                                        //        bestScore = score;
                                        //        bestIndex = rIndex;
                                                //possibleList.add(rIndex);
                                                //System.out.println("Find the bestIndex for= "+lIndex+" which is="+bestIndex);
                                        //}

                        }
                        if(possibleList.size()>0)//if(bestIndex!=-1)
                        {
                        	Collections.sort(possibleList,new SHPairComparator());
                        	//System.out.println("LeftInde = "+lIndex+"   posible RightIndexes: "+possibleList.size()+"   ");
                             /*??for(int l=0;l<possibleList.size();l++){
                              System.out.println("  "+((Pair)possibleList.get(l)).getRevisedIndex()+ " "+"value= "+((Pair)possibleList.get(l)).value+",");
                             }*/
                        	//          pairList.add(new Pair(lIndex, bestIndex,bestScore));

                             //  hm.put(bestIndex, bestIndex);
                                left.add(lIndex);
                                right.add(bestIndex);
                               	//float bs=0.45f;
                               	
                               	float bs=0.45f;
                               	
                                boolean mappingFound1=false;
                                for(int k=0, m=0;k<possibleList.size()&& m<10;k++,m++){
                                	Pair p = (Pair)possibleList.get(k);
                                	int index = (Integer)p.getRevisedIndex();
                                	float score = this.calcMatchingScore1(lIndex, index);
                                 	//??System.out.println("LeftIndex = "+lIndex+"   Right Index= "+index+"   score= "+score);
                            		/*??if(lIndex==723){
                            			System.out.println("**$$723 Score= "+score+"  "+this.revFile.getAbsolutePath());
                            		}*/
                                	if(score>=bs){
                                		bestIndex = index;
                                		bs = score;
                                	}
                                	if(score>=0.45f){
                                		fpairList.add(new Pair(lIndex,index, score));
                						mappingFound1 = true;
                	 			
                                	}
                                }
                                /*if(bestIndex!=-1){
                                	System.out.println("Match found   LeftIndex = "+lIndex+"   Right Index= "+bestIndex);
                                    
                                	hm.put(bestIndex, bestIndex);
                                	 this.getFwdMapping().put(lIndex, bestIndex);
                                    this.getBwdMapping().put(bestIndex, lIndex);
                               	
                                            	
                                }*/
                                if(mappingFound1!=true){
                		    		this.add(lIndex, -1);
                		    	}
                		    
                                else{this.add(lIndex, bestIndex);
                                //this.hmMap.put(bestIndex, bestIndex);
                                //??System.out.println("Final l = "+lIndex+"  r= "+bestIndex);
                                //	 this.getFwdMapping().put(lIndex, -1);
                                }
                               //this.getFwdMapping().put(lIndex, bestIndex);
                                //this.getBwdMapping().put(bestIndex, lIndex);
                                //System.out.println("Later Mapping LeftKey="+lIndex+"value="+bestIndex);
                        	
                        }
                        else{
                                //left.add(lIndex);
                         	//System.out.println("LeftIndex = "+lIndex+"   posible RightIndexes: "+possibleList.size()+"   "+possibleList);
                        
                                this.add(lIndex, -1);
                        }
                    }

                   // System.out.println("PrevLeft = "+lLineList.size());
                   // System.out.println("PrevRight = "+rLineList.size());

                    Collection c1 = CollectionUtils.disjunction(lLineList, left);
                    Collection c2 = CollectionUtils.disjunction(rLineList, right);

                    lLineList = new ArrayList<Integer>(c1);
                    rLineList = new ArrayList<Integer>(c2);

                    Collections.sort(fpairList, new PairComparator()); //sort bydescending order
                        HashMap<Integer,Integer> hmAlreadyMappedLeft = new HashMap<Integer,Integer>();
                        HashMap<Integer,Integer> hmAlreadyMappedRight = new HashMap<Integer,Integer>();

                        for (int i = 0; i < fpairList.size(); i++) {
                                Pair value = fpairList.get(i);
                                if (hmAlreadyMappedLeft.containsKey(value.getOriginalIndex())
                                                || hmAlreadyMappedRight.containsKey(value.getRevisedIndex())) {
                                        continue;
                                } else
                                {

                                        hmAlreadyMappedLeft.put(value.getOriginalIndex(),
                                                        value.getRevisedIndex());
                                        hmAlreadyMappedRight.put(value.getRevisedIndex(),
                                                        value.getOriginalIndex());

                                        //System.out.println("??Mapping in line map");
                                        //System.out.println(""+(value.getOriginalIndex()));
                                        //System.out.println(""+(value.getRevisedIndex()));

                                        this.add(value.getOriginalIndex(),
                                           value.getRevisedIndex());//this.getBwdMapping().put(value.getRevisedIndex(),left.getStartLine()+ value.getOriginalIndex());

                                }
                        }
                 //+++++++++++++++++++++++++++++++++++++++++++++++++++++++
                                   /* left = new ArrayList<Integer>();
                                           right = new ArrayList<Integer>();


                                            for(int i=0;i<lLineList.size();i++){
                                                int lIndex = lLineList.get(i);
                                                float bestScore =  0.50f;
                                                int bestIndex = -1;
                                                for(int j=0;j<rLineList.size();j++){
                                                                int rIndex = rLineList.get(j);
                                                                if(hm.containsKey(rIndex)){
                                                                //      continue;
                                                                }

                                                        //if(this.getBwdMapping().containsKey(rIndex)) continue;
                                                                float score = this.calcMatchingScore1(lIndex,rIndex);
                                                                if(lIndex==400){
                                                                        //System.out.println("LINDEX400="+rIndex+"  bestIndex=
"+bestIndex+ " score="+score +"   bestScore = "+bestScore);
                                                                }
                                                                if(score>bestScore ){
                                                                        bestScore = score;
                                                                        bestIndex = rIndex;
                                                                }

                                                }
                                                if(bestIndex!=-1){
                                                        hm.put(bestIndex, bestIndex);
                                                        left.add(lIndex);
                                                        right.add(bestIndex);
                                                        this.getFwdMapping().put(lIndex, bestIndex);
                                                        this.getBwdMapping().put(bestIndex, lIndex);
                                                        //System.out.println("Later Mapping LeftKey="+lIndex+"
value="+bestIndex);
                                                }
                                                else{
                                                        //left.add(lIndex);
                                                        //this.getFwdMapping().put(lIndex, -1);
                                                }
                                            }

                                           // System.out.println("PrevLeft = "+lLineList.size());
                                           // System.out.println("PrevRight = "+rLineList.size());

                                           c1 = CollectionUtils.disjunction(lLineList, left);
                                            c2 = CollectionUtils.disjunction(rLineList, right);

                                            lLineList = new ArrayList<Integer>(c1);
                                            rLineList = new ArrayList<Integer>(c2);
                    //System.out.println("Left = "+lLineList.size());
                    //System.out.println("Right = "+rLineList.size());

                        hm = new HashMap();

                    for(int i=0;i<lLineList.size();i++){
                        int lIndex = lLineList.get(i);
                        float bestScore =  0.40f;
                        int bestIndex = -1;
                        for(int j=0;j<rLineList.size();j++){
                                        int rIndex = rLineList.get(j);
                                        if(hm.containsKey(rIndex)){
                                                continue;
                                        }

                                //if(this.getBwdMapping().containsKey(rIndex)) continue;
                                        float score = this.calcMatchingScore(lIndex,rIndex);
                                        if(lIndex==400){
                                                //System.out.println("LINDEX400="+rIndex+"  bestIndex=
"+bestIndex+ " score="+score +"   bestScore = "+bestScore);
                                        }
                                        if(score>bestScore ){
                                                bestScore = score;
                                                bestIndex = rIndex;
                                        }

                        }
                        if(bestIndex!=-1){
                                hm.put(bestIndex, bestIndex);
                                this.getFwdMapping().put(lIndex, bestIndex);
                                this.getBwdMapping().put(bestIndex, lIndex);
                                //System.out.println("Later Mapping LeftKey="+lIndex+"
value="+bestIndex);
                        }
                        else{
                                this.getFwdMapping().put(lIndex, -1);
                        }
                    }*/
                        
                        //System.out.println("File: = "+this.getRevFile().getAbsolutePath());
                        
                        //add code to remove isolated lines
                        //??System.out.println("****PRINt NEIGHBORS****");
                        this.printNeighbors();
                        //??System.out.println("****PRINt split****");
                        
                        int size=5;
                        
                        /*add code to detect splitted lines
                         * We consider that line has been merged
                         * 
                         */
                        /*for(int i=1;i<this.getOrglLines().size();i++){
                        	if(this.getOrglLines().get(i).matches("\\s+")) continue;
                             HashMap hm1 =this.getFwdMapping().get(i);
                             System.out.println("Line="+i+"hm1="+hm1.keySet());
                             if(hm1.keySet().size()==1 && hm1.containsKey(-1)){
                            	 //this line is deleted
                            	 //find the previous mapped line
                            	 for(int m=1;m<this.getRevLines().size();m++)
                            	 {
                            		 //hm1=this.getFwdMapping().get(i-1);
                            		 //System.out.println("   Line="+(i-1)+"hm1="+hm1.keySet());
                                     
                            		 int rline=m;//(Integer)hm1.values().toArray()[0];
                            		 int lline=i;
                            		 String str="";
                            		 float oldResult=-1;
                            		 int counter=0;
                            		 float sim =0;
                            		 int match =1;
                            		 for(int j=0;j<8 && (i+j)<this.getOrglLines().size() && (rline+1)<this.getRevLines().size();j++){
                            			 if(match==2)
                            		     {counter--;break;} 
                            			 if(this.isdeleted(i+j)==false) 
                            			 {
                            				 match++;
                            			 }
                            			 //if(this.isdeleted(i+j)==false){counter--;break;}
                            			
                            			 str=str+this.getOrglLines().get(i+j);
                            			 float n=new Levenshtein().getSimilarity(str, this.getRevLines().get(rline));
                            			 System.out.println("RightLine="+rline+"Left="+str+"]\n[right="+this.getRevLines().get(rline)+"  sim="+n);
                            			 if(oldResult<=n) {oldResult=n;sim=n;counter++;}
                            			 else {counter--;break;}
                            		 }
                            		 System.out.println("i= "+i+ "counter="+counter);
                            	 
                            		 if(counter>0 && sim>0.85f){
                            		 
                            			 //add the mapping
                            			 for(int l=0;l<=counter;l++){
                            				 if((i+l)<this.getOrglLines().size() &&this.getOrglLines().get(i+l).matches("\\s+")) continue;
                            				 HashMap hmNew = this.getFwdMapping().get(i);
                                  			//System.out.println("__"+this.getOrglLines().get(i+l));
                            				 hmNew.clear();
                                     	
                                  			hmNew.put(rline, rline);
                                  			this.getFwdMapping().put(i+l, hmNew);
                            			 }
                            		
                            			 i=i+counter;
                            			 //System.out.println("change i= "+ i+" line"+this.getOrglLines().get(i));
                                    	 
                            		 }
                            		 
                            		 //else i++;
                            		 
                            	 }
                             }
                        }
                        */
                        /**
                         * Now add code line has been splitted
                         */
                        //****************************************************************************************
                        int previousMappedKey=-1;
                        int previousMappedValue=-1;


                        for(int i=1;i<this.getOrglLines().size();i++){

                            HashMap hm1 =this.getFwdMapping().get(i);
                            //System.out.println("Line="+i+"hm1="+hm1.keySet());
                            int key = i;
                            Set s  = hm1.keySet();
                            Iterator iterator = hm1.keySet().iterator();
                            int value = (Integer)hm1.get(iterator.next());

                       	   

                           if(key!=-1 && value!=-1){

                           previousMappedKey =i;

                           previousMappedValue =value;

                           }

                          

                            if(hm1.keySet().size()==1 && hm1.containsKey(-1))

                            {

                           	//this line is deleted

                           	//find the previous mapped line

                            	

                            if(previousMappedKey==-1){

                           	previousMappedValue =1;

                           	}

                            	

                           	for(int m=previousMappedValue,k=0;k<20 && m<this.getRevLines().size();m++,k++)

                           	{

                           	//hm1=this.getFwdMapping().get(i-1);

                           	//System.out.println("   Line="+(i-1)+"hm1="+hm1.keySet());

                                    boolean found = false;

                           	int rline=m;//(Integer)hm1.values().toArray()[0];

                           	int lline=i;

                           	String str="";

                           	float oldResult=-1;

                           	int counter=0;

                           	float sim =0;

                           	int match =1;

                           	for(int j=0;j<8 && (rline+1)<this.getRevLines().size() && (i+j)<this.getOrglLines().size();j++){

                           	//if(match==2)

                           	    //{counter--;break;} 

                           	//if(this.isdeleted(m+j)==false) 

                           	//{

                           //	match++;

                           	//}

                           	

                           	str=str+this.getOrglLines().get(i+j);

                           	float n1=new Levenshtein().getSimilarity(str,this.getRevLines().get(rline));

                            

                           	float n2 = new Levenshtein().getSimilarity(this.getOrglLines().get(i+j),this.getRevLines().get(rline));

                           	if(n2>n1) {counter=1;break;}

                           	//System.out.println("LeftLine = "+lline+"  Left="+this.getOrglLines().get(lline)+"]   [right= "+str+"  sim="+n1);

                           	if(oldResult<n1) {oldResult=n1;sim=n1;counter++;}

                           	else {break;}

                           	}

                           	//System.out.println("i= "+i+ "counter="+counter);

                            

                           	if(counter>1 && sim>0.85f){

                            

                           	//add the mapping

                           	HashMap hmN=new HashMap();

                           	hmN.clear();

                           	for(int l=0;l<counter;l++){

                           	if((i+l)<this.getOrglLines().size() &&this.getOrglLines().get(i+l).matches("\\s*")) continue;

                           	{//hmN.put(i+l,m+l);

                           	HashMap hashm = new HashMap();

                           	hashm.put(rline, rline);

                           	this.getFwdMapping().put(i+l,hashm); }

                                 	

                           	}

                            

                           	    found = true;

                           	i = i+counter-1;

                           	//i=i+counter;

                           	//System.out.println("change i= "+ i+" line"+this.getOrglLines().get(i));

                                    

                           	}

                            

                           	//else i++;

                           	if(found==true)

                           	break;

                           	}

                            }

                       } /*for(int i=1;i<this.getOrglLines().size();i++){
                        
                        
                        
                        
                        /*add code to detect splitted lines
                         * We consider that line has been splittedd
                         * 
                         */
                       
                        /*for(int i=1;i<this.getOrglLines().size();i++){
                             HashMap hm1 =this.getFwdMapping().get(i);
                             //System.out.println("Line="+i+"hm1="+hm1.keySet());
                             if(hm1.keySet().size()==1 && hm1.containsKey(-1))
                             {
                            	 //this line is deleted
                            	 //find the previous mapped line
                            	 for(int m=1;m<this.getRevLines().size();m++)
                            	 {
                            		 //hm1=this.getFwdMapping().get(i-1);
                            		 //System.out.println("   Line="+(i-1)+"hm1="+hm1.keySet());
                                     boolean found = false;
                            		 int rline=m;//(Integer)hm1.values().toArray()[0];
                            		 int lline=i;
                            		 String str="";
                            		 float oldResult=-1;
                            		 int counter=0;
                            		 float sim =0;
                            		 int match =1;
                            		 for(int j=0;j<8 && (m+j)<this.getRevLines().size() && (lline+1)<this.getOrglLines().size();j++){
                            			 //if(match==2)
                            		     //{counter--;break;} 
                            			 //if(this.isdeleted(m+j)==false) 
                            			 //{
                            			//	 match++;
                            			 //}
                            			
                            			 str=str+this.getRevLines().get(m+j);
                            			 float n1=new Levenshtein().getSimilarity(this.getOrglLines().get(lline),str);
                            			 
                            			 float n2 = new Levenshtein().getSimilarity(this.getOrglLines().get(lline),this.getRevLines().get(m+j));
                            			 if(n2>n1) {counter=1;break;}
                            			 //System.out.println("LeftLine = "+lline+"  Left="+this.getOrglLines().get(lline)+"]   [right= "+str+"  sim="+n1);
                            			 if(oldResult<n1) {oldResult=n1;sim=n1;counter++;}
                            			 else {break;}
                            		 }
                            		 //System.out.println("i= "+i+ "counter="+counter);
                            	 
                            		 if(counter>1 && sim>0.85f){
                            		 
                            			 //add the mapping
                            			 HashMap hmN=new HashMap();
                            			 hmN.clear();
                            			 for(int l=0;l<counter;l++){
                            				 if((m+l)<this.getRevLines().size() &&this.getRevLines().get(m+l).matches("\\s*")) continue;
                            				 hmN.put(m+l,m+l);
                                  			
                            			 }
                            			 this.getFwdMapping().put(i, hmN);
                            		     found = true;
                            			 m = m+counter;
                            			 //i=i+counter;
                            			 //System.out.println("change i= "+ i+" line"+this.getOrglLines().get(i));
                                    	 
                            		 }
                            		 
                            		 //else i++;
                            		 if(found==true)
                            			 break;
                            	 }
                             }
                        }*/
                        //*******************************************************************************
                        
        }
        
        public boolean isdeleted(int l){
        	HashMap hm=this.getFwdMapping().get(l);
        	if(hm.keySet().size()==1 && hm.containsKey(-1))
        		return true;
        	else return false;
        }

        /*protected void mapChangedHunk(HunkPair hp, int leftStartLine,int
rightStartLine, ArrayList<Integer> lLineList, ArrayList<Integer>
rLineList){
                //map this two changed hunks
                int hunkDiffTh = 5;
                Hunk leftHunk  = hp.getLeftHunk();
                Hunk rightHunk = hp.getRightHunk();
                //hp.getLeftHunk().print();
                //hp.getRightHunk().print();
                System.out.println("??Left Hunk:"+hp.getLeftHunk().getLines().size()+"  RightHUnk:"+hp.getRightHunk().getLines().size());
                if(Math.abs(leftHunk.getLines().size()-rightHunk.getLines().size())>hunkDiffTh){

                for(int i=leftStartLine;i<leftStartLine+hp.getLeftHunk().getLines().size();i++){

                        lLineList.add(i);
                }
                for(int i=rightStartLine;i<rightStartLine+hp.getRightHunk().getLines().size();i++){

                        rLineList.add(i);
                }

                return;
                }

                if(Math.abs(leftHunk.getLines().size()-rightHunk.getLines().size())>hunkDiffTh){
                        return;
                }
                ArrayList<Integer> leftMapped = new ArrayList<Integer>();
                ArrayList<Integer> rightMapped = new ArrayList<Integer>();

                int contextSize =2;
                ArrayList<Integer> leftList  = new ArrayList<Integer>();
                ArrayList<Integer> rightList = new ArrayList<Integer>();
                for(int i=rightStartLine;i<rightHunk.getEndline();i++){
                        rightList.add(i);
                }
                for(int i=leftStartLine;i<leftHunk.getEndline();i++){
                        leftList.add(i);
                        float bestScore =  0.8f;
                int bestIndex = -1;
                int lIndex = i;
                        for(int j=rightHunk.getStartLine();j<rightHunk.getEndline();j++){
                        int rIndex = j;
                        //if(this.getBwdMapping().containsKey(rIndex)) continue;
                                float score = this.calcMatchingScore(lIndex,rIndex);
                                if(score>bestScore ){
                                        bestScore = score;
                                        bestIndex = rIndex;
                                }

                }

                if(bestIndex!=-1){
                        this.add(lIndex, bestIndex);
                        //this.getBwdMapping().put(bestIndex, lIndex);
                        leftMapped.add(lIndex);
                        rightMapped.add(bestIndex);
                        System.out.println("Later Mapping FunctionKey="+lIndex+"value="+bestIndex);
                }
                else{
                        //this.getFwdMapping().put(lIndex, -1);
                        System.out.println("Still Not Mapped");

                }
                }
                System.out.println("LeftList ="+leftList);

                Collection c1 = CollectionUtils.disjunction(leftList,leftMapped );
                Collection c2 = CollectionUtils.disjunction(rightList,rightMapped );
                lLineList.addAll(c1);
                rLineList.addAll(c2);
        }*/


        public String getContext(int lineNum, ArrayList<String> lines){
                StringBuilder context = new StringBuilder();
                if(lineNum>0){
                        for(int i=lineNum-1,j=0;i>=0 &&j<W_BEST_LINE_MATCHING.CONTEXT_LENGTH;i--,j++){
                                if(lines.get(i).matches("\\s+")==false &&lines.get(i).trim().matches("\\{ | \\( | \\)| \\}")==false){
                                context.append(lines.get(i));
                                j++;
                                }
                        }
                }
                if((lineNum+1)<lines.size()){
                        for(int i=lineNum+1,j=0;i<lines.size() &&j<W_BEST_LINE_MATCHING.CONTEXT_LENGTH;i++,j++){
                                if(lines.get(i).matches("\\s+")==false&&lines.get(i).trim().matches("\\{ | \\( | \\)| \\}")==false){
                                context.append(lines.get(i));
                                j++;
                                }

                                //context.append("\n"+lines.get(i));
                        }
                }
                return context.toString();
        }

        public String getTopContext(int lineNum, ArrayList<String> lines){
                StringBuilder context = new StringBuilder(" ");
                if(lineNum>0){
                        for(int i=lineNum-1,j=0;i>=0 &&j<W_BEST_LINE_MATCHING.CONTEXT_LENGTH;i--,j++){
                                if(lines.get(i).matches("\\s+")==false){
                                context.append(lines.get(i));
                                j++;
                                }
                        }
                }

                return context.toString();
        }

        public String getBottomContext(int lineNum, ArrayList<String> lines){
                StringBuilder context = new StringBuilder(" ");

                if((lineNum+1)<lines.size()){
                        for(int i=lineNum+1,j=0;i<lines.size() &&j<W_BEST_LINE_MATCHING.CONTEXT_LENGTH;i++,j++){
                                if(lines.get(i).matches("\\s+")==false){
                                context.append(lines.get(i));
                                j++;
                                }

                                //context.append("\n"+lines.get(i));
                        }
                }
                return context.toString();
        }

        /*Copied from W-Best_Line_Matching*/
     /* float calcMatchingScore(int orgLineNum, int revLineNum){

                String leftContent  = this.getOrglLines().get(orgLineNum);
                String rightContent = this.getRevLines().get(revLineNum);
                StringBuilder leftContext = new StringBuilder();
                StringBuilder rightContext= new StringBuilder();

                if(orgLineNum>0){
                        for(int i=orgLineNum-1,j=0;i>=0 &&j<=W_BEST_LINE_MATCHING.CONTEXT_LENGTH;i--,j++){
                                leftContext.append("\n"+this.getOrglLines().get(i));
                        }
                }

                if((orgLineNum+1)<this.getOrglLines().size()){
                        for(int i=orgLineNum+1,j=0;i<this.getOrglLines().size() &&j<=W_BEST_LINE_MATCHING.CONTEXT_LENGTH;i++,j++){
                                leftContext.append("\n"+this.getOrglLines().get(i));
                        }
                }

                if(revLineNum>0){
                        for(int i=revLineNum-1,j=0;i>=0 &&j<=W_BEST_LINE_MATCHING.CONTEXT_LENGTH;i--,j++){
                                rightContext.append("\n"+this.getRevLines().get(i));
                        }
                }
                if(revLineNum<this.getRevLines().size()-1){
                        for(int i=revLineNum+1,j=0;i<this.getRevLines().size() &&j<=W_BEST_LINE_MATCHING.CONTEXT_LENGTH;i++,j++){
                                rightContext.append("\n"+this.getRevLines().get(i));
                        }
                }

                //float contextSimilarity = newCosineSimilarity().getSimilarity(leftContext.toString(),rightContext.toString());
                float contextSimilarity = new JaccardSimilarity(new ShingleTokenizer(4,true)).getSimilarity(leftContext.toString(),rightContext.toString());
                //float contentSimilarity = new Levenshtein().getSimilarity(leftContent,rightContent);
                //float contentSimilarity = new  DiceSimilarity(new JavaTokenizer(true)).getSimilarity(leftContent,rightContent);
                float contentSimilarity = new JaccardSimilarity(new ShingleTokenizer(4,true)).getSimilarity(leftContent.toString(),rightContent.toString());

                if(orgLineNum==400){
                //System.out.println("[LeftContent "+leftContent);
                //System.out.println("RightContent "+rightContent);
                //System.out.println("LeftContext "+leftContext);
                //System.out.println("RightContext] "+rightContext);
                //System.out.println("Content similarity = "+contentSimilarity+ "contextSimilarity = "+contextSimilarity);
                }
                //System.out.println("[LeftContent ["+orgLineNum+"]"+leftContent);
                //System.out.println("RightContent ["+revLineNum+"]"+rightContent);
                //System.out.println("Content similarity = "+contentSimilarity+ "contextSimilarity = "+contextSimilarity);

                
                //return 0.6f*contentSimilarity+0.4f*contextSimilarity;
                return 1;
                //if(contextSimilarity>=0.6)
                //      return 0.6f*contentSimilarity+0.4f*contextSimilarity;
                //else return contentSimilarity;
        }*/

        /*Copied from W-Best_Line_Matching*/
        protected float calcMatchingScore1(int orgLineNum, int revLineNum){

                String leftContent  = this.getOrglLines().get(orgLineNum);
                String rightContent = this.getRevLines().get(revLineNum);
                StringBuilder leftContext = new StringBuilder();
                StringBuilder rightContext= new StringBuilder();

                if(orgLineNum>0){
                        for(int i=orgLineNum-1,j=0;i>=0 &&
j<W_BEST_LINE_MATCHING.CONTEXT_LENGTH;i--){
                                if(this.getOrglLines().get(i).trim().matches("\\s+")==false){
                                leftContext.append(this.getOrglLines().get(i));
                                j++;
                                }
                        }
                }

                if((orgLineNum+1)<this.getOrglLines().size()){
                        for(int i=orgLineNum+1,j=0;i<this.getOrglLines().size() &&j<W_BEST_LINE_MATCHING.CONTEXT_LENGTH;i++){
                                if(this.getOrglLines().get(i).trim().matches("\\s+")==false)
                                leftContext.append(this.getOrglLines().get(i));
                                j++;
                        }
                }

                if(revLineNum>0){
                        for(int i=revLineNum-1,j=0;i>=0 &&j<W_BEST_LINE_MATCHING.CONTEXT_LENGTH;i--){
                                if(this.getRevLines().get(i).trim().matches("\\s+")==false){

                                rightContext.append(this.getRevLines().get(i));
                                j++;
                                }
                        }
                }
                if(revLineNum<this.getRevLines().size()-1){
                        for(int i=revLineNum+1,j=0;i<this.getRevLines().size() &&j<W_BEST_LINE_MATCHING.CONTEXT_LENGTH;i++){
                                if(this.getRevLines().get(i).trim().matches("\\s+")==false){
                                rightContext.append(this.getRevLines().get(i));
                                j++;
                                }
                        }
                }

               
              float contextSimilarity = new CosineSimilarity().getSimilarity(leftContext.toString(),rightContext.toString());
              //float contextSimilarity = new Levenshtein().getSimilarity(leftContext.toString(),rightContext.toString());
               // float contextSimilarity = new JaccardSimilarity().getSimilarity(leftContext.toString(), rightContext.toString());
                //float contextSimilarity = new JaccardSimilarity(new JavaTokenizer(true)).getSimilarity(leftContext.toString(),rightContext.toString());
               float contentSimilarity = new Levenshtein().getSimilarity(leftContent,rightContent);
                //float contentSimilarity = new  DiceSimilarity(new JavaTokenizer(true)).getSimilarity(leftContent,rightContent);

                if(orgLineNum==400){
                //System.out.println("[LeftContent "+leftContent);
                //System.out.println("RightContent "+rightContent);
                //System.out.println("LeftContext "+leftContext);
                //System.out.println("RightContext] "+rightContext);
                //System.out.println("Content similarity = "+contentSimilarity+ "contextSimilarity = "+contextSimilarity);
                }
                //System.out.println("[LeftContent ["+orgLineNum+"]"+leftContent);
                //System.out.println("RightContent ["+revLineNum+"]"+rightContent);
                //System.out.println("Content similarity = "+contentSimilarity+ "contextSimilarity = "+contextSimilarity);

                //return 0.6f*contentSimilarity+0.4f*contextSimilarity; //original
                if(contentSimilarity>0.50f){
                	return 0.6f*contentSimilarity+0.4f*contextSimilarity;
                }
                else return 0;
                //if(contextSimilarity>=0.6)
                //      return 0.6f*contentSimilarity+0.4f*contextSimilarity;
                //else return contentSimilarity;
        }
        
        public int neighborCalculator(int lLine){
        	HashMap hm = this.getFwdMapping().get(lLine);
        	int rLine=0;
        	if(this.getFwdMapping().containsKey(lLine)==false){
        		return 0;
        	}
        	else{
        		Iterator iterator = hm.keySet().iterator();
        		rLine = (Integer)iterator.next();
        	}
        	ArrayList<Integer> lneighbors = new ArrayList();
        	ArrayList<Integer> rneighbors = new ArrayList();
        	for(int i=1;i<=5;i++){
        		if(this.getFwdMapping().containsKey(lLine+i)){
        			HashMap hm1=this.getFwdMapping().get(lLine+i);
        			if(hm1!=null && hm1.containsKey(-1)){
        				
        			}
        			else{
        				Iterator it1=hm1.keySet().iterator();
        				while(it1.hasNext()){
        				lneighbors.add(((Integer)it1.next()));
        				}
        			}
        		}
        		if(this.getFwdMapping().containsKey(lLine-i)){
        			HashMap hm2= this.getFwdMapping().get(lLine-i);
if(hm2!=null &&hm2.containsKey(-1)){
        				
        			}
        			else{
        				Iterator it1=hm2.keySet().iterator();
        				while(it1.hasNext()){
        				lneighbors.add(((Integer)it1.next()));
        				}
        			}
        		}
        	}
        	for(int i=1;i<=5;i++){
        		//if(this.getFwdMapping().containsKey(rLine+i))
        		if((rLine+i)<this.getRevLines().size())
        		{
        			//if()
        			rneighbors.add(rLine+i);
        		}
        		if((rLine-i)<this.getRevLines().size()){
        			rneighbors.add(rLine-i);
        		}
        	}
        	
        	return CollectionUtils.intersection(lneighbors,rneighbors).size();
        	
        }
        public void printNeighbors(){
        	/*??for(int i=1;i<this.getOrglLines().size();i++){
        		if(this.diffMap.containsKey(i)==false)
        		System.out.println("["+i+"] ->"+this.neighborCalculator(i));
        		
        	}*/
        	//??System.out.println("&&&&&&&&&&&&&&&&&&&");
        	for(int i=1;i<this.getOrglLines().size();i++){
        		if(this.diffMap.containsKey(i))
        			continue;
        		int calc = this.neighborCalculator(i);
        		if(calc ==0){
        			HashMap hm = this.getFwdMapping().get(i);
        			HashMap hm1 = new HashMap();
        			hm1.put(-1,-1);
        			this.getFwdMapping().put(i,hm1);
        		}
        		//System.out.println("["+i+"] ->"+this.neighborCalculator(i));
        	}
        }
        
        /**
         * @param args
         */
        
        public ArrayList<MatchLines> calculate ( String fbody , String sbody ) {
        	
        	 HDiffSHMatching hdm = new HDiffSHMatching();
        	 hdm.init(fbody,sbody);
        	 hdm.match();
        	// hdm.print();
        	 //hdm.printLine();
        	// hdm.printLine();
        	 
        	 
        	 return hdm.matchLines();
        }
        
        public static void main(String[] args) {
                // TODO Auto-generated method stub
                String testdir = IConstant.BASE_DIR+System.getProperty("file.separator")+"Data/eclipseTest/";
        	//String testdir ="/Users/parvez/sdiff-read-only/SDiff/files/limbodata/";
        	//String testdir ="/Users/parvez/sdiff-read-only/SDiff/files/test-data/";
               
        	//"/Users/parvez/Documents/powerOld.txt","/Users/parvez/Documents/powerNew.txt"
                HDiffSHMatching hdm = new HDiffSHMatching();
                //hdm.init(testdir+"DoubleCache_1.java", testdir+"DoubleCache_2.java");
                //hdm.init(testdir+"Example_3.java", testdir+"Example_4.java");
                //hdm.init(testdir+"asdf_1.java", testdir+"asdf_2.java");
                hdm.init("/home/amee/workspace/version1/src/version2/Customer.java", "/home/amee/workspace/Version2/src/version3/Customer.java");
                
                long startTime = System.currentTimeMillis();
                hdm.match();
                long endTime = System.currentTimeMillis();
                System.out.println("Time Elapsed = "+((endTime-startTime)/1000.0f));
                hdm.print();
                hdm.printLine();

              
        }

        @Override
        public String getName() {
                // TODO Auto-generated method stub
                return "HDiffSHMatching";
        }

}

class PairComparator implements Comparator<Pair> {

        public int compare(Pair a, Pair b) {
                float originalValue = a.getValue();
                float revisedValue = b.getValue();

                if (revisedValue > originalValue)
                        return 1;
                else if (revisedValue < originalValue)
                        return -1;
                else
                        return 0;
        }
}

class SHPairComparator implements Comparator<Pair> {

        public int compare(Pair a, Pair b) {
                float originalValue = a.getValue();
                float revisedValue = b.getValue();

                if (revisedValue > originalValue)
                        return -1;
                else if (revisedValue < originalValue)
                        return 1;
                else
                        return 0;
        }
}
class IndexComparator implements Comparator {

        public int compare(Object a, Object b) {
                int originalValue = (Integer)a;
                int revisedValue  =  (Integer)b;

                if (revisedValue > originalValue)
                        return 1;
                else if (revisedValue < originalValue)
                        return -1;
                else
                        return 0;
        }
}


class Pair {
        int originalIndex;
        int revisedIndex;
        float value; // value can be similarity between a pair of line

        public int getOriginalIndex() {
                return originalIndex;
        }

        public int getRevisedIndex() {
                return revisedIndex;
        }

        public float getValue() {
                return value;
        }

        public Pair(int firstIndex, int secondIndex, float value) {
                super();
                this.originalIndex = firstIndex;
                this.revisedIndex = secondIndex;
                this.value = value;
        }
}