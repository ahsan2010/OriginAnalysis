package com.marge.split;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;

public class Parse {

	String source1;
	String source2;
	int version;

	MethodVisitor visitor1 ;
	MethodVisitor visitor2;

	HashMap<MethodDeclaration, ArrayList<MethodInvocation>> invocationsForMethods1;
	HashMap<MethodDeclaration, ArrayList<MethodInvocation>> invocationsForMethods2;

	HashMap<String, ArrayList<MethodDeclaration>> caller1 = new HashMap<String, ArrayList<MethodDeclaration>>();
	HashMap<String, ArrayList<MethodDeclaration>> caller2 = new HashMap<String, ArrayList<MethodDeclaration>>();

	public Parse(String source1, String source2) {
		this.source1 = source1;
		this.source2 = source2;
		
	}

	public void parseMethod() {

		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setSource(source1.toCharArray());
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		final CompilationUnit cu = (CompilationUnit) parser.createAST(null);

		visitor1 = new MethodVisitor(cu);
		
		
		cu.accept(visitor1);
		invocationsForMethods1 = visitor1.getInvokeMethod();
		caller1 = visitor1.getCaller();

		ASTParser parser2 = ASTParser.newParser(AST.JLS3);
		parser2.setSource(source2.toCharArray());
		parser2.setKind(ASTParser.K_COMPILATION_UNIT);
		final CompilationUnit cu2 = (CompilationUnit) parser2.createAST(null);

		visitor2 = new MethodVisitor(cu2);
		cu2.accept(visitor2);
	
		invocationsForMethods2 = visitor2.getInvokeMethod();
		caller2 = visitor2.getCaller();

	}

	public HashMap<MethodDeclaration, ArrayList<MethodInvocation>> getInvocationFirst() {

		return invocationsForMethods1;
	}

	public HashMap<MethodDeclaration, ArrayList<MethodInvocation>> getInvocationSecond() {

		return invocationsForMethods2;
	}

	public HashMap<String, ArrayList<MethodDeclaration>> getCallerSecond() {
		return caller2;
	}

	public HashMap<String, ArrayList<MethodDeclaration>> getCallerFirst() {
		return caller1;
	}

	public List<MethodInfo> getMethodFirst() {
		return visitor1.getMethods();
	}

	public List<MethodInfo> getMethodSecond() {
		return visitor2.getMethods();
	}

}
