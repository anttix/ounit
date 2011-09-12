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
import java.util.List;
import java.util.Locale;

import javax.servlet.http.Cookie;

import org.apache.wicket.request.IRequestParameters;
import org.apache.wicket.request.Url;
import org.apache.wicket.request.http.WebRequest;
import org.apache.wicket.request.parameter.EmptyRequestParameters;
import org.apache.wicket.util.time.Time;

public class OpaqueRequest extends WebRequest {
	Url url;
	IRequestParameters postParameters;
		
	public void setUrl(Url url) {
		this.url = url;
	}
	
	public void setUrl(String url) {
		this.url = Url.parse(url, getCharset());
	}
	
	public void setPostParameters(IRequestParameters postParameters) {
		this.postParameters = postParameters;
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
		// OPAQUE can not support Ajax requests so both URLs always match
		//return getUrl();
		return Url.parse("/");
	}

	@Override
	public Locale getLocale() {
		// FIXME: Derive it from language passed to start call
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
}
