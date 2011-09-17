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

import static com.googlecode.ounit.OunitConfig.MARKS_PROPERTY;
import static com.googlecode.ounit.OunitConfig.TITLE_PROPERTY;

import java.io.File;
import java.util.Properties;

import com.googlecode.ounit.opaque.QuestionInfo;

public abstract class QuestionBase implements OunitQuestion {
	protected String id;
	protected String version;
	protected String baseURL;
	protected String revision;
	protected QuestionInfo info;
	protected File srcDir;
	
	public QuestionBase(String id, String version, String baseURL) {
		this.id = id;
		this.version = version;
		this.baseURL = baseURL;
		
		assert id != null && !id.isEmpty() : "Missing ID";
		assert version != null && !version.isEmpty() : "Missing version";
		assert baseURL != null && !baseURL.isEmpty() : "Missing BaseURL";
		
		this.revision = findHeadRevision();

		Properties qprops = OunitApplication.getModelProperties(getSrcDir());
		int maxScore;
		try {
			maxScore = Integer.parseInt((String) qprops.get(MARKS_PROPERTY));
		} catch (Exception e) {
			maxScore = OunitSession.DEFAULT_MARKS;
		}
		
		info = new QuestionInfo();		
		info.setMaxScore(maxScore);
		
		/* Moodle currently does not display it, but we handle it anyway */
		info.setTitle((String) qprops.get(TITLE_PROPERTY));
	}
	
	@Override
	public String getId() {
		return id;
	}
	
	@Override
	public String getVersion() {
		return version;
	}
	
	@Override
	public String getBaseUrl() {
		return baseURL;
	}
	
	@Override
	public QuestionInfo getInfo() {
		return info;
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
