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

import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.PackageResourceReference;

public class MockBasePage extends OpaquePage {
	private static final long serialVersionUID = 1L;
	final MockModelObject m;

	public MockBasePage(PageParameters parameters) {
		super(parameters);
		setDefaultModel(new CompoundPropertyModel<MockModelObject>(new MockModelObject()));
		m = (MockModelObject)getDefaultModelObject();
	}
	
	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		
		response.renderCSSReference(new PackageResourceReference(
				MockHomePage.class, "mockstyle.css"));
		response.renderJavaScriptReference(new PackageResourceReference(
				MockHomePage.class, "mockscript.js"));
	}
	
	@Override
	protected void onMainFormSubmit() {
		OpaqueSession os = getOpaqueSession();
		int nr = m.getNr();
		if(nr >= 0 && nr < 10) {
			//os.setMaxMarks(3);
			//os.setScore(nr * 33.3);
			os.setScore(nr * 10);
			os.setClosed(true);
		}
	}
}
