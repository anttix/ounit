/*
 * API CLASS: This class is used in SOAP returns and should not be altered  
 * 
 * Contains pieces of code from OpenMark online assessment system
 * SVN r437 (2011-04-21)
 *  Copyright (C) 2007 The Open University, Licensed under GPLv2+
 */

package com.googlecode.ounit.opaque;

/**
 * Custom question result (ignored by test navigator system but may be used in
 * custom interpretation of results).
 * <p>
 * API CLASS: This class is used in SOAP returns and should probably not be
 * altered (after initial release).
 */
public class CustomResult {
	/** Name (ID) of result */
	private String name;
	/** Value of result */
	private String value;

	public CustomResult() {

	}

	/**
	 * @param sName
	 *            Name (ID) of result
	 * @param sValue
	 *            Value of result
	 */
	public CustomResult(String name, String value) {
		this.name = name;
		this.value = value;
	}

	/** @return Name (ID) of custom result */
	public String getName() {
		return name;
	}

	/** @return Value of custom result */
	public String getValue() {
		return value;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
