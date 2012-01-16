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

package com.googlecode.ounit;

import org.apache.wicket.extensions.protocol.opaque.OpaquePage;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.PackageResourceReference;

/**
 * Base page for all Ounit engine pages.
 * This class will make sure generic CSS files are loaded, main form
 * properly set up, SessionBean populated etc.
 * 
 * @author anttix
 *
 */
public class BasePage extends OpaquePage {
	private static final long serialVersionUID = 1L;
	
	public BasePage(PageParameters parameters) {
		super(parameters);
		
		/* We use OunitSession as a read-only bean for all page components */
		setDefaultModel(new CompoundPropertyModel<OunitSession>(
				new AbstractReadOnlyModel<OunitSession>() {
					private static final long serialVersionUID = 1L;

					@Override
					public OunitSession getObject() {
						return OunitSession.get();
					}
				}));
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		
		response.renderJavaScriptReference(new PackageResourceReference(
				MainPage.class, "jquery/jquery.min.js")); // 1.5
		response.renderJavaScript("jQuery.noConflict();", "jQnoConflict");
		
		response.renderCSSReference(new PackageResourceReference(
				MainPage.class, "ounit.css"));
	}
	
	@SuppressWarnings("unchecked")
	public IModel<OunitSession> getOunitModel() {
		return (IModel<OunitSession>)getDefaultModel();
	}
	
	public OunitSession getOunitSession() {
		return getOunitModel().getObject();
	}
}
