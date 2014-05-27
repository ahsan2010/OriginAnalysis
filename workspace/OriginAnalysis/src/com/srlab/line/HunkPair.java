package com.srlab.line;

public class HunkPair {
	private Hunk leftHunk;
	private Hunk rightHunk;
	
	private int changeType;
	
	public HunkPair(Hunk _leftHunk, Hunk _rightHunk, int _changeType){
		this.leftHunk = _leftHunk;
		this.rightHunk = _rightHunk;
		this.changeType = _changeType;
	}

	public Hunk getLeftHunk() {
		return leftHunk;
	}

	public Hunk getRightHunk() {
		return rightHunk;
	}

	public int getChangeType() {
		return changeType;
	}
	
}
