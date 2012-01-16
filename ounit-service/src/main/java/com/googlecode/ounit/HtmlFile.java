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

import java.io.IOException;

import org.apache.wicket.extensions.protocol.opaque.InvalidMarkupFilter;
import org.apache.wicket.markup.Markup;
import org.apache.wicket.markup.MarkupFactory;
import org.apache.wicket.markup.MarkupResourceStream;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.util.file.File;
import org.apache.wicket.util.resource.StringResourceStream;
import org.apache.wicket.util.string.AppendingStringBuffer;

/**
 * Include contents of a HTML file into a panel.
 * 
 * @author anttix
 */
public class HtmlFile extends Panel {
	private static final long serialVersionUID = 1L;
	
	public HtmlFile(String id) {
		super(id);
	}

	public HtmlFile(String id, final java.io.File file) {
		super(id);
		setDefaultModelObject(file);
	}
	
	private File getFile() {
		return new File((java.io.File)getDefaultModelObject());
	}
	
	@Override
	protected void onConfigure() {
		super.onConfigure();
		setVisible(getFile().canRead());
	}
		
	@Override
	public Markup getAssociatedMarkup() {
		File f = getFile();
		
		if(!getFile().canRead()) {
			//return Markup.NO_MARKUP;
			return Markup.of("<wicket:panel></wicket:panel>");
		}
		
		final AppendingStringBuffer sb = new AppendingStringBuffer("<wicket:panel>");
		try {
			sb.append(f.readString());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		sb.append("</wicket:panel>");
		InvalidMarkupFilter.removeInvalidMarkup(sb);
		
		return MarkupFactory.get().loadMarkup(this, new MarkupResourceStream(
						new StringResourceStream(sb)), false);
	}
}
