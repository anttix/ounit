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

import java.util.Collection;
import java.util.HashMap;

/*
 * API CLASS: This class is used in SOAP returns and should not be altered  
 */

/** Question results (scores)
 */
public class Results
{
	/** Question summary */
	private String questionLine=null;

	/** Answer summary */
	private String answerLine=null;

	/** Summary of actions */
	private String actionSummary=null;

	/** Stores information about the number of attempts taken to get question right (1=first time) */
	private int attempts=ATTEMPTS_UNSET;

	/** User passed on question */
	public final static int ATTEMPTS_PASS=0;

	/** User got question wrong after all attempts */
	public final static int ATTEMPTS_WRONG=-1;

	/** User got question partially correct after all attempts */
	public final static int ATTEMPTS_PARTIALLYCORRECT=-2;

	/** If developer hasn't set the value */
	public final static int ATTEMPTS_UNSET=-99;

	/** Hashmap of all scores based on axis (String -> Score) */
	private HashMap<String, Score> hmScores=new HashMap<String, Score>();

	/** Hashmap of custom results (String [id] -> CustomResult) */
	private HashMap<String, CustomResult> hmCustomResults=new HashMap<String, CustomResult>();

	/** @return One-line summary of question (may be null) */
	public String getQuestionLine() { return questionLine; }
	/** @return One-line summary of answer (may be null) */
	public String getAnswerLine() { return answerLine; }
	/** @return Summary of user actions (may be null) */
	public String getActionSummary() { return actionSummary; }
	/**
	 * @return Number of attempts taken to get correct answer; 1=first attempt.
	 *   ATTEMPTS_PASS(0)=user passed, ATTEMPTS_WRONG(-1)=user failed all attempts,
	 *   ATTEMPTS_UNSET(-99)=value has not been set
	 */
	public int getAttempts() { return attempts; }

	/**
	 * Sets the one-line question summary.
	 * <p>
	 * Example: 'Select 5 equations equivalent to 4n.'
	 * <p>
	 * Where possible, the summary should define the specific question asked.
	 * However, some questions may be impossible to accurately summarise in
	 * a single line.
	 * @param sText One-line question summary
	 * @throws OpaqueException If sText is longer than 255 characters
	 */
	public void setQuestionLine(String sText) throws OpaqueException
	{
		if(sText.length() > 255) throw new OpaqueException(
			"Question line must be <= 255 characters long");
		questionLine=sText;
	}

	/**
	 * Sets the one-line answer summary.
	 * <p>
	 * Example: 'Mean 0.45, Median 0, Mode 1'
	 * <p>
	 * Where possible, the summary should define the specific answer the user
	 * gave. In some cases this may be impossible, for reasons of length or
	 * the text-only format.
	 * @param sText One-line answer summary
	 * @throws OpaqueException If sText is longer than 255 characters
	 */
	public void setAnswerLine(String sText) throws OpaqueException
	{
		if(sText.length() > 255) throw new OpaqueException(
			"Answer line must be <= 255 characters long");
		answerLine=sText;
	}
	
	/**
	 * @see #appendActionSummary(String)
	 * @param actionSummary
	 */
	public void setActionSummary(String actionSummary) {
		this.actionSummary = actionSummary;
	}

	/**
	 * Appends text to the action summary. This is a longer summary of the
	 * user's actions, intended for admin view only. Supposing they get a
	 * question right on the third try, this would typically include the
	 * first and second answers too. It is not restricted in length (though
	 * shouldn't be ridiculous) and may contain any other information about the
	 * question that should be recorded for easy access by admin staff.
	 * @param sText Additional information that will be appended to the action
	 *   summary. (You don't need to end with whitespace; a newline is
	 *   automatically included at the end of each entry.)
	 */
	public void appendActionSummary(String sText)
	{
		if(actionSummary==null) actionSummary="";
		actionSummary+=sText+"\n";
	}

	/**
	 * Sets the question numerical result (on default score axis).
	 * @param iMarks Marks obtained
	 * @param iAttempts Attempts taken to get result, or an ATTEMPTS_xx constant
	 * @throws OpaqueException
	 */
	/*
	 * "Set" methods with no corresponding getter will somehow confuse Axis2
	 * and the result is a null pointer exception during the encoding phase.
	 * Therefore we must use "add" here.
	 * 
	 * Anttix 2011-05-11
	 */
	public void addScore(int iMarks, int iAttempts) throws OpaqueException
	{
	  this.attempts=iAttempts;
		addScore(null,iMarks);
	}

	/**
	 * Sets the question numerical result (on specified score axis). If a result
	 * has already been set in a particular axis, that result is replaced. When
	 * calling this method, be sure to set the attempts value as well.
	 * @param sAxis Score axis ID (null = default)
	 * @param iMarks Marks obtained
	 * @throws OpaqueException If axis name is not a valid ID
	 */
	public void addScore(String sAxis,int iMarks) throws OpaqueException
	{
		checkID(sAxis);
		hmScores.put(sAxis,new Score(sAxis,iMarks));
	}

	/**
	 * Sets the number of attempts taken to get question right. Must be called
	 * if setting score on individual axes; can be set as part of setScore
	 * for single-axis results.
	 * @param iAttempts 1 = user got question
	 *   right on first attempt, etc.; or use an ATTEMPTS_xx constant
	 */
	public void setAttempts(int iAttempts)
	{
		this.attempts=iAttempts;
	}

	private void checkID(String sID) throws OpaqueException
	{
		if(sID==null) return;
		if(!sID.matches("[-_.!A-Za-z0-9]*"))
			throw new OpaqueException("Not a valid ID string (disallowed characters): "+sID);
		if(sID.length()>64)
			throw new OpaqueException("Not a valid ID string (>64 characters): "+sID);
	}

	/**
	 * Sets a custom question result. Custom results are not interpreted by the
	 * test navigator but may be analysed in later processing.
	 * @param sName Name (ID) for result
	 * @param sValue Value of result
	 * @throws OpaqueException If name isn't a valid ID string
	 */
	public void addCustomResult(String sName, String sValue) throws OpaqueException
	{
		checkID(sName);
		hmCustomResults.put(sName,new CustomResult(sName,sValue));
	}

	/**
	 * @return All question scores that have been set.
	 */
	public Score [] getScores()
	{
		Collection<Score> s = hmScores.values();
		
		// Ugly hack to combat this PHP nonsense http://bugs.php.net/bug.php?id=36226
		// TODO: Remove when Moodle qtype_opaque is fixed
		if(s.size() == 1) {
			Score [] rv = s.toArray(new Score[s.size()+1]);
			rv[1] = rv[0];
			return rv;
		}
		
		return s.toArray(new Score[s.size()]);
		
		//return hmScores.values().toArray(
		//	new Score[hmScores.values().size()]);
	}
	
	public void setScores(Score [] scores) {
		throw new UnsupportedOperationException("Not implemented");
	}
	
	/**
	 * @return All custom results.
	 */
	public CustomResult [] getCustomResults()
	{
		return hmCustomResults.values().toArray(
			new CustomResult[hmCustomResults.values().size()]);
	}
	
	public void setCustomResults(CustomResult [] customResults) {
		throw new UnsupportedOperationException("Not implemented");
	}
}
