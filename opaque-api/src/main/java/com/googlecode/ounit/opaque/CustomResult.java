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
 * Custom question result (ignored by test navigator system but may be used in
 * custom interpretation of results).
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
