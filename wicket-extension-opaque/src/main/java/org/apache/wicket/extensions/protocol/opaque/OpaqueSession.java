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

package org.apache.wicket.extensions.protocol.opaque;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.Application;
import org.apache.wicket.MetaDataKey;
import org.apache.wicket.Session;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import com.googlecode.ounit.opaque.OpaqueException;
import com.googlecode.ounit.opaque.Results;

public class OpaqueSession {
	public final static String SESSION_PARAMETER_NAME = "opaque:wicket:" + Session.SESSION_ATTRIBUTE_NAME;
	public final static String PAGE_PARAMETER_NAME = "wicketpage";
	public final static String MOODLE_EVENT_PARAMETER_NAME = "event";
	public final static int DEFAULT_MARKS = 10;
	
	String wicketPage;
	String wicketSessionId;
	String engineSessionId;
	
	PageParameters initialParams;
	PageParameters postParameters;
	List<String> cachedResources = new ArrayList<String>();
	int maxMarks = DEFAULT_MARKS;
	double score = 0;
	boolean closed;
	Results results;
	
	/** meta data for session data */
	private static final MetaDataKey<OpaqueSession> OPAQUE_SESSION_KEY = new MetaDataKey<OpaqueSession>()
	{
		private static final long serialVersionUID = 1L;
	};
	
	public static OpaqueSession get() {
		return Application.get().getMetaData(OPAQUE_SESSION_KEY);
	}
	
	public static void set(OpaqueSession session) {
		Application.get().setMetaData(OPAQUE_SESSION_KEY, session);
	}
	
	public static void detach() {
		Application.get().setMetaData(OPAQUE_SESSION_KEY, null);
	}
	
	// TODO: This has a potential to consume up a lot of memory so it's not enabled
	// until the real need surfaces.
	//LinkedList<PageParameters> history = new LinkedList<PageParameters>();
	
	public OpaqueSession(String[] initialParamNames, String[] initialParamValues)
			throws OpaqueException {
		
		initialParams = arraysToParameters(initialParamNames, initialParamValues);
	}
	
	private PageParameters arraysToParameters(String[] names, String[] values)
			throws OpaqueException {
		
		PageParameters rv = new PageParameters();
		if(values.length != values.length)
			throw new OpaqueException("The count of parameter names and values does not match");

		for(int i = 0; i <  names.length; i++) {
			rv.add(names[i], values[i]);
		}
		return rv;
	}
	
	public String getWicketPage() {
		return wicketPage;
	}
	
	public void setWicketPage(String wicketPage) {
		this.wicketPage = wicketPage;
	}
	
	public String getWicketSessionId() {
		return wicketSessionId;
	}
	
	public void setWicketSessionId(String wicketSessionId) {
		this.wicketSessionId = wicketSessionId;
	}
	
	public List<String> getCachedResources() {
		return cachedResources;
	}

	public void setCachedResources(List<String> cachedResources) {
		this.cachedResources = cachedResources;
	}
	
	public void setCachedResources(String [] cachedResources) {
		this.cachedResources.clear();
		for(String r : cachedResources) {
			this.cachedResources.add(r);
		}
	}

	public void newPostParameters(String[] names, String[] values) throws OpaqueException {
		postParameters = arraysToParameters(names, values);
		
		setWicketPage(postParameters.get(PAGE_PARAMETER_NAME).toString());
		setWicketSessionId(postParameters.get(SESSION_PARAMETER_NAME).toString());
		postParameters.remove(PAGE_PARAMETER_NAME);
		postParameters.remove(SESSION_PARAMETER_NAME);
		postParameters.remove(MOODLE_EVENT_PARAMETER_NAME);
		
		// TODO: This has a potential to consume up a lot of memory so it's not enabled
		// until the real need surfaces.
		//history.add(postParameters);
	}

	public PageParameters getPostParameters() {
		return postParameters;
	}
	
	public boolean isClosed() {
		return closed;
	}
	
	public void setClosed(boolean closed) {
		this.closed = closed;
	}
	
	public int getMaxMarks() {
		return maxMarks;
	}
	
	public void setMaxMarks(int marks) {
		this.maxMarks = marks;
	}
	
	public String getEngineSessionId() {
		return engineSessionId;
	}
	
	public void setEngineSessionId(String engineSessionId) {
		this.engineSessionId = engineSessionId;
	}
	
	public Results getResults() throws OpaqueException {
		if(results == null) {
			results = new Results();
		}
		
		results.addScore(getMarks(), Results.ATTEMPTS_UNSET);

		return results;
	}
	
	public void setResults(Results results) {
		this.results = results;
	}
	
	/**
	 * Final score in percent (0-100). Default mark will be calculated from this:
	 * marks = score / 100 * maxMarks
	 * 
	 * @return
	 */
	public double getScore() {
		return score;
	}
	
	public void setScore(double score) {
		this.score = score;
	}
	
	/**
	 * Calculate final (default) marks from score.
	 * @return score / 100 * maxMarks
	 */
	public int getMarks() {
		return (int)Math.round(getScore() / 100 * getMaxMarks());
	}
}
