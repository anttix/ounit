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

import org.apache.wicket.Application;
import org.apache.wicket.Component;
import org.apache.wicket.ThreadContext;
import org.apache.wicket.application.IComponentInstantiationListener;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.IHeaderResponseDecorator;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.mock.MockHttpServletResponse;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.mapper.parameter.PageParameters.NamedPair;
import org.apache.wicket.settings.IRequestCycleSettings.RenderStrategy;
import org.apache.wicket.util.tester.WicketTester;

import com.googlecode.ounit.opaque.OpaqueException;
import com.googlecode.ounit.opaque.Resource;
import com.googlecode.ounit.opaque.ReturnBase;

public class PageRunner {
	private final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(this.getClass());

	WicketTester renderer;
	WebApplication application;
	
	public PageRunner(WebApplication application) {
		this.application = application;
		renderer = new WicketTester(application);
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
	 * 
	 * @param app
	 */
	
	public static void configure(final Application app) {
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
		app.getRequestCycleListeners().add(new AbstractRequestCycleListener() {
			@Override
			public void onBeginRequest(RequestCycle cycle) {
				
			}
			@Override
			public void onEndRequest(RequestCycle cycle) {
				
			}
		});
		*/
		
		/* Register output filters */
		app.getRequestCycleSettings().addResponseFilter(new InvalidMarkupFilter());
		app.getRequestCycleSettings().addResponseFilter(OpaqueResourceMapper.getFilter());
		app.getRequestCycleSettings().setRenderStrategy(RenderStrategy.ONE_PASS_RENDER);
		
		/* Add our mapper between the app and rest of the bunch */
		app.setRootRequestMapper(new OpaqueResourceMapper(app.getRootRequestMapper()));
	}
	
	public void doPage(OpaqueSession rq, ReturnBase rv) throws OpaqueException {
		try {
			ThreadContext.setApplication(application);
			OpaqueReturn rh = OpaqueReturn.get();
			OpaqueSession.set(rq);
			//Session.get();
			
			rh.setCachedResources(rq.getCachedResources());
			rh.setEngineSessionId(rq.getEngineSessionId());
			
			String pageUrl = rq.getWicketPage();
			if(pageUrl != null) {
				log.debug("Processing wicket pageUrl: {}", pageUrl);
				
				// FIXME: This is a seriously ugly temporary hack!
				if(!pageUrl.startsWith("?") && !pageUrl.startsWith("wicket/")) {
					if(pageUrl.startsWith("page?"))
						pageUrl = "wicket/" + pageUrl;
					else
						pageUrl = "wicket/bookmarkable/" + pageUrl;
				}
				
				renderer.getRequest().setURL(pageUrl);

				for(NamedPair i : rq.getPostParameters().getAllNamed()) {
					renderer.getRequest().getPostParameters()
						.addParameterValue(i.getKey(), i.getValue());
				}
				renderer.processRequest();
			} else {
				log.debug("Starting new HomePage");
				renderer.startPage(application.getHomePage());
				renderer.processRequest();
			}
			
			// TODO: Why does this throw NullPointers 
			// at org.apache.wicket.Session.getId(Session.java:368)
			//log.debug("Wiket session ID = {}", renderer.getSession().getId());

			rv.setXHTML(renderer.getLastResponse().getDocument());
			rv.setHead(rh.getHeader());
			
			String css = rh.getCSS();			
			if(css.length() > 0) {
				rv.setCSS(css);
			}
			
			Map<String, IRequestHandler> rm = rh.getNewResources(); 
			List<Resource> newResources = new ArrayList<Resource>(rm.size());
			
			for(String name: rm.keySet()) {
				renderer.processRequest(rm.get(name));
				MockHttpServletResponse r = renderer.getLastResponse();
				newResources.add(new Resource(name, r.getContentType(), r.getBinaryContent()));
				// TODO: rh.getCachedResources().add(name);
			}
			
			if(newResources.size() > 0) {
				rv.setResources(newResources.toArray(new Resource[rm.size()]));
				log.debug("Sent {} new resources to LMS", newResources.size());
			}
		} /*catch(Exception e) {
			throw new OpaqueException(e);
		}*/
		finally
		{
			OpaqueReturn.detach();
			OpaqueSession.detach();
			ThreadContext.detach();
		}
	}
}
