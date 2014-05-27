package com.marge.split;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.MethodRef;

public class MethodVisitor extends ASTVisitor {
	
		CompilationUnit cu ;
	
	  List<MethodInfo> methods = new ArrayList<MethodInfo>();
	   
	  HashMap<MethodDeclaration, ArrayList<MethodInvocation>> invocationsForMethods =
			    new HashMap<MethodDeclaration, ArrayList<MethodInvocation>>();
	   
	   HashMap<String, ArrayList<MethodDeclaration>>	caller =
			    new HashMap<String, ArrayList<MethodDeclaration>>();
	   
	   //HashMap<MethodInvocation, ArrayList<MethodDeclaration>> caller =
			 //   new HashMap<MethodInvocation, ArrayList<MethodDeclaration>>();
	   
	//  Map <String,MethodDeclaration> methods = new HashMap <String , MethodDeclaration>();
	  private MethodDeclaration activeMethod;

	  public MethodVisitor ( CompilationUnit cu ) {
		  this.cu = cu ;
		  
	  }
	  
	  @Override
	  public boolean visit(MethodDeclaration node) {
		 
		  
		 
		  if(node.getParent().getNodeType() == ASTNode.TYPE_DECLARATION){ 
			 
	//	  System.out.println("Method Name "+" NT "+ASTNode.TYPE_DECLARATION +" " + node.getName() + " Parent " + node.getParent() + " Type " + node.getParent().getNodeType() );
		  activeMethod = node;
		  //int body = node.getBody().toString().split("\n").length - 1 ;
		 
		  int start = cu.getLineNumber(node.getStartPosition());
		  //int end =  start + body ;
		  int end = cu.getLineNumber(node.getStartPosition() + node.getLength());
		  //node.get
		  //ddddSystem.out.println("Start = " + start + " End " + end  );
		 // System.out.println(node.getBody());
		  MethodInfo m  = new MethodInfo ( node , start , end );
		  methods.add(m);
	  
		 
		  }
		  
	    return super.visit(node);
	  }
	  

	  @Override
	public boolean visit(MethodInvocation node) {
		// TODO Auto-generated method stub
		 
		  //System.out.println("Invoke " + node.getName()+" " + activeMethod.getName());
		  
		  
		  if (invocationsForMethods.get(activeMethod) == null) {
			  
              invocationsForMethods.put(activeMethod, new ArrayList<MethodInvocation>());
          
		  }
		  
		  if(caller.get(node.getName().toString()) == null){
	
			  caller.put(node.getName().toString().trim(), new ArrayList <MethodDeclaration>());			 
		  }
		  
		  invocationsForMethods.get(activeMethod).add(node);
		  caller.get(node.getName().toString()).add(activeMethod);
         
          
          
          
          return super.visit(node);
	}


	public List<MethodInfo> getMethods() {
	    
		return methods;
	  }
	
	public HashMap<MethodDeclaration, ArrayList<MethodInvocation>> getInvokeMethod(){
		return invocationsForMethods;
	}
	public HashMap<String, ArrayList<MethodDeclaration>> getCaller(){
		return caller;
	}


	

} 