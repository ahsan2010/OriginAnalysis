package com.marge.split;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;

public class MethodInfoParse {
	
	List <MethodDeclaration> parseMethod =  new ArrayList <MethodDeclaration>();
	HashMap<MethodDeclaration, ArrayList<MethodInvocation>> invocationsForMethods1 ;
	HashMap<MethodDeclaration, ArrayList<MethodInvocation>> invocationsForMethods2 ;
	
	HashMap<String, ArrayList<MethodDeclaration>> caller1 =
		    new HashMap<String, ArrayList<MethodDeclaration>>();
	HashMap<String, ArrayList<MethodDeclaration>> caller2 =
		    new HashMap<String, ArrayList<MethodDeclaration>>();
	
	public MethodInfoParse (
								List <MethodDeclaration> parseMethod,
								HashMap<MethodDeclaration, ArrayList<MethodInvocation>> invocationsForMethods1,
								HashMap<MethodDeclaration, ArrayList<MethodInvocation>> invocationsForMethods2,
								HashMap<String, ArrayList<MethodDeclaration>> caller1,
								HashMap<String, ArrayList<MethodDeclaration>> caller2
							){
			
			this.parseMethod = parseMethod ;
			this.invocationsForMethods1 = invocationsForMethods1 ;
			this.invocationsForMethods2 = invocationsForMethods2 ;
			this.caller1 = caller1 ;
			this.caller2 = caller2 ;
		
	}
}
