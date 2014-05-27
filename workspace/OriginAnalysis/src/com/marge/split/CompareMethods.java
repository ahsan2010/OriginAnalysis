package com.marge.split;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import uk.ac.shef.wit.simmetrics.similaritymetrics.AbstractStringMetric;
import uk.ac.shef.wit.simmetrics.similaritymetrics.Levenshtein;

import com.srlab.line.*;

public class CompareMethods {

	javaxt.io.File f1;
	javaxt.io.File f2;
	BufferedWriter bw, bw_data;
	int compareValue[][];
	Parse pmethod;
	double thresHoldValue = 1;
	boolean updateValueFinish = false;
	Document doc;

	Element item ;
	
	List<MethodInfo> m1 = new ArrayList<MethodInfo>();
	List<MethodInfo> m2 = new ArrayList<MethodInfo>();

	HashMap<MethodDeclaration, ArrayList<MethodInvocation>> invocationsForMethods1;
	HashMap<MethodDeclaration, ArrayList<MethodInvocation>> invocationsForMethods2;

	HashMap<MethodDeclaration, String> callMapVer1 = new HashMap<MethodDeclaration, String>();
	HashMap<MethodDeclaration, String> callMapVer2 = new HashMap<MethodDeclaration, String>();

	HashMap<String, ArrayList<MethodDeclaration>> caller1 = new HashMap<String, ArrayList<MethodDeclaration>>();
	HashMap<String, ArrayList<MethodDeclaration>> caller2 = new HashMap<String, ArrayList<MethodDeclaration>>();

	public CompareMethods(javaxt.io.File f1, javaxt.io.File f2,
			BufferedWriter bw, BufferedWriter bw_data, Element item,Document doc, int threshValue) {
		this.f1 = f1;
		this.f2 = f2;
		this.bw = bw;
		this.bw_data = bw_data;
		this.item = item ;
		this.doc = doc ;
		this.thresHoldValue = threshValue;

	}

	public String commentRemove ( String str ){
		
		boolean blockComment = false ;
		String line = "";
		String code = "";
		BufferedReader br = new BufferedReader(new StringReader(str));
		
		try{
		
		while ( ( line = br.readLine() ) != null ){
				if ( line.isEmpty() ) continue;
				
				line = line.trim();
				
				if ( line.startsWith("/*") ){
					blockComment = true ;
					continue ;
				}

				line = line.replaceAll("(?:/\\*(?:[^*]|(?:\\*+[^*/]))*\\*+/)|(?://.*)","");
				
				if ( !blockComment ){
					code += line + "\n" ;
					  continue ; 
				}
				
				if( line.endsWith("*/") ){
					blockComment = false ;
					continue ;
				}
				
				
				
				
			}
		}catch ( Exception e ){
			e.printStackTrace();
		}
			
			
		//	System.out.println("Finished");
		 return str;
	}
	public Set<String> getParameter(MethodDeclaration method) {

		List<String> para = new ArrayList<String>();
		Set<String> paraMeterList = new HashSet<String>();

		for (Object m : method.parameters()) {
			para.add(m.toString().trim());
		}
		Collections.sort(para);
		String st = "";
		StringTokenizer token = new StringTokenizer(para.toString(), ",[]");
		int ind = 0;
		while (token.hasMoreTokens()) {
			String temp = token.nextToken().toLowerCase();
			temp.trim();
			if (!temp.isEmpty())
				paraMeterList.add(temp);
		}
		return paraMeterList;
	}

	public double getParameterSimilarity(Set<String> paraVer1,
			Set<String> paraVer2) {
		
		if(paraVer1.size() == 0 && paraVer2.size() == 0 )
			return 1.0 ;
		
		double paraSimilarity = 0.0;
		Set<String> temp = new HashSet<String>();
		Set<String> temp2 = new HashSet<String>();

		temp.addAll(paraVer1);
		temp2.addAll(paraVer2);
		
		int size1 = temp.size();
		int size2 = temp2.size();

		temp.retainAll(paraVer2);

		double denominator = Math.max(1, Math.min(size1, size2));
		paraSimilarity = temp.size() / denominator;


		return paraSimilarity;
	}

	int getTotalLine(String path) {

		int line = 0;
		try {

			Process p = Runtime.getRuntime().exec("wc -l " + path);
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(
					p.getInputStream()));
			String output = "";
			output += stdInput.readLine();
			String element[] = output.split(" ");
			line = Integer.parseInt(element[0]);
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}

		return line;
	}
	
	public Set < String >  getCallerName( ArrayList <MethodDeclaration> md){
		
		if( md == null ) return null ;
		
		Set < String > callerName = new HashSet<String>();
		for ( MethodDeclaration method : md ){
		
			callerName.add(method.getName().toString().toLowerCase());
		}
		
		
		return callerName ; 
		
	}

	public String getCalleeName(ArrayList<String> d) {
		HashMap<String, Integer> mapCall = new HashMap<String, Integer>();
		Collections.sort(d);
		String st = "";
		StringTokenizer token = new StringTokenizer(d.toString(), ",[] ");
		while (token.hasMoreTokens()) {
			String temp = token.nextToken();
			if (!mapCall.containsKey(temp)) {
				if (!temp.isEmpty())
					st += temp + '\n';
				mapCall.put(temp, 1);
			}

		}
		return st;
	}
	
	/*public void getCallPrintNameCaller (MethodDeclaration md, String temp , int flag) {
		
		if (temp == null ) {
			System.out.println("No Caller ");
			return ;
		}
		String callerList [] = temp.split("\n");
		
		List<String> wordList = Arrays.asList(callerList);  
		
		Collections.sort(wordList , new Comparator < String > () {
			@Override
			public int compare (String a , String b ) {
				return ( a.compareTo(b));
			}
		});
		
		
		write("/////////////////////////////",flag);
		for ( String st : wordList ){
			write(st,flag);
		}
		write("/////////////////////////////",flag);
		
		
	}*/

	double countCalleeSimilirarity(String callVer1, String callVer2) {
		double value = 0;
		callVer1.trim();
		callVer2.trim();
		if (callVer1.isEmpty() || callVer2.isEmpty()) {
			return value;
		}

		Set<String> callList1 = new HashSet<String>();
		Set<String> callList2 = new HashSet<String>();
		Set<String> totalList1 = new HashSet<String>();

		String callMethodName1[] = callVer1.split("\n");
		String callMethodName2[] = callVer2.split("\n");

		for (String st : callMethodName1) {
			callList1.add(st.toLowerCase());
			totalList1.add(st.toLowerCase());
		}
		for (String st : callMethodName2) {
			callList2.add(st.toLowerCase());
			totalList1.add(st.toLowerCase());
		}
		
		int size1  = callList1.size();
		int size2 = callList2.size();
		
		callList1.retainAll(callList2);

		double denominator = Math.max(1, Math.min(size1,size2));
		value = callList1.size() / denominator;

		// System.out.println("Caller  "+value+" " + callList1.size() + "  " + denominator);
		// totalList1.size());

		return value;
	}
	
	
	int isM2SplitFromM1 (MethodDeclaration md1, MethodDeclaration md2) {
	//	int result = 0 ;
		
		Set < String > callerOfMethod2 = new HashSet < String > ();
		callerOfMethod2 = getCallerName(caller2.get(md2.getName().toString()));
		for ( String st : callerOfMethod2){
			if( st.compareToIgnoreCase(md1.getName().toString()) == 0 ){
				return 1 ;
			}
		}
		
		return 0 ;
		//return result ;
	}
	
	
	double countCallerSimilarity ( ArrayList <MethodDeclaration> md1 , ArrayList <MethodDeclaration> md2){
			double value = 0 ;
			
			Set < String > method1Caller = getCallerName(md1);
			Set < String > method2Caller = getCallerName(md1);
		
			if ( method1Caller == null || method2Caller == null )
			{
				return 0 ;
			}
			
			int size1 = method1Caller.size();
			int size2 = method2Caller.size();
			
			method1Caller.retainAll(method2Caller);
			
			double denominator =Math.max(1, Math.min(size1,size2));
			value =  method1Caller.size()/denominator ;
			
			return value ;
	}

	public double getBodySimilarity(String bodyVer1, String bodyVer2) {

		double bodyMatch = 0;
		AbstractStringMetric metric = new Levenshtein();
		bodyMatch = metric.getSimilarity(bodyVer1, bodyVer2);

		return bodyMatch;
	}

	public void CalculateMatchLines() {

		try {

			ArrayList<MatchLines> match = new ArrayList<MatchLines>();
			HDiffSHMatching hdm = new HDiffSHMatching();
			
		//	match = hdm.calculate(f1.getText(),f2.getText());
			match = hdm.calculate(commentRemove(f1.getText()), commentRemove(f2.getText())); // Line Matching
																// algorithm ..
																// using..

			int size = Math.max(getTotalLine(f1.getPath() + f1.getName()),
					getTotalLine(f2.getPath() + f2.getName()));
			int tmethod = Math.max(m1.size(), m2.size());

			int index[] = new int[size + 200];
			MethodInfo[] rev2Method = new MethodInfo[size + 100];
			compareValue = new int[tmethod + 10][tmethod + 10];

			int line1[] = new int[size + 1000];
			int line2[] = new int[size + 1000];

			for (MatchLines m : match) {

				line1[m.getRev1LinePosition()] = m.getRev2LinePosition();
				line2[m.getRev2LinePosition()] = m.getRev1LinePosition();

			}
			for (MethodInfo info2 : m2) {
				ArrayList<String> s2 = new ArrayList<String>();

				for (int i = info2.startLine; i <= info2.endLine; i++) {
					if (line2[i] > 0) {
						rev2Method[i] = info2;
						index[i] = m2.indexOf(info2);
					}
				}
				if (pmethod.getInvocationSecond().containsKey(info2.method)) {
					for (MethodInvocation m : pmethod.getInvocationSecond()
							.get(info2.method)) {
						s2.add(m.getName().toString());
					}
				}

				String st = getCalleeName(s2);
				callMapVer2.put(info2.method, st);

			}
			for (MethodInfo info1 : m1) {
				ArrayList<String> s1 = new ArrayList<String>();

				for (int i = info1.startLine; i <= info1.endLine; i++) {

					if (line1[i] > 0) {
						compareValue[m1.indexOf(info1)][index[line1[i]]]++;

					}
				}
				if (pmethod.getInvocationFirst().containsKey(info1.method)) {
					for (MethodInvocation m : pmethod.getInvocationFirst().get(
							info1.method)) {
						s1.add(m.getName().toString());
					}
				}
				String st = "";
				st += getCalleeName(s1);
				callMapVer1.put(info1.method, st);

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		updateValueFinish = true;

	}

	/*
	 *  calculate the matching candidate using predefined threshHold value for  
	 * 	line comparing and write down the information to a file
	 */
	
	
	public boolean calculateSimilarity() {

		boolean match = false ;
		String data = " ";
		String data_write = "";
		
		try {
			bw_data.write(data_write);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
	
		
		for (int i = 0; i < m1.size(); i++) {
			data = " ";

			for (int j = 0; j < m2.size(); j++) {

				boolean noBody = false;

				if (m1.get(i).method.getBody() == null
						|| m2.get(j).method.getBody() == null) {
					noBody = true;
				}

				//double dynamicThreshHold = thresHoldValue;

				if (compareValue[i][j] >= Math.floor(thresHoldValue)) {

					MethodDeclaration md1 = m1.get(i).method;
					MethodDeclaration md2 = m2.get(j).method;
					boolean flag = true;

					if (!noBody) {

						int len1 = m1.get(i).method.getBody().toString()
								.split("\n").length;
						int len2 = m2.get(j).method.getBody().toString()
								.split("\n").length;

						if (md1.getName().toString()
								.equals(md2.getName().toString())) {
							flag = false;
						}

					} else
						flag = false;

					try {
						if (flag) {

							match = true ;
							
							double bodyValue = getBodySimilarity(md1.getBody()
									.toString(), md2.getBody().toString());
							double paraValue = getParameterSimilarity(
									getParameter(md1), getParameter(md2));				
							
							double calleeValue = countCalleeSimilirarity(
									callMapVer1.get(m1.get(i).method),
									callMapVer2.get(m2.get(j).method));
							double callerValue = countCallerSimilarity (
										caller1.get(m1.get(i).method.getName().toString()),
										caller2.get(m2.get(j).method.getName().toString())
									);

							bw.write("Class " + f1.getName() + " (Ver1)-> "
									+ m1.get(i).method.getName().toString()
									+ " (Ver2)-> "
									+ m2.get(j).method.getName().toString()
									+ "   " + "Dynamic Threshold ("
									+ thresHoldValue + " )" + "\n");

							data_write = String.format("%16s", " ");

							data_write += m1.get(i).method.getName() + " -> "
									+ m2.get(j).method.getName();
							data_write += "  "
									+ String.format("%18d (%f) (%f) (%f)",
											compareValue[i][j], bodyValue,
											paraValue, calleeValue);

							bw_data.write(data_write);
							bw_data.write("\n");
							
							
							Element methodInfo = doc.createElement("Similartiy");
							methodInfo.setAttribute("OldVersion", md1.getName().toString());
							methodInfo.setAttribute("NewVersion",md2.getName().toString());
							item.appendChild(methodInfo);
							
							
							///////////////////////////////
							
							Element matchLines = doc.createElement("LineSimilarity");
							matchLines.appendChild(doc.createTextNode(""+compareValue[i][j]));
							methodInfo.appendChild(matchLines);
							
							Element body = doc.createElement("Body");
							body.appendChild(doc.createTextNode(""+bodyValue));
							methodInfo.appendChild(body);
							
							Element para = doc.createElement("Parameter");
							para.appendChild(doc.createTextNode(""+paraValue));
							methodInfo.appendChild(para);
							
							Element caller = doc.createElement("Caller");
							caller.appendChild(doc.createTextNode(""+callerValue));
							methodInfo.appendChild(caller);
							
							Element callee = doc.createElement("Callee");
							callee.appendChild(doc.createTextNode(""+calleeValue));
							methodInfo.appendChild(callee);							
							
							
							/////////////////////////////////////

						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			}
		}
		return match; 

	}

	public int[][] getMatrix() {

		if (updateValueFinish)
			return compareValue;
		return null;

	}

	public List<MethodInfo> getMethodInfoVersionOne() {
		return m1;
	}

	public List<MethodInfo> getMethodInfoVersionSecond() {
		return m2;
	}
	
	/*public void printAllCallerVersionOne(){
		
		for (int i = 0; i < m1.size(); i++) {
			
			MethodDeclaration md1 = m1.get(i).method;
			write("*******" + md1.getName() + "**********\n",1);
		//	System.out.println("*******" + md1.getName() + "**********");
			if( md1 != null && callMapVer1.containsKey(md1))getCallPrintNameCaller (md1,callMapVer1.get(md1),1);
		}
	}*/
	
	
	/*public void printAllCallerVersionTwo(){
	
	for (int i = 0; i < m2.size(); i++) {
		
		MethodDeclaration md2 = m2.get(i).method;
		
		String data = " ";
		data += "*******" + md2.getName() + "**********";
		
		write(data,2);
		//System.out.println("*******" + md2.getName() + "**********");
		getCallPrintNameCaller (md2,callMapVer2.get(md2),2);
	}
}*/
	
	/*public void printCallerNameVer1(){
		
			for (int i = 0; i < m1.size(); i++) {
			
			MethodDeclaration md1 = m1.get(i).method;
			write("*******" + md1.getName() + "**********\n",1);
			System.out.println("*******" + md1.getName() + "******ver 1 **");
			if( md1 != null && caller1.containsKey(md1.getName().toString().trim()) ) {
			
				getCallerName (caller1.get(md1.getName().toString()),1);
			}
		}
	}*/
	
	/*public void printCallerNameVer2(){
		
		for (int i = 0; i < m2.size(); i++) {
		
		MethodDeclaration md2 = m2.get(i).method;
		write("*******" + md2.getName() + "**********\n",2);
		
		if( md2 != null && caller2.containsKey(md2.getName().toString()) ) getCallerName (caller2.get(md2.getName().toString()),2);
	}
}*/
	
	
	
public void write (String data , int flag ) {
		
	try{
		
		if(flag == 1 ){
			
			bw_data.write(data);
			bw_data.write("\n");
		}
		else if ( flag == 2 ){
			bw.write(data);
			bw.write("\n");
		}
		
		
	}catch(Exception e ){
		e.printStackTrace();
	}
}	



	public void extractMethodyFromAST() {

		try{
		
		pmethod = new Parse(commentRemove(f1.getText()), commentRemove(f2.getText()));
		pmethod.parseMethod();

		m1 = pmethod.getMethodFirst();
		m2 = pmethod.getMethodSecond();
		
		caller1 = pmethod.getCallerFirst();
		caller2 = pmethod.getCallerSecond();

		// AbstractStringMetric metric = new Levenshtein();
		// CalculateMatchLines(m1, m2,pmethod) ;
		}catch (Exception e ){
			e.printStackTrace();
		}
	}
}
