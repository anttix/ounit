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

import org.apache.wicket.Session;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Response;
import org.apache.wicket.session.ISessionStore;
import org.apache.wicket.util.IProvider;

import com.googlecode.ounit.opaque.OpaqueException;

public abstract class OpaqueApplication extends WebApplication {
	private OpaqueSessionStore sessionStore;
	
	/**
	 * Fetch question from question database.
	 * This function is called from getQuestionMetadata and start to
	 * make sure the question exists.
	 * 
	 * @param questionID
	 * @param questionVersion
	 * @param questionBaseURL
	 * @return
	 */
	public abstract OpaqueQuestion fetchQuestion(String id, String version,
			String baseUrl) throws OpaqueException;

	public int getActiveSessions() {
		if(sessionStore != null) {
			return sessionStore.getActiveSessionCount();
		} else {
			return -1; // Unknown
		}
	}
	
	public void setSessionStore(OpaqueSessionStore sessionStore) {
		this.sessionStore = sessionStore; 
	}
	
	@Override
	protected void init() {
		super.init();
		if (sessionStore != null) {
			/* Set session store provider */
			setSessionStoreProvider(new IProvider<ISessionStore>() {
				@Override
				public ISessionStore get() {
					return sessionStore;
				}
			});
		}
	}
	
	@Override
	public String getInitParameter(String key) {
		return null;
	}

	@Override
	public Session newSession(Request request, Response response) {
		return new OpaqueSession(request);
	}
}
