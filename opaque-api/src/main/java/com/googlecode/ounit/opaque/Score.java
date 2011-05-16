/*
 * API CLASS: This class is used in SOAP returns and should not be altered  
 * 
 * Contains pieces of code from OpenMark online assessment system
 * SVN r437 (2011-04-21)
 *  Copyright (C) 2007 The Open University, Licensed under GPLv2+
 */
package com.googlecode.ounit.opaque;

/** Single score */
public class Score
{
	public Score() {
	}
	
	/**
	 * Create a score
	 * @param sAxis the axis.
	 * @param iMarks the score on that axis.
	 */
	public Score(String axis,int marks)
	{
		this.axis=axis;
		this.marks=marks;
	}
	
	private int marks;
	private String axis;

	/** @return Score axis (null for default) */
	public String getAxis() { return axis; }

	/** @return Number of marks achieved for question (or, maximum for this axis,
	 * in that context) */
	public int getMarks() { return marks; }

	public void setMarks(int marks) {
		this.marks = marks;
	}
	public void setAxis(String axis) {
		this.axis = axis;
	}
}