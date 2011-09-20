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

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public abstract class OpaquePage extends WebPage {
	private static final long serialVersionUID = 1L;
	//private final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(this.getClass());
	
	protected Form<?> mainForm;
	
    public OpaquePage(final PageParameters parameters) {
    	String kala = "";
    	
    	mainForm = new StatelessForm<Object>("mainform") {
			private static final long serialVersionUID = 1L;
			
			@Override
			protected void onConfigure() {
				super.onConfigure();
				String url = getActionUrl().toString();
    			getOpaqueResponse().setPageURL(url);
			}

			protected void onSubmit() {
				onMainFormSubmit();
			}
    	};
    	super.add(mainForm);
    	mainForm.setRenderBodyOnly(true);
    	Component wicketpage = new HiddenField<String>("wicketpage", Model.of(kala));
    	mainForm.add(wicketpage);
    	/* We specifically do not use a model here, because it's easier to implement with 
    	 * a behaviour. */
    	
    	// FIXME: This should be added by a PageManager or something
    	//        so we do not depend on OpaqueResponse from this class
    	wicketpage.add(new Behavior() {
    		private static final long serialVersionUID = 1L;
    		
    		@Override
    		public void onComponentTag(Component component, ComponentTag tag) {
    			String url = getOpaqueResponse().getPageURL();
    			if(url != null) {
    				 // FIXME: Ugly hack!
        			url = url.replaceAll("(\\.\\./)*", "");
        			url = url.replaceAll("(\\.\\./)*\\.\\.\\?", "?");
    				tag.getAttributes().put("value", url);
    			}
    		}
    	});
    	
    	setVersioned(false);
    	mainForm.setVersioned(false);
    	setStatelessHint(true);
    	
    	/* Close session if the LMS asks us to finish */
		if (parameters.getNamedKeys().contains(
				OpaqueRequest.MOODLE_FINISH_PARAMETER_NAME))
			getOpaqueSession().setClosed(true);
    }
    
    private OpaqueResponse getOpaqueResponse() {
    	return (OpaqueResponse)getRequestCycle().getOriginalResponse();
    }
    
    protected OpaqueSession getOpaqueSession() { 
    	return (OpaqueSession)getSession();
    }
    
    protected void onMainFormSubmit() { }
}
