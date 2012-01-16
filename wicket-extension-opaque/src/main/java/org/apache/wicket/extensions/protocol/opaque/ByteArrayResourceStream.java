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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.wicket.util.lang.Args;
import org.apache.wicket.util.lang.Bytes;
import org.apache.wicket.util.resource.AbstractResourceStream;
import org.apache.wicket.util.resource.ResourceStreamNotFoundException;

public class ByteArrayResourceStream extends AbstractResourceStream {
	private static final long serialVersionUID = 1L;

	private byte [] data;
	String contentType;
	InputStream inputStream;
	
	public ByteArrayResourceStream(byte [] data) {
		this(data, null);
	}
	
	public ByteArrayResourceStream(byte [] data, String contentType) {
		Args.notNull(data, "data");
		this.data = data;
		this.contentType = contentType;
	}
	
	@Override
	public String getContentType() {
		return contentType;
	}

	@Override
	public InputStream getInputStream() throws ResourceStreamNotFoundException {
		if (inputStream == null) {
			inputStream = new ByteArrayInputStream(data);
		}

		return inputStream;
	}
	
	@Override
	public Bytes length() {
		return Bytes.bytes(data.length);
	}

	@Override
	public void close() throws IOException {
		if (inputStream != null) {
			inputStream.close();
			inputStream = null;
		}
	}

}
