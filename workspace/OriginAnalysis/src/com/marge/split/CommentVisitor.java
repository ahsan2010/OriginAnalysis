package com.marge.split;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.BlockComment;
import org.eclipse.jdt.core.dom.Comment;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.LineComment;
import org.eclipse.jdt.core.dom.MethodDeclaration;

public class CommentVisitor extends ASTVisitor{
	
	CompilationUnit compilationUnit;
	List<String> comments = new ArrayList<String>();
    private String[] source;

    public CommentVisitor(CompilationUnit compilationUnit, String[] source) {

    	super();
        this.compilationUnit = compilationUnit;
        this.source = source;
        
    }

    public boolean visit(LineComment node) {

    	//System.out.println("GOT COME1");
    	
        int startLineNumber = compilationUnit.getLineNumber(node.getStartPosition()) - 1;
        String lineComment = source[startLineNumber].trim();
        comments.add(lineComment);
      //  System.out.println(lineComment);

        return true;
    }

    public boolean visit(BlockComment node) {

    	//System.out.println("GOT COME2");
    	
        int startLineNumber = compilationUnit.getLineNumber(node.getStartPosition()) - 1;
        int endLineNumber = compilationUnit.getLineNumber(node.getStartPosition() + node.getLength()) - 1;

        StringBuffer blockComment = new StringBuffer();

        for (int lineCount = startLineNumber ; lineCount<= endLineNumber; lineCount++) {

            String blockCommentLine = source[lineCount].trim();
            blockComment.append(blockCommentLine);
            if (lineCount != endLineNumber) {
                blockComment.append("\n");
            }
        }

       // System.out.println(blockComment.toString());

        comments.add(blockComment.toString());
        
        return true;
    }
    
    public List<String> getComment(){
    	return comments;
    }
    
   
	
}
