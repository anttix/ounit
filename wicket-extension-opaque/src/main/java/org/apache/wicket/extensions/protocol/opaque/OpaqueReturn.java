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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.wicket.Application;
import org.apache.wicket.MetaDataKey;
import org.apache.wicket.request.IRequestHandler;

public class OpaqueReturn {
	
	/** meta data for header buffer */
	private static final MetaDataKey<OpaqueReturn> OPAQUE_RETURN_KEY = new MetaDataKey<OpaqueReturn>()
	{
		private static final long serialVersionUID = 1L;
	};
	
	public static OpaqueReturn get() {
		if(Application.get().getMetaData(OPAQUE_RETURN_KEY) == null)
			Application.get().setMetaData(OPAQUE_RETURN_KEY, new OpaqueReturn());
	
		return Application.get().getMetaData(OPAQUE_RETURN_KEY);
	}
	
	public static void detach() {
		Application.get().setMetaData(OPAQUE_RETURN_KEY, null);
	}

	private List<String> cachedResources = new ArrayList<String>();
	private Map<String, IRequestHandler> newResources = new HashMap<String, IRequestHandler>();
	// private StringBuilder scripts = new StringBuilder();
	private StringBuilder css = new StringBuilder();
	private StringBuilder head = new StringBuilder();
	private String pageURL;
	private String engineSessionId;
	
	public boolean isCached(String name) {
		return cachedResources.contains(name);
	}
	
	public List<String> getCachedResources() {
		return cachedResources;
	}
	
	public void setCachedResources(List<String> cachedResources) {
		this.cachedResources = cachedResources;
	}

	public void addNewResource(String name, IRequestHandler request) {
		if(!isCached(name))
			newResources.put(name, request);
	}
	
	public Map<String, IRequestHandler> getNewResources() {
		return newResources;
	}
	
	public String getCSS() {
		return css.toString();
	}
	
	public String getHeader() {
		return head.toString();
	}

	public void setHeader(String header) {
		// TODO: Consider other tags to filter (eg: <meta http-equiv="Content-Type: ...)
		header = header.replaceAll("<title[^>]*>[^<]*</title>", "");
		head.append(header);
	}

	public String getPageURL() {
		return pageURL;
	}
	
	public void setPageURL(String url) {
		pageURL = url;
	}
	
	public String getEngineSessionId() {
		return engineSessionId;
	}
	
	public void setEngineSessionId(String engineSessionId) {
		this.engineSessionId = engineSessionId;
	}
}