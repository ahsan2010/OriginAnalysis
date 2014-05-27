package com.marge.split;


import javaxt.io.*;
import javaxt.utils.Array;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
//import java.nio.file.Files;
//import java.nio.file.Paths;
import java.util.*;

//import javax.swing.plaf.basic.BasicInternalFrameTitlePane.SystemMenuBar;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.LineComment;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.Comment;

import uk.ac.shef.wit.simmetrics.similaritymetrics.*;

import java.util.*;

public class Information {

	MethodDeclaration m1;
	MethodDeclaration m2;
	 ArrayList<MethodInvocation> inv1 = new ArrayList < MethodInvocation>();
	 ArrayList<MethodInvocation> inv2 = new ArrayList < MethodInvocation>();
	
	 Data data;
	
	public Information ( MethodDeclaration m1, MethodDeclaration m2, 
			ArrayList<MethodInvocation> inv1, ArrayList<MethodInvocation> inv2,Data data) {
		
			this.m1 = m1;
			this.m2 = m2;
			this.inv1 = inv1;
			this.inv2 = inv2;
			this.data = data;
			
	}
	
	public String FurstRet () {
	
		return m1.getReturnType2().toString();
	}
	public String SecRet () {
		return m2.getReturnType2().toString();
	}
	
	public String FirstBody () {
		return m1.getBody().toString();
	}
	public String SecBody ( ) {
		return m2.getBody().toString();
	}
	public String FirstPara () {
		//return m1.parameters().toString();
		String temp = "";
		for (Object l : m1.parameters()){
			temp +=l.toString();
		}
		return temp;
	}
	public String SecPara () {
		
		String temp = "";
		for (Object l : m2.parameters()){
			temp +=l.toString();
		}
		return temp;
	}
	
	public String getFirstCallee () {
		
		String temp = "" ;
		Set<String> s = new HashSet<String>();
		for ( MethodInvocation inv : inv1){
			s.add(inv.getName().toString());	
			//temp+=inv.getName().toString()+"\n";
		}
		
		for ( String name : s ){
			temp+=name+'\n';
		}
		
		return temp;
	}
	public String getSecCallee () {
			
			String temp = "" ;
			Set<String> s = new HashSet<String>();
			for ( MethodInvocation inv : inv2){
				s.add(inv.getName().toString());	
				//	temp+=inv.getName().toString()+"\n";
			}
			
			for ( String name : s ){
				temp+=name+'\n';
			}
			
			return temp;
		}
	public String getInfo () {
		String temp = "";
		temp+= 	" Name : " + data.name
				+ "\n"+ " Body : " + data.body 
				+ "\n" + " Para : " + data.para
				+ "\n" + " Return : " + data.ret
				+ "\n" + " Callee : " + data.callee
				+ "\n" + " Total : " + data.total;
		return temp;		
	}
	
	
}
