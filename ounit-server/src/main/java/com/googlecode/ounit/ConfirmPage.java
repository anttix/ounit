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

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public class ConfirmPage extends BasePage {
	private static final long serialVersionUID = 1L;

	public ConfirmPage(PageParameters parameters) {
		super(parameters);
		
		mainForm.add(new HtmlFile("resultsFile"));
		mainForm.add(new Label("marks"));
		mainForm.add(new Label("maxMarks"));

		mainForm.add(new Button("grade") {
			private static final long serialVersionUID = 1L;
			@Override
			public void onSubmit() {
				getOunitSession().setClosed(true);
		        setResponsePage(MainPage.class); 
			}
		});
		
		mainForm.add(new Button("return") {
			private static final long serialVersionUID = 1L;
			@Override
			public void onSubmit() {
		        setResponsePage(MainPage.class); 
			}
		});
	}	
}
