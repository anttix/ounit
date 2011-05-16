package com.googlecode.ounit.opaque;

/*
 * API CLASS: This class is used in SOAP returns and should not be altered  
 * 
 * Contains pieces of code from OpenMark online assessment system
 * SVN r437 (2011-04-21)
 *  Copyright (C) 2007 The Open University, Licensed under GPLv2+
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