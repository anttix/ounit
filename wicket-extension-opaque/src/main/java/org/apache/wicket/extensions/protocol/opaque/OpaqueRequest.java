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

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.Cookie;

import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.request.IRequestParameters;
import org.apache.wicket.request.Url;
import org.apache.wicket.request.http.WebRequest;
import org.apache.wicket.request.parameter.EmptyRequestParameters;
import org.apache.wicket.util.lang.Args;
import org.apache.wicket.util.time.Time;

public class OpaqueRequest extends WebRequest {
	public final static String PAGE_PARAMETER_NAME = "wicketpage";
	public final static String MOODLE_EVENT_PARAMETER_NAME = "event";
	public final static String MOODLE_FINISH_PARAMETER_NAME = "-finish";
	public final static String MOODLE_SPECIAL_PARAMETER_PREFIX = "-";

	public enum CallType {
		START, PROCESS, OTHER
	};
	
	protected String sessionId;
	protected Url url;
	protected CallType callType = CallType.OTHER;
	
	protected OpaqueRequestParameters postParameters;
	protected OpaqueQuestion question;
	protected List<String> cachedResources;

	public OpaqueRequest(String sessionId, Url url) {
		this.sessionId = sessionId;
		this.url = url;
	}	

	public OpaqueRequest(OpaqueQuestion question, String[] initialParamNames,
			String[] initialParamValues, String[] cachedResources) {

		Args.notNull(question, "question");

		this.callType = CallType.START;
		this.question = question;
		this.cachedResources = Arrays.asList(cachedResources);

		// TODO: Do something with the initial parameters
		
		setUrl("");
	}
	
	public OpaqueRequest(String sessionId, String[] names, String[] values) {
		Args.notEmpty(sessionId, "sessionId");
		
		this.callType = CallType.PROCESS;
		this.sessionId = sessionId;
		
		/* Parse parameters */
		if(values.length != values.length)
			throw new WicketRuntimeException(
					"The count of parameter names and values does not match");
		
		postParameters = new OpaqueRequestParameters();
		
		for(int i = 0; i <  names.length; i++) {
			/* Find "special" parameters */
			if(names[i].equals(MOODLE_EVENT_PARAMETER_NAME)) {
				// TODO: Do something with it (eg. detect replay)
				continue;
			}
			if(names[i].startsWith(MOODLE_SPECIAL_PARAMETER_PREFIX)) {
				if(getUrl() == null)
					setUrl(""); // Let HomePage handle the request
				
				getUrl().addQueryParameter(names[i], values[i]);
				continue;
			}
			
			if(names[i].equals(PAGE_PARAMETER_NAME)) {
				String pageUrl = values[i];
				
				// FIXME: This is a seriously ugly temporary hack!
				if(!pageUrl.startsWith("?") && !pageUrl.startsWith("wicket/")) {
					if(pageUrl.startsWith("page?"))
						pageUrl = "wicket/" + pageUrl;
					else
						pageUrl = "wicket/bookmarkable/" + pageUrl;
				}
				
				setUrl(pageUrl);
				continue;
			}
			
			/* Parameter not recognized, must belong to the page */
			postParameters.add(names[i], values[i]);
		}
		
		if (getUrl() == null)
			throw new WicketRuntimeException(PAGE_PARAMETER_NAME
					+ " not present in POST parameters");
	}

	public CallType getCallType() {
		return callType;
	}

	public void setUrl(Url url) {
		this.url = url;
	}
	
	public void setUrl(String url) {
		this.url = Url.parse(url, getCharset());
	}

	public String getSessionId() {
		return sessionId;
	}
	
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public List<String> getCachedResources() {
		return cachedResources;
	}

	@Override
	public IRequestParameters getPostParameters() {
		if(postParameters == null)
			return EmptyRequestParameters.INSTANCE;
		else
			return postParameters;
	}

	@Override
	public List<Cookie> getCookies() {
		return null;
	}

	@Override
	public List<String> getHeaders(String name) {
		return null;
	}

	@Override
	public String getHeader(String name) {
		return null;
	}

	@Override
	public Time getDateHeader(String name) {
		return null;
	}

	@Override
	public Url getUrl() {
		return url;
	}

	@Override
	public Url getClientUrl() {
		// We want Wicket to always generate absolute URLs (wicket/...)
		return new Url();
	}

	@Override
	public Locale getLocale() {
		// FIXME: Derive it from language passed to the start call
		return Locale.getDefault();
	}

	@Override
	public Charset getCharset() {
		return Charset.forName("UTF-8");
	}

	@Override
	public Object getContainerRequest() {
		return null;
	}

	public OpaqueQuestion getQuestion() {
		return question;
	}	
}
