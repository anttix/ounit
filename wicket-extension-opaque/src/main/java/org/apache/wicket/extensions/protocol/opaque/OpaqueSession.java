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
import java.util.Collections;
import java.util.List;

import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.extensions.protocol.opaque.OpaqueRequest.CallType;
import org.apache.wicket.protocol.http.WebSession;
import org.apache.wicket.request.Request;

import com.googlecode.ounit.opaque.OpaqueException;
import com.googlecode.ounit.opaque.Results;

public class OpaqueSession extends WebSession {
	private static final long serialVersionUID = 1L;
	
	public final static int DEFAULT_MARKS = 10;
	
	protected OpaqueQuestion question;
	protected List<String> cachedResources;
	protected int maxMarks = DEFAULT_MARKS;
	protected double score = 0;
	protected boolean closed;

	// TODO: This has a potential to consume up a lot of memory so it's not enabled
	// until the real need surfaces.
	//LinkedList<PageParameters> history = new LinkedList<PageParameters>();
	
	public OpaqueSession(Request request) {
		super(request);

		if (request instanceof OpaqueRequest) {
			OpaqueRequest rq = (OpaqueRequest) request;

			if (rq.getCallType() != CallType.START)
				throw new IllegalArgumentException(
						"New sessions must be created from START requests." +
						"Stale session ID?");

			this.question = rq.getQuestion();

			cachedResources = new ArrayList<String>(rq.getCachedResources());
			
			bind(); // Make sure session ID is generated!
		} else {
			// TODO: Remove the exception when it becomes possible to run
			// OpaqueApps with the normal WicketFilter
			// (for testing purposes)
			throw new WicketRuntimeException(
					"Opaque application only works with OpaqueRequest");
		}
	}

	public OpaqueQuestion getQuestion() {
		return question;
	}

	public List<String> getCachedResources() {
		return Collections.unmodifiableList(cachedResources);
	}

	void setCachedResources(List<String> cachedResources) {
		this.cachedResources = cachedResources;
		dirty();
	}
		
	void addCachedResource(String resource) {
		this.cachedResources.add(resource);
		dirty();
	}
	
	public boolean isClosed() {
		return closed;
	}
	
	public void setClosed(boolean closed) {
		this.closed = closed;
		dirty();
	}
	
	public int getMaxMarks() {
		return maxMarks;
	}
	
	public void setMaxMarks(int marks) {
		this.maxMarks = marks;
		dirty();
	}
	
	/**
	 * Build an OPAQUE results object from the session data. Override this
	 * method in your own session if you wish to pass more details back to the LMS.
	 * 
	 * @return a new results object containing only a single score returned
	 *         by {@link #getMarks()}
	 * @throws OpaqueException
	 */

	public Results getResults() throws OpaqueException {
		Results results = new Results();
		results.addScore(getMarks(), Results.ATTEMPTS_UNSET);

		return results;
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
		dirty();
	}
	
	/**
	 * Calculate final (default) marks from score.
	 * @return score / 100 * maxMarks
	 */
	public int getMarks() {
		return (int)Math.round(getScore() / 100 * getMaxMarks());
	}
}
