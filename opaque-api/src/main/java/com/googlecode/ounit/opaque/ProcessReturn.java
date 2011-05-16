package com.googlecode.ounit.opaque;

/*
 * API CLASS: This class is used in SOAP returns and should not be altered  
 * 
 * Contains pieces of code from OpenMark online assessment system
 * SVN r437 (2011-04-21)
 *  Copyright (C) 2007 The Open University, Licensed under GPLv2+
 */

/**
 * Returned data from {@link OmService#process(java.lang.String, java.lang.String[], java.lang.String[])} call.
 */

public class ProcessReturn extends ReturnBase {
	private boolean questionEnd = false;
	private Results results;

	/**
	 * @return True if the question has now ended and the test navigator should
	 * proceed to show a new question.
	 */
	public boolean isQuestionEnd() { return questionEnd; }
	
	/**
	 * @return Results from this question if provided this time; null if
	 *   not provided. (Results are sometimes provided before the end of the
	 *   question, if the last page shows answers etc.)
	 */
	public Results getResults() { return results; }

	/**
	 * @param questionEnd {@link #isQuestionEnd()}
	 */
	public void setQuestionEnd(boolean questionEnd) {
		this.questionEnd = questionEnd;
	}
	
	/**
	 * @param results {@link #getResults()}
	 */
	public void setResults(Results results) {
		this.results = results;
	}

}