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
 * Abstract public class for results returned from OPAQUE
 * start and process commands.
 */
abstract public class ReturnBase {
	private String XHTML;
	private String CSS;
	private String progressInfo;
	private Resource[] resources;
	private String head;

	/**
	 * Obtains XHTML content. XHTML content must:
	 * <ul>
	 * <li> Be well-formed, with single root element</li>
	 * <li> Be suitable for placing within &lt;body&gt;</li>
	 * <li> Not use named entities except the basic XML set.</li>
	 * </ul>
	 * The following placeholder strings may be included in the content and
	 * will be replaced by the test navigator:
	 * <table border="1">
	 * <tr><th>Placeholder</th><th>Replacement</th></tr>
	 * <tr><td>%%RESOURCE%%</td><td>Path [relative or absolute] at which
	 *   resources will become available. This should not include the
	 *   terminating /.<br>Example: If a resource has the name myfile.png,
	 *   then &lt;img src="%%RESOURCES%%/myfile.png"/&gt; should work to include
	 *   that image.</td></tr>
	 * <tr><td>%%IDPREFIX%%</td><td>must be put at the start of all id and name attributes
	 * in the XHTML (and references to them in Javascript.</td></tr>
	 * </table>
	 * @return XHTML content as string.
	 */
	public String getXHTML() { return XHTML; }

	/** @return CSS file. Null if none is required. */
	public String getCSS() { return CSS; }

	/** @return Resource files that should be made available */
	public Resource[] getResources() {
		
		// Ugly hack to combat this PHP nonsense http://bugs.php.net/bug.php?id=36226
		// FIXME: Remove when Moodle qtype_opaque is fixed
		if(resources != null && resources.length == 1) {
			Resource [] rv = { resources[0], resources[0] }; 
			return rv;
		}
		
		return resources;
	}

	/**
	 * @return Short textual information of progress on question, which should
	 *   be displayed alongside the question. (For example, this might indicate
	 *   how many attempts at the question are remaining.)
	 *   Null indicates no change from previously-returned info (or blank if this
	 *   is the first time).
	 */
	public String getProgressInfo() { return progressInfo; }
	
	/**
	 * @return A block of HTML that should be included in page header.
	 *   This is not supported by OpenMark, however it IS implemented in
	 *   Moodles OPAQUE question type.
	 */
	public String getHead() { return head; }
	
	/**
	 * @param xHTML {@link #getXHTML()}
	 */
	public void setXHTML(String xHTML) {
		XHTML = xHTML;
	}

	/**
	 * @param cSS {@link #getCSS()}
	 */
	public void setCSS(String cSS) {
		CSS = cSS;
	}

	/**
	 * @param progressInfo {@link #getProgressInfo()}
	 */
	public void setProgressInfo(String progressInfo) {
		this.progressInfo = progressInfo;
	}

	/**
	 * @param resources {@link #getResources()}
	 */
	public void setResources(Resource[] resources) {
		this.resources = resources;
	}
	
	public String setHead(String head) {
		return this.head = head;
	}
}