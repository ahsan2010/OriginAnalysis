package com.marge.split;

public class MatchLines {
	
	public int rev1LinePosition ;
	public int rev2LinePosition ;
	
	public MatchLines (int a , int b) {
		rev1LinePosition = a ;
		rev2LinePosition = b ;
	}
	
	public int getRev1LinePosition () {
		return this.rev1LinePosition;
	}
	public int getRev2LinePosition () {
		return this.rev2LinePosition;
	}
}
