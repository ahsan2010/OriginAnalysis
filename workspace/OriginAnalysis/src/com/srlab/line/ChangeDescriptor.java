package com.srlab.line;

import java.util.ArrayList;

public class ChangeDescriptor {
	
	public static final int LINE_ADDED   = 0;
	public static final int LINE_DELETED = 1;
	public static final int LINE_CHANGED = 2;
	
	public ArrayList<HunkPair> changes;

	public ArrayList<HunkPair> getChanges() {
		return changes;
	}
	public ChangeDescriptor(Diff.change script, ArrayList<String> org, ArrayList<String> rev){
		this.changes = new ArrayList<HunkPair>();
        while(script!=null){
        	
         	if(script.deleted!=0 && script.inserted==0){
         		int startLine = script.line0;
                int length = script.deleted;
                Hunk hunk = new Hunk(startLine,startLine+length-1,new ArrayList<String>(org.subList(startLine, startLine+length)));
                HunkPair hp = new HunkPair(hunk,null,ChangeDescriptor.LINE_DELETED);
                this.changes.add(hp);
         	}
         	else if(script.deleted==0 && script.inserted!=0){
        		int startLine = script.line1;
                int length = script.inserted;
                Hunk hunk = new Hunk(startLine,startLine+length-1,new ArrayList<String>(rev.subList(startLine, startLine+length)));
                HunkPair hp = new HunkPair(null,hunk,ChangeDescriptor.LINE_ADDED);
                this.changes.add(hp);
                
         	}
         	else {
         		int lStartLine = script.line0;
                int lLength = script.deleted;
                Hunk leftHunk = new Hunk(lStartLine,lStartLine+lLength-1,new ArrayList<String>(org.subList(lStartLine, lStartLine+lLength)));
                
                int rStartLine = script.line1;
                int rLength = script.inserted;
                Hunk rightHunk = new Hunk(rStartLine,rStartLine+rLength-1,new ArrayList<String>(rev.subList(rStartLine, rStartLine+rLength)));
                HunkPair hp = new HunkPair(leftHunk,rightHunk,ChangeDescriptor.LINE_CHANGED);
                this.changes.add(hp);
         	}
            script = script.link;
        }
	}
	
}



