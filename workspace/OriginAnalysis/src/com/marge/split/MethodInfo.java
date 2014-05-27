package com.marge.split;


import org.eclipse.jdt.core.dom.MethodDeclaration;

public class MethodInfo {
	
	MethodDeclaration method;
	int startLine;
	int endLine ;
	
	public MethodInfo( MethodDeclaration node , int startLine , int endLine){
		
		this.method  = node ;
		this.startLine = startLine ;
		this.endLine = endLine ;
		
	}
	
}
