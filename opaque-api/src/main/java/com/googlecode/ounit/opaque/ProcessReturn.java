/*
 * OUnit - an OPAQUE compliant framework for Computer Aided Testing
 *
 * Copyright (C) 2010, 2011  Antti Andreimann
 *
 * This file is part of OUnit.
 *
 * OUnit is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OUnit is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OUnit.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contains pieces of code from OpenMark online assessment system
 * SVN r437 (2011-04-21)
 * 
 * Copyright (C) 2007 The Open University
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package com.googlecode.ounit.opaque;

/*
 * API CLASS: This class is used in SOAP returns and should not be altered  
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