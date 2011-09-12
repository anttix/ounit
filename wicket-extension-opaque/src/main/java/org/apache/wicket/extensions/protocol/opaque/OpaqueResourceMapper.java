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

import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.IRequestMapper;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Url;
import org.apache.wicket.request.handler.resource.ResourceReferenceRequestHandler;
import org.apache.wicket.response.filter.IResponseFilter;
import org.apache.wicket.util.lang.Args;
import org.apache.wicket.util.string.AppendingStringBuffer;

/**
 * Rewrite resource URLs to %%RESOURCE%%.
 * 
 * @author anttix
 *
 */
public class OpaqueResourceMapper implements IRequestMapper {
	static final String PLACEHOLDER_HACK = "opaque-resources";
	static final String OPAQUE_PLACEHOLDER = "%%RESOURCES%%";

	/**
	 * The original request mapper that will actually resolve the page
	 */
	private final IRequestMapper delegate;

	public OpaqueResourceMapper(IRequestMapper delegate) {
		Args.notNull(delegate, "delegate");

		this.delegate = delegate;
	}

	public int getCompatibilityScore(Request request) {
		return delegate.getCompatibilityScore(request);
	}

	public Url mapHandler(IRequestHandler requestHandler) {
		Url url = delegate.mapHandler(requestHandler);
		
		/* This is a marvelous piece of ugly hackery!
		 * OPAQUE uses %%RESOURCES%% as a resource prefix. Of-course the {@link Url}
		 * class will escape the %-s and guess what? It's final so it's impossible to override
		 * that behaviour! So we add a syntactically correct placeholder here and then replace it
		 * later with an output filter. Grr ......  
		 * 
		 * FIXME: Maybe there IS a way to combat this nonsense!
		 */
		if(requestHandler instanceof ResourceReferenceRequestHandler) {
			if(!url.toString().startsWith(PLACEHOLDER_HACK)) {
				String name = url.toString().replace("wicket/resource/", "").replace('/', '.');
				OpaqueReturn.get().addNewResource(name, new Url(url));
				url.getSegments().clear();
				url.getSegments().add(PLACEHOLDER_HACK);
				url.getSegments().add(name);
			}
		}
		return url;
	}

	public IRequestHandler mapRequest(Request request) {
		return delegate.mapRequest(request);
	}

	public static IResponseFilter getFilter() {
		return new IResponseFilter() {
			
			public AppendingStringBuffer filter(
					AppendingStringBuffer responseBuffer) {
				int i = 0;
				while((i = responseBuffer.indexOf(PLACEHOLDER_HACK, i)) != -1) {
					while(i >= 3 && responseBuffer.substring(i - 3, i).equals("../")) {
						i -= 3;
						responseBuffer.replace(i, i + 3, "");	
					}
					responseBuffer.replace(i, i + PLACEHOLDER_HACK.length(), OPAQUE_PLACEHOLDER);	
				}
				return responseBuffer;
			}
			
		};
	}
}
