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

/** Represents a resource file provided by a question */
public class Resource
{
	/** Resource filename */
	private String filename;
	/** Resource MIME type */
	private String mimeType;
	/** Resource character encoding if appropriate */
	private String encoding;
	/** Resource content */
	private byte[] content;

	public Resource() {
	}

	/**
	 * Stores the three pieces of information together.
	 * @param sFilename Resource filename
	 * @param sMimeType Resource MIME type
	 * @param sEncoding Character encoding
	 * @param abContent Resource content
	 */
	public Resource(String sFilename,String sMimeType,String sEncoding,byte[] abContent)
	{
		this.filename=sFilename;
		this.mimeType=sMimeType;
		this.encoding=sEncoding;
		this.content=abContent;
	}

	/**
	 * Stores the three pieces of information together.
	 * @param sFilename Resource filename
	 * @param sMimeType Resource MIME type
	 * @param abContent Resource content
	 */
	public Resource(String sFilename,String sMimeType,byte[] abContent)
	{
		this.filename=sFilename;
		this.mimeType=sMimeType;
		this.content=abContent;
	}

	/** @return Resource filename */
	public String getFilename() { return filename; }
	/** @return Resource MIME type */
	public String getMimeType() { return mimeType; }
	/** @return Character encoding (null if not a text type) */
	public String getEncoding() { return encoding; }
	/** @return Resource content */
	public byte[] getContent() { return content; }
	
	public void setFilename(String filename) {
		this.filename = filename;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public void setContent(byte[] content) {
		this.content = content;
	}
}