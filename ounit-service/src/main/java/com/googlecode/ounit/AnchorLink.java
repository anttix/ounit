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

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.link.StatelessLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 * A link that points to an object in the same page.
 * 
 * @author anttix
 *
 */
public class AnchorLink extends StatelessLink<Void> {
	
	public AnchorLink(final String id, final Component anchor) {
		this(id, anchor, (IModel<?>)null);
	}
	
	public AnchorLink(final String id, final Component anchor, final String label) {
		this(id, anchor, Model.of(label));
	}
	
	public AnchorLink(final String id, final Component anchor, final IModel<?> label) {
		super(id);
		
		setAnchor(anchor);
		if(label != null) {
			setDefaultModel(label);
			setBody(label);
		}
	}
	
	private static final long serialVersionUID = 1L;
	@Override
	public void onClick() { }
	@Override
	protected CharSequence getURL() { return ""; }
}