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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.wicket.Application;
import org.apache.wicket.Component;
import org.apache.wicket.IPageManagerProvider;
import org.apache.wicket.IRequestCycleProvider;
import org.apache.wicket.Session;
import org.apache.wicket.ThreadContext;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.application.IComponentInstantiationListener;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.IHeaderResponseDecorator;
import org.apache.wicket.mock.MockPageManager;
import org.apache.wicket.page.IPageManager;
import org.apache.wicket.page.IPageManagerContext;
import org.apache.wicket.protocol.http.mock.MockServletContext;
import org.apache.wicket.request.IExceptionMapper;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.Url;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.cycle.RequestCycleContext;
import org.apache.wicket.request.resource.caching.FilenameWithVersionResourceCachingStrategy;
import org.apache.wicket.request.resource.caching.IResourceCachingStrategy;
import org.apache.wicket.request.resource.caching.IStaticCacheableResource;
import org.apache.wicket.request.resource.caching.ResourceUrl;
import org.apache.wicket.request.resource.caching.version.CachingResourceVersion;
import org.apache.wicket.request.resource.caching.version.IResourceVersion;
import org.apache.wicket.request.resource.caching.version.MessageDigestResourceVersion;
import org.apache.wicket.request.resource.caching.version.RequestCycleCachedResourceVersion;
import org.apache.wicket.settings.IRequestCycleSettings.RenderStrategy;
import org.apache.wicket.util.lang.Args;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.string.Strings;

import com.googlecode.ounit.opaque.OpaqueException;
import com.googlecode.ounit.opaque.ProcessReturn;
import com.googlecode.ounit.opaque.Resource;
import com.googlecode.ounit.opaque.ReturnBase;
import com.googlecode.ounit.opaque.StartReturn;

public class PageRunner {
	private final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(this.getClass());

	OpaqueApplication application;

	public PageRunner(OpaqueApplication application) {
		this.application = application;
		
		ThreadContext.detach();
		
		if(application.getName() == null)
			application.setName("OpaqueApplication-" + UUID.randomUUID());
		
		ThreadContext.setApplication(application);
		
		application.setSessionStore(new OpaqueSessionStore());
		
		// FIXME: Get rid of this cruft
		application.setServletContext(new MockServletContext(application, null));
		
		application.initApplication();
		configure(application);
	}
	
	/**
	 * This method will set up application class to do OPAQUE specific rendering.
	 * 
	 * 1. All "id" and "name" attributes will be prefixed with %%IDPREFIX%%
	 * 2. Resource references are captured and stored in a list so they
	 *    can be loaded and set to client (if not present already)
	 * 3. All resource URL-s will be flattened and prefixed with %%RESOURCES%%
	 * 4. Headers will be separated from the body.
	 * 5. Resources not present in client will be loaded
	 * 6. Exceptions will be exposed (thrown not rendered)
	 * 
	 * @param app
	 */
	
	private void configure(final Application app) {
		//final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(PageRunner.class);
		
		/* Add component listener to rewrite names and ID-s */
		app.getComponentInstantiationListeners().add(new IComponentInstantiationListener() {
			public void onInstantiation(Component component) {
				component.add(new NameAndIdAttributeBehavior());
			}
		});

		/* Set header response decorator to capture headers */
		app.setHeaderResponseDecorator(new IHeaderResponseDecorator() {
			public IHeaderResponse decorate(final IHeaderResponse response) {
				return new OpaqueHeaderResponse(response);
			}
		});
		
		/*
		 * Expose Exceptions. We want OPAQUE client to get a proper SOAP
		 * error instead of a garbled HTML output with a rendered exception
		 * in it.
		 */
		final IRequestCycleProvider rp = app.getRequestCycleProvider();
		app.setRequestCycleProvider(new IRequestCycleProvider() {
			private final IRequestCycleProvider delegate = rp;
			
			@Override
			public RequestCycle get(RequestCycleContext context) {
				context.setExceptionMapper(new IExceptionMapper() {
					@Override
					public IRequestHandler map(Exception e) {
						if (e instanceof RuntimeException) {
							throw (RuntimeException) e;
						} else {
							throw new WicketRuntimeException(e);
						}
					}
				});
				return delegate.get(context);
			}
		});

		/* Register output filters */
		app.getRequestCycleSettings().addResponseFilter(new InvalidMarkupFilter());
		app.getRequestCycleSettings().addResponseFilter(OpaqueResourceMapper.getFilter());
		app.getRequestCycleSettings().setRenderStrategy(RenderStrategy.ONE_PASS_RENDER);
		
		/* Add our mapper between the app and rest of the bunch */
		app.setRootRequestMapper(new OpaqueResourceMapper(app.getRootRequestMapper()));
		
		/*
		 * Force MD5 digest based versions to be included in resource filenames
		 * ({@see org.apache.wicket.settings.def.ResourceSettings}) This is
		 * required to make sure resource changes are picked up and disk space
		 * on the LMS side is used sparingly (all resources passed to the LMS
		 * will be cached on it's local disk and as of 09/2011 Moodle has no
		 * garbage collection so they stay there forever).
		 */
		final IResourceVersion resourceVersion;
		final IResourceCachingStrategy resourceCachingStrategy;
		if (app.usesDevelopmentConfig()) {
			resourceVersion = new RequestCycleCachedResourceVersion(
					new MessageDigestResourceVersion());
		} else {
			resourceVersion = new CachingResourceVersion(
					new MessageDigestResourceVersion());
		}
		resourceCachingStrategy = new FilenameWithVersionResourceCachingStrategy(
				resourceVersion);
		app.getResourceSettings().setCachingStrategy(resourceCachingStrategy);
		
		/* TODO: Create our own page manager */
		app.setPageManagerProvider(new IPageManagerProvider() {
			@Override
			public IPageManager get(IPageManagerContext context) {
				return new MockPageManager();
			}
		});
	}
	
	/**
	 * Create a request cycle and execute it. Follows redirects if necessary.
	 * 
	 * @param request
	 *            the request
	 * @return a RequestCycle object, null if request URL was not resolved to a
	 *         wicket request
	 */
	private RequestCycle processRequest(OpaqueRequest request) {
		
		Args.notNull(request, "request");
		
		OpaqueResponse response;
		RequestCycle requestCycle;
		
		int redirectCount = 0;
		do {
			if(redirectCount > 100)
				throw new WicketRuntimeException("Infinite redirect detected!");
			
			log.debug("Rendering {}", request.getUrl());
			
			// Setup request cycle
			response = new OpaqueResponse();
			requestCycle = application.createRequestCycle(request, response);

			if (!requestCycle.processRequestAndDetach()) {
				return null; // did not resolve to a wicket request
			}
			
			if (response.isRedirect()) {
				Url nextUrl = Url.parse(response.getRedirectLocation());
				if (!nextUrl.isAbsolute()) {
					Url newUrl = new Url(request.getClientUrl().getSegments(),
							nextUrl.getQueryParameters());
					newUrl.concatSegments(nextUrl.getSegments());
					nextUrl = newUrl;
				}
				request = new OpaqueRequest(request.getSessionId(), nextUrl);
			}
			redirectCount++;
		} while(response.isRedirect());
		
		return requestCycle;
	}
	
	public void execute(final OpaqueRequest request, final ReturnBase rv)
			throws OpaqueException {
		final ThreadContext previousThreadContext = ThreadContext.detach();

		try {
			ThreadContext.setApplication(application);
			log.debug("Rendering URL: {}", request.getUrl());

			// Render the page
			RequestCycle cycle;
			cycle = processRequest(request);

			if(cycle == null)
				throw new WicketRuntimeException("Can't resolve URL: "
						+ request.getUrl());
			
			OpaqueResponse r = (OpaqueResponse)cycle.getResponse();
			OpaqueSession session = (OpaqueSession)Session.get();

			if(rv instanceof StartReturn) {
				((StartReturn)rv).setQuestionSession(session.getId());
			}
			
			if(session.isClosed() && rv instanceof ProcessReturn) {
				((ProcessReturn)rv).setResults(session.getResults());
				// We do not set the questionEnd flag here because it will
				// discard the output HTML. 
			}
			
			CharSequence body = r.getCharacterContent();
			CharSequence head = r.getCSS();
			CharSequence css = r.getCSS();
			
			// Render referenced resources
			// TODO: allow resources to reference more resources
			Map<String, Url> rm = r.getReferencedResources(); 
			List<Resource> newResources = new ArrayList<Resource>();
			
			for(String name: rm.keySet()) {
				if(session.getCachedResources().contains(name))
					continue;
				
				String sessionId = request.getSessionId();
				
				OpaqueRequest resourceRequest = new OpaqueRequest(
						sessionId, rm.get(name));
				
				cycle = processRequest(resourceRequest);
				if(cycle == null)
					// FIXME: Should we throw here?
					continue;
				r = (OpaqueResponse)cycle.getResponse();
				
				byte [] data = r.getBinaryContent();
				
				/* Handle dynamically generated resources */
				if(name.startsWith(sessionId)) {
					String fname = r.getFileName();

					if(fname == null)
						fname = "resource-data";
					
					fname = decorateFileName(fname, data);

					body = Strings.replaceAll(body, name, fname);
					head = Strings.replaceAll(head, name, fname);
					css = Strings.replaceAll(css, name, fname);

					name = fname;
				}

				newResources.add(new Resource(name, r.getContentType(), data));

				session.addCachedResource(name);
				session.dirty();
			}

			rv.setXHTML(body.toString());
			rv.setHead(head.toString());
			
			if(css.length() > 0) {
				rv.setCSS(css.toString());
			}
			
			if(newResources.size() > 0) {
				rv.setResources(newResources.toArray(new Resource[rm.size()]));
				log.debug("Sent {} new resources to LMS", newResources.size());
			}
		}
		finally
		{
			ThreadContext.restore(previousThreadContext);
		}
	}

	private String decorateFileName(final String fileName, final byte[] data) {
		Args.notNull(fileName, "fileName");
		
		ResourceUrl url = new ResourceUrl(fileName, null);
		IResourceCachingStrategy rc =
			new FilenameWithVersionResourceCachingStrategy(
				new MessageDigestResourceVersion());
		
		rc.decorateUrl(url, new IStaticCacheableResource() {
			private static final long serialVersionUID = 1L;
			@Override
			public void respond(Attributes attributes) {
			}
			@Override
			public IResourceStream getCacheableResourceStream() {
				return new ByteArrayResourceStream(data);
			}
			@Override
			public Serializable getCacheKey() {
				return null;
			}
		});
		
		return url.getFileName();
	}
}
