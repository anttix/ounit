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
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.model.IModel;

public class QuizStateAttributeModifier extends Behavior {
	private static final long serialVersionUID = 1L;
	
	IModel<OunitSession> model;
	Behavior modifier;

	private String attribute;
	private String openValue;
	private String closedValue;
	
	public QuizStateAttributeModifier(IModel<OunitSession> model,
			String attribute, String openValue, String closedValue) {

		this.model = model;
		this.attribute = attribute;
		this.openValue = openValue;
		this.closedValue = closedValue;
	}

	@Override
	public void onComponentTag(Component component, ComponentTag tag) {
		if (!isEnabled(component)) return;
		
		// FIXME: This shouldn't happen ....
		if(model == null || model.getObject() == null) return;
		
		String value = (model.getObject().isClosed()) ? closedValue : openValue;
		
		if(value == null)
			tag.getAttributes().remove(attribute);
		else
			tag.getAttributes().put(attribute, value);
	}
}