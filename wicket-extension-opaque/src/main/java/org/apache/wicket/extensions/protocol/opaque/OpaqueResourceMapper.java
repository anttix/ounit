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

import java.util.List;

import org.apache.wicket.IRequestListener;
import org.apache.wicket.IResourceListener;
import org.apache.wicket.RequestListenerInterface;
import org.apache.wicket.Session;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.IRequestMapper;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Url;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.handler.BookmarkableListenerInterfaceRequestHandler;
import org.apache.wicket.request.handler.ListenerInterfaceRequestHandler;
import org.apache.wicket.request.handler.RenderPageRequestHandler;
import org.apache.wicket.request.handler.resource.ResourceReferenceRequestHandler;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.request.resource.caching.IStaticCacheableResource;
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
	
	private final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(this.getClass());

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
		
		/* No point in trying to extract a resource that we can't map */
		if(url == null)
			return url;
		
		RequestCycle requestCycle = RequestCycle.get();
		
		if(!(requestCycle.getOriginalResponse() instanceof OpaqueResponse))
			throw new WicketRuntimeException(
					"Opaque resource mapper can only be used with OpaqueResponse");
		
		OpaqueResponse response = (OpaqueResponse) requestCycle
				.getOriginalResponse();
		
		/* This is a marvelous piece of ugly hackery!
		 * OPAQUE uses %%RESOURCES%% as a resource prefix. Of-course the {@link Url}
		 * class will escape the %-s and guess what? It's final so it's impossible to override
		 * that behaviour! So we add a syntactically correct placeholder here and then replace it
		 * later with an output filter. Grr ......  
		 * 
		 * FIXME: Maybe there IS a way to combat this nonsense!
		 */
		if (url.toString().startsWith(PLACEHOLDER_HACK)) // Already rewritten
			return url;

		String name;

		if (requestHandler instanceof RenderPageRequestHandler) {
			// Do not crawl to other pages
			return url;
		}
		
		RequestListenerInterface listenerInterface = null;
		if (requestHandler instanceof BookmarkableListenerInterfaceRequestHandler) {
			listenerInterface = 
				((BookmarkableListenerInterfaceRequestHandler) requestHandler)
				.getListenerInterface();
		} else if (requestHandler instanceof ListenerInterfaceRequestHandler) {
			listenerInterface = 
				((ListenerInterfaceRequestHandler) requestHandler)
				.getListenerInterface();
		}
		
		if (listenerInterface != null) {
			Class<? extends IRequestListener> interfaceClass = listenerInterface
					.getListenerInterfaceClass();
			
			if(interfaceClass.equals(IResourceListener.class)) {
				name = dynamicName(url);
			} else {
				// Do not touch other listener requests
				return url;
			}
			
		} else if (requestHandler instanceof ResourceReferenceRequestHandler) {

			ResourceReference resourceReference =
				((ResourceReferenceRequestHandler) requestHandler)
					.getResourceReference();
			
			IResource resource = resourceReference.getResource();
			
			if (resource instanceof IVersionedResource) {
				/*
				 * Special "versioned" resources have their checksum (aka
				 * version) embedded into the "versioned" filename.
				 */
				name = ((IVersionedResource) resource).getVersionedName();
			} else if (resource instanceof IStaticCacheableResource) {
				/*
				 * Cacheable resources have their checksum (aka version) already
				 * embedded into the URL. Therefore it is safe to use it as is.
				 */
				List<String> segments = url.getSegments();
				name = segments.get(segments.size() - 1);
			} else {
				name = dynamicName(url);
			}

		} else { 
			log.debug("Unable to extract data from handler {}",
					requestHandler.getClass().getName());
			
			return url;
		}
		
		/* Queue a request for the referenced resource and replace the URL */		
		response.addReferencedResource(name, new Url(url));
		url.getSegments().clear();
		url.getQueryParameters().clear();
		url.getSegments().add(PLACEHOLDER_HACK);
		url.getSegments().add(name);
		
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
	
	/**
	 * Generate names for dynamic resources. The names are prefixed and suffixed
	 * with the current session ID to make them unique and safe to
	 * search/replace. PageRunner will later filter the HTML output and replace
	 * these names with filenames returned by respective resource streams.
	 * 
	 * @return a name that is safe to use as a place holder
	 */
	private String dynamicName(final Url url) {
		String id = Session.get().getId();		

		Url newUrl = new Url(url);
		
		List <String> segments = newUrl.getSegments();

		if(segments.size() > 1 && segments.get(1).equals("resource"))
			segments.remove(1);
		if(segments.size() > 0 && segments.get(0).equals("wicket"))
			segments.remove(0);
		
		return id + newUrl.toString().replace('/', '.').replace('?', '.') + id;
	}
}
