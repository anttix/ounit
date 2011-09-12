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
import java.util.Map;
import java.util.UUID;

import org.apache.wicket.Application;
import org.apache.wicket.Component;
import org.apache.wicket.IPageManagerProvider;
import org.apache.wicket.IRequestCycleProvider;
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
import org.apache.wicket.request.IRequestParameters;
import org.apache.wicket.request.Url;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.cycle.RequestCycleContext;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.settings.IRequestCycleSettings.RenderStrategy;
import org.apache.wicket.util.lang.Args;

import com.googlecode.ounit.opaque.OpaqueException;
import com.googlecode.ounit.opaque.Resource;
import com.googlecode.ounit.opaque.ReturnBase;

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
		
		/* TODO: Create our own page manager */
		app.setPageManagerProvider(new IPageManagerProvider() {
			@Override
			public IPageManager get(IPageManagerContext context) {
				return new MockPageManager();
			}
		});
		
		//app.getResourceSettings().setResourcePollFrequency(null);
	}
	
	/**
	 * Create a request cycle and execute it. Follows redirects if necessary.
	 * 
	 * @param url
	 *            page URL
	 * @return a RequestCycle object, null if URL was not resolved to a wicket
	 *         request
	 */
	private RequestCycle processRequest(final String url) {
		return processRequest(url, null);
	}
	
	/**
	 * Create a request cycle and execute it. Follows redirects if necessary.
	 * 
	 * @param url
	 *            page URL
	 * @param postParameters
	 *            POST parameters
	 * @return a RequestCycle object, null if URL was not resolved to a wicket
	 *         request
	 */
	private RequestCycle processRequest(final String url,
			final IRequestParameters postParameters) {
		
		Args.notNull(url, "wicketPage");
		
		OpaqueRequest request;
		OpaqueResponse response;
		RequestCycle requestCycle;
		
		int redirectCount = 0;
		Url nextUrl = Url.parse(url);
		do {
			if(redirectCount > 100)
				throw new WicketRuntimeException("Infinite redirect detected!");
			
			log.debug("Rendering {}", nextUrl);
			
			// Setup request cycle
			request = new OpaqueRequest();
			response = new OpaqueResponse();
			requestCycle = application.createRequestCycle(request, response);
			
			// FIXME: Are these lines actually required?
			requestCycle.setCleanupFeedbackMessagesOnDetach(false);
			ThreadContext.setRequestCycle(requestCycle);
			requestCycle.scheduleRequestHandlerAfterCurrent(null);
			
			request.setUrl(nextUrl);
			request.setPostParameters(postParameters);
			
			if (!requestCycle.processRequestAndDetach()) {
				return null; // did not resolve to a wicket request
			}
			
			if (response.isRedirect()) {
				nextUrl = Url.parse(response.getRedirectLocation());
				if (!nextUrl.isAbsolute()) {
					Url newUrl = new Url(request.getClientUrl().getSegments(),
							nextUrl.getQueryParameters());
					newUrl.concatSegments(nextUrl.getSegments());
					nextUrl = newUrl;
				}
			}
			redirectCount++;
		} while(response.isRedirect());
		
		return requestCycle;
	}
	
	public void doPage(OpaqueSession rq, ReturnBase rv) throws OpaqueException {
		try {
			ThreadContext.setApplication(application);
			OpaqueReturn rh = OpaqueReturn.get();
			OpaqueSession.set(rq);
			
			rh.setCachedResources(rq.getCachedResources());
			rh.setEngineSessionId(rq.getEngineSessionId());
			
			String pageUrl = rq.getWicketPage();
			if(pageUrl == null) {
				log.debug("Rendering HomePage");
				pageUrl = "";
			} else {
				log.debug("Rendering URL: {}", pageUrl);
				
				// FIXME: This is a seriously ugly temporary hack!
				if(!pageUrl.startsWith("?") && !pageUrl.startsWith("wicket/")) {
					if(pageUrl.startsWith("page?"))
						pageUrl = "wicket/" + pageUrl;
					else
						pageUrl = "wicket/bookmarkable/" + pageUrl;
				}

			}

			// Render the page
			RequestCycle cycle;
			PageParameters postParameters = rq.getPostParameters();
			if(postParameters != null)
				cycle = processRequest(pageUrl, new PageRequestParameters(
					postParameters));
			else
				cycle = processRequest(pageUrl);

			if(cycle == null)
				throw new WicketRuntimeException("Can't resolve URL: " + pageUrl);
			
			OpaqueResponse r = (OpaqueResponse)cycle.getResponse();
			
			// TODO: Why does this throw NullPointers 
			// at org.apache.wicket.Session.getId(Session.java:368)
			//log.debug("Wiket session ID = {}", renderer.getSession().getId());

			rv.setXHTML(r.getCharacterContent());
			rv.setHead(rh.getHeader());
			
			String css = rh.getCSS();		
			if(css.length() > 0) {
				rv.setCSS(css);
			}
			
			// Render referenced resources
			// TODO: allow resources to reference more resources
			Map<String, Url> rm = rh.getNewResources(); 
			List<Resource> newResources = new ArrayList<Resource>(rm.size());
			
			for(String name: rm.keySet()) {
				log.debug("Rendering resource {}", rm.get(name));
				
				cycle = processRequest(rm.get(name).toString());
				if(cycle == null)
					// FIXME: Should we throw here?
					continue;
				r = (OpaqueResponse)cycle.getResponse();
				newResources.add(new Resource(name, r.getContentType(),
						r.getBinaryContent()));
				// TODO: rh.getCachedResources().add(name);
			}
			
			if(newResources.size() > 0) {
				rv.setResources(newResources.toArray(new Resource[rm.size()]));
				log.debug("Sent {} new resources to LMS", newResources.size());
			}
		}
		finally
		{
			OpaqueReturn.detach();
			OpaqueSession.detach();
			ThreadContext.detach();
		}
	}
}
