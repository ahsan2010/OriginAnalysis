package com.srlab.line;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Vector;

import com.srlab.line.*;

public class SimHash {

	/**
	 * @param args
	 */
	private String input;
	private static JenkinsHash jenkinsHash = new JenkinsHash ();
	public static byte[] genSimHash(String string){
		//Step-1: pick a hash size
		int hashSize  = 32;
		int tokenSize = 2; 
		//Step-2 Construct a vector 
		Vector<Integer> vector = new Vector<Integer>(hashSize);
		//int vector[] = new int[32];
		//Step-2:1 Intialize the value with zero
		for(int i=0;i<hashSize;i++){
			vector.add(i,0);
			//vector[i]=0;
		}
		//step:3-1 break the line into shingles
		ShingleTokenizer shTokenizer = new ShingleTokenizer(tokenSize,true);
		ArrayList<String> list = shTokenizer.tokenizeToArrayList(string);
		//System.out.println("List = "+list);
		for(String str :list){
		byte bytes[];
		bytes = str.getBytes();
		
		for (int charCount = 0; charCount < str.length(); charCount++) {
			bytes[charCount] = (byte)str.codePointAt(charCount);
		}
		//Long tokenHash = jenkinsHash.hash(bytes);
		byte b[];
		char chars[];
		//bytes = Long.toBinaryString(jenkinsHash.hash(bytes)).getBytes();
		b = Long.toBinaryString(jenkinsHash.hash(bytes)).getBytes();
		
		//System.out.println("Vector size= "+vector.size());
	    //for (int c=0; c<32; c++){  
	     //   vector[c] += (tokenHash & (1l << c)) == 0 ? -1: 1;
	    //}
		byte b3[]=new byte[hashSize];
	    if(bytes.length<32){
	    	int padding = 32-b.length;

	    	for(int l1=0;l1<padding;l1++)
	    		b3[l1]='0';
	    	for(int l2=0;l2<b.length;l2++){
	    		b3[padding+l2]= b[l2];  
	    	}

	    	
	    	//System.out.println("bytes:"+""+new String(buf).toCharArray());
	    }
		for(int i=0;i<hashSize;i++){
			//if(i<bytes.length)
			{
			if(b3[i]=='1'){
				//vector[i]=vector[i]+1;
				vector.set(i, (vector.get(i)+1));
			}
			else{
				//vector[i]=vector[i]-1;
				vector.set(i, (vector.get(i)-1));
			}}
			//else
			{
				//vector[i]=vector[i]-1;
				//vector.set(i, (vector.get(i)-1));			
			}
		}
		}
		  
		byte simhash[] = new byte[hashSize];
		for(int i=0;i<hashSize;i++){
			if(vector.get(i)>0){
				simhash[i]='1';
			}
			else{
				simhash[i]='0';
			}
		}
		//System.out.println("\ninput =   "+string+"  SimHash = "+simhash);
		  
		return simhash;	
	}
	
	public static byte[] gen128SimHash(String string){
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//byte[] hash = md.digest();
		//Step-1: pick a hash size
		int hashSize = 128;
		//Step-2 Construct a vector 
		Vector<Integer> vector = new Vector<Integer>(hashSize);
		//int vector[] = new int[32];
		//Step-2:1 Intialize the value with zero
		for(int i=0;i<hashSize;i++){
			vector.add(i,0);
			//vector[i]=0;
		}
		//step:3-1 break the line into shingles
		ShingleTokenizer shTokenizer = new ShingleTokenizer(2,true);
		ArrayList<String> list = shTokenizer.tokenizeToArrayList(string);
		//System.out.println("List = "+list);
		for(String str :list){
		byte bytes[];
		bytes = new byte[str.length()];
		
		for (int charCount = 0; charCount < str.length(); charCount++) {
			bytes[charCount] = (byte)str.codePointAt(charCount);
		}
		//Long tokenHash = jenkinsHash.hash(bytes);
		byte b[];
		//bytes = Long.toBinaryString(jenkinsHash.hash(bytes)).getBytes();
		//b = Long.toBinaryString(jenkinsHash.hash(bytes)).getBytes();
		md.update(string.getBytes()); 
		b = md.digest();
		//System.out.println("Vector size= "+vector.size());
	    //for (int c=0; c<32; c++){  
	     //   vector[c] += (tokenHash & (1l << c)) == 0 ? -1: 1;
	    //}
		byte b3[]=new byte[hashSize];
	    if(bytes.length<32){
	    	int padding = hashSize-b.length;

	    	for(int l1=0;l1<padding;l1++)
	    		b3[l1]='0';
	    	for(int l2=0;l2<b.length;l2++){
	    		b3[padding+l2]= b[l2];  
	    	}

	    	
	    	//System.out.println("bytes:"+""+new String(buf).toCharArray());
	    }
		for(int i=0;i<hashSize;i++){
			//if(i<bytes.length)
			{
			if((char)b3[i]=='1'){
				//vector[i]=vector[i]+1;
				vector.set(i, (vector.get(i)+1));
			}
			else{
				//vector[i]=vector[i]-1;
				vector.set(i, (vector.get(i)-1));
			}}
			//else
			{
				//vector[i]=vector[i]-1;
				//vector.set(i, (vector.get(i)-1));			
			}
		}
		}
		  
		byte simhash[] = new byte[hashSize];
		for(int i=0;i<hashSize;i++){
			if(vector.get(i)>0){
				simhash[i]='1';
			}
			else{
				simhash[i]='0';
			}
		}
		System.out.println("\ninput =   "+string+"  SimHash = "+simhash+"\n");
		for(int k=0;k<hashSize;k++){
			System.out.print((char)simhash[k]);
		}
		System.out.println("");
		
		return simhash;	
	}
	public static long genFasterSimHash(String string){
		//Step-1: pick a hash size
		int hashSize = 64;
		int tokenSize =2;
		//Step-2 Construct a vector 
		int vector[] = new int[]{
			0,0,0,0,0,0,0,0,
			0,0,0,0,0,0,0,0,
			0,0,0,0,0,0,0,0,
			0,0,0,0,0,0,0,0,
			
			0,0,0,0,0,0,0,0,
			0,0,0,0,0,0,0,0,
			0,0,0,0,0,0,0,0,
			0,0,0,0,0,0,0,0
			
		};
		//Step-2:1 Intialize the value with zero
		//for(int i=0;i<hashSize;i++){
		//	vector[i]=0;
			//vector[i]=0;
		//}
		//step:3-1 break the line into shingles
		ShingleTokenizer shTokenizer = new ShingleTokenizer(tokenSize,true);
		ArrayList<String> list = shTokenizer.tokenizeToArrayList(string);
		//System.out.println("List = "+list);
		for(String str :list){
		byte bytes[];
		
		//bytes = new byte[str.length()];
		//for (int charCount = 0; charCount < str.length(); charCount++) {
		//	bytes[charCount] = (byte)str.charAt(charCount);
		//}
		Long tokenHash = jenkinsHash.hash(str.getBytes());
		//Long tokenHash = ApacheHash.lookup3ycs64(str, 0, str.length(), 0);
	    for (int c=0; c<hashSize; c++){  
	        vector[c] += (tokenHash & (1l << c)) == 0 ? -1: 1;
	    }
		}
		  
		long simhash=0;
		for (int c = 0; c < hashSize; c++)
		simhash = simhash | ((vector[c] > 0 ? 1l : 0l) << c);
		  
		//byte simhash[] = new byte[32];
		//for(int i=0;i<32;i++){
		//	if(vector[i]>0){
		//		simhash[i]='1';
		//	}
		//	else{
		//		simhash[i]='0';
		//	}
		//}
		//System.out.println("\ninput =   "+string+"  SimHash = "+simhash);
		//simhash = simhash<<2;
		return simhash;	
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		long start = System.currentTimeMillis();
		byte b1[] = SimHash.genSimHash("" +
				"protected void main(String args[])");
		byte b2[] = SimHash.genSimHash("" +
				"this.aal = 100");
		byte b3[] = SimHash.genSimHash("" +
				"this.val = value");
		
		String str1="protected void main(String args[])";
		String str2="this.val = 100";
		String str3="this.aal = 100";
		
		long l1=ApacheHash.lookup3ycs64(str1, 0, str1.length(), 0);
		long l2=ApacheHash.lookup3ycs64(str2, 0, str2.length(), 0);
		long l3=ApacheHash.lookup3ycs64(str3, 0, str3.length(), 0);
		for(int i=0;i<1;i++){
			SimHash.genSimHash("import java.util.HashMap");
		}
		System.out.println("b1= "+b1);
		System.out.println("b2= "+b2);
		System.out.println("b3= "+b3);
		
		//System.out.println("l1= "+(Long.bitCount(l2l1)));
		System.out.println("l2= "+(Long.bitCount(l2|l3)));
		System.out.println("l3= "+Long.bitCount(l3));
		
		System.out.println("Hamming distance between: b1,b2  :"+SimHash.hamming(b1, b2));
		System.out.println("Hamming distance between: b2,b3  :"+SimHash.hamming(b2, b3));
		System.out.println("Hamming distance between: b3,b1  :"+SimHash.hamming(b1, b3));
		
		long end = System.currentTimeMillis();
		System.out.println("Time Spended: "+(end-start)/(1000.0f));
		
		System.out.println("Number of set bit: "+ SimHash.hamming(7l, 0l));
		long test = 0;
		
		String password = "123456";
		 
       try { MessageDigest md;
		
			md = MessageDigest.getInstance("MD5");
		    md.update(password.getBytes());
		    
	        byte byteData[] = md.digest();
	        System.out.println("");
	        for(int i=0;i<byteData.length;i++){
	        	System.out.print(byteData[i]);
	        }
	        //System.out.println("Changed: "+new String(byteData));
		
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	//for(int i=0;i<32;)

	}
	public static int hamming(byte b1[],byte b2[]){
		int count=0;
		for(int i=0;i<32;i++){
			if(b1[i]!=b2[i]){
				count++;
			}
		}
		return count;
	}
	
	public static int hamming32(byte b1[],byte b2[]){
		int count=0;
		for(int i=0;i<32;i++){
			if(b1[i]==b2[i] && b1[i]=='1'){
				count ++;
			}
		}
		return count;
	}
	public static int hamming32(int b1,int b2){
		int count=0;
		return (32-Long.bitCount(b1^b2));
		
	}
	public static int hamming128(byte b1[],byte b2[]){
		int count=0;
		for(int i=0;i<128;i++){
			if(b1[i]!=b2[i]){
				count++;
			}
		}
		return count;
	}
	public static int hamming64(byte b1[],byte b2[]){
		int count=0;
		for(int i=0;i<64;i++){
			if(b1[i]!=b2[i]){
				count++;
			}
		}
		return count;
	}
	public static int hamming(long b1,long b2){
		//tested
		return (Long.bitCount(b1^b2));
		//return (Long.bitCount(b1&b2));
	}
	
	
}
