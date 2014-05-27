package com.srlab.line;

import java.util.ArrayList;

public class Hunk {
	int startLine;
	int endline;
	int nlines;
	ArrayList<String> lines;
	
	public Hunk(int _startLine, int _endLine,ArrayList<String> _lines){
		this.startLine = _startLine;
		this.endline = _endLine;
		this.lines = _lines;
	}
	public int getStartLine() {
		return startLine;
	}
	public int getEndline() {
		return endline;
	}
	public ArrayList<String> getLines() {
		return lines;
	}
	public void print(){
		int nline = startLine;
		for(String string:this.getLines()){
			System.out.println("["+nline +"]"+lines.get(nline-startLine));
			nline++;
		}
	}
}
