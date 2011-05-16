package com.googlecode.ounit.opaque;

/*
 * API CLASS: This class is used in SOAP returns and should not be altered  
 * 
 * Contains pieces of code from OpenMark online assessment system
 * SVN r437 (2011-04-21)
 *  Copyright (C) 2007 The Open University, Licensed under GPLv2+
 */
/**
 * Returned data from {@link OpaqueService#start(String,String,String,String[],String[],String[])} call.
 * 
 */
public class StartReturn extends ReturnBase {
	private String questionSession;

	public StartReturn() {
	}
	
	public StartReturn(String questionSession) {
		super();
		this.questionSession = questionSession;
	}

	/** @return Question session ID.
	 * (Not a user session ID! Used to refer to the period between a start() call
	 * and the end of the question or stop().) */
	public String getQuestionSession() { return questionSession; }
	
	public void setQuestionSession(String questionSession) {
		this.questionSession = questionSession;
	}
}
