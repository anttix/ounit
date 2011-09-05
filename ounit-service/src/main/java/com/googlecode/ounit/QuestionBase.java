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
 */

package com.googlecode.ounit;

import java.io.File;

public abstract class QuestionBase implements OunitQuestion {
	String id;
	String version;
	String baseURL;
	String revision;
	File srcDir;
	
	public QuestionBase(String id, String version, String baseURL) {
		this.id = id;
		this.version = version;
		this.baseURL = baseURL;
		
		assert id != null && !id.isEmpty() : "Missing ID";
		assert version != null && !version.isEmpty() : "Missing version";
		assert baseURL != null && !baseURL.isEmpty() : "Missing BaseURL";
		
		this.revision = findHeadRevision();
	}
	
	/* (non-Javadoc)
	 * @see com.googlecode.ounit.OunitQuestion#getId()
	 */
	@Override
	public String getId() {
		return id;
	}
	
	/* (non-Javadoc)
	 * @see com.googlecode.ounit.OunitQuestion#getVersion()
	 */
	@Override
	public String getVersion() {
		return version;
	}
	
	/* (non-Javadoc)
	 * @see com.googlecode.ounit.OunitQuestion#setVersion()
	 */
	@Override
	public void setVersion(String version) {
		// TODO Auto-generated method stub
		
	}
	
	/* (non-Javadoc)
	 * @see com.googlecode.ounit.OunitQuestion#getBaseURL()
	 */
	@Override
	public String getBaseURL() {
		return baseURL;
	}
		
	/* (non-Javadoc)
	 * @see com.googlecode.ounit.OunitQuestion#getRevision()
	 */
	@Override
	public String getRevision() {
		return revision;
	}
	
	/* (non-Javadoc)
	 * @see com.googlecode.ounit.OunitQuestion#setRevision(java.lang.String)
	 */
	@Override
	public void setRevision(String revision) {
		if (revision != null && !revision.equals(this.revision)) {
			this.revision = revision;
			srcDir = null;
		}
	}
	
	/* (non-Javadoc)
	 * @see com.googlecode.ounit.OunitQuestion#getSrcDir()
	 */
	@Override
	public File getSrcDir() {
		if(srcDir == null)
			fetchQuestion();

		return srcDir;
	}
	
	/**
	 * Find latest revision for question in Database.
	 * 
	 * @return
	 */
	abstract protected String findHeadRevision();
	
	/**
	 * Fetch question from Question Database.
	 */
	abstract protected void fetchQuestion();
}
