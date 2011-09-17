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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.wicket.request.IRequestParameters;
import org.apache.wicket.util.string.StringValue;

/**
 * Utility class that expresses a list as {@link IRequestParameters}.
 * 
 * @author Antti Andreimann
 */

public class OpaqueRequestParameters implements IRequestParameters {
	final Map<String, List<StringValue>> parameters = new HashMap<String, List<StringValue>>();
	
	/* Access is only allowed inside our own circle of trust (read: package) */
	void add(String name, String value) {
		List<StringValue> list = parameters.get(name);
		if(list == null) {
			list = new ArrayList<StringValue>(1);
			parameters.put(name, list);
		}
		list.add(StringValue.valueOf(value));
	}

	/*
	 * Code of the following three methods is shamelessly stolen from
	 * @see org.apache.wicket.mock.MockRequestParameters
	 * 
	 * Licensed to the Apache Software Foundation (ASF) under one or more
	 * contributor license agreements.
	 */
	@Override
	public Set<String> getParameterNames() {
		return Collections.unmodifiableSet(parameters.keySet());
	}

	@Override
	public StringValue getParameterValue(String name) {
		List<StringValue> values = parameters.get(name);
		return (values != null && !values.isEmpty()) ? values.get(0)
				: StringValue.valueOf((String) null);
	}

	@Override
	public List<StringValue> getParameterValues(String name) {
		List<StringValue> values = parameters.get(name);
		return values != null ? Collections.unmodifiableList(values) : null;
	}
}
