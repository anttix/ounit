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

import java.util.List;
import java.util.Set;

import org.apache.wicket.request.IRequestParameters;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.lang.Args;
import org.apache.wicket.util.string.StringValue;

/**
 * Utility class that expresses PageParameters as {@link IRequestParameters}.
 * 
 * @author Antti Andreimann
 */
public class PageRequestParameters implements IRequestParameters {
	private final PageParameters parameters;

	public PageRequestParameters(final PageParameters parameters) { 
		Args.notNull(parameters, "parameters");
		
		this.parameters = parameters;
	}

	@Override
	public Set<String> getParameterNames() {
		return parameters.getNamedKeys();
	}

	@Override
	public StringValue getParameterValue(String name) {
		return parameters.get(name);
	}

	@Override
	public List<StringValue> getParameterValues(String name) {
		return parameters.getValues(name);
	}
}