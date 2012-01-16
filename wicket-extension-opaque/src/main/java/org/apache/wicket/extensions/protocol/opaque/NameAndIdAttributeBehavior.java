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

/**
 * Opaque protocol requires that all name and id attributes have to be prefixed
 * with %ID% in order to avoid collisions when two questions are loaded on the
 * same page. This class implements that behavior.
 * 
 * Names are prefixed during tag rendering because form data from OPAQUE
 * comes back without the prefix. Therefore we do not want to touch the name inside
 * the component itself.
 * 
 * ID-s must be prefixed on configuration time because other components may depend on them
 * to render correctly (eg JavaScript snippets, Anchor links etc.).
 * 
 * @author anttix
 *
 */
public class NameAndIdAttributeBehavior extends Behavior {
	public final String ID_PREFIX = "%%IDPREFIX%%";
	
	private static final long serialVersionUID = 1L;
	
	@Override
	public void onConfigure(Component component) {
		boolean oldOutputFlag = component.getOutputMarkupId();
		String oldId = component.getMarkupId();
		
		if(oldId.startsWith(ID_PREFIX)) return;

		component.setMarkupId(ID_PREFIX + oldId);
		component.setOutputMarkupId(oldOutputFlag);
	}
	
	@Override
	public void beforeRender(Component component) {
		String id = component.getMarkupId(false);
		if( id == null ) return;
		assert id.startsWith(ID_PREFIX) : "All component ID-s must be prefixed with" + ID_PREFIX;
	}
	
	@Override
	public void onComponentTag(final Component component, final ComponentTag tag)
	{
		if (isEnabled(component))
		{	
			String oldName = tag.getAttribute("name");
			if(oldName != null && !oldName.startsWith(ID_PREFIX)) {
				tag.getAttributes().put("name", ID_PREFIX + oldName);
			}
			
			String oldId = tag.getAttribute("id");
			if(oldId != null && !oldId.startsWith(ID_PREFIX)) {
				tag.getAttributes().put("id", ID_PREFIX + oldId);
			}
		}
	}	
}
