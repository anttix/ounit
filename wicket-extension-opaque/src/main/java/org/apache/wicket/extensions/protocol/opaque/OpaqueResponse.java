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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;

import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.request.Url;
import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.util.time.Time;

public class OpaqueResponse extends WebResponse {
	String mimeType;
	String redirectLocation = null;
	
	private ByteArrayOutputStream byteStream;
	private StringWriter stringWriter;

	private Map<String, Url> referencedResources = new HashMap<String, Url>();
	private StringBuilder css = new StringBuilder();
	private StringBuilder head = new StringBuilder();
	private String pageURL;
	private String fileName;
		
	OpaqueResponse() {
		stringWriter = new StringWriter();
		byteStream = new ByteArrayOutputStream();
	}
	
	public String getContentType() {
		return mimeType;
	}
	
	/**
	 * Get the binary content that was written with {@link #write(byte[])}
	 * 
	 * @return The binary content
	 */
	public byte[] getBinaryContent()
	{
		return byteStream.toByteArray();
	}
	
	/**
	 * Get the content that was written with {@link #write(CharSequence)}
	 * 
	 * @return The document
	 */
	public String getCharacterContent()
	{
		return stringWriter.getBuffer().toString();
	}
	
	/**
	 * 
	 * 
	 * @return redirected location
	 */
	public String getRedirectLocation() {
		return redirectLocation;
	}
	
	public Map<String, Url> getReferencedResources() {
		return referencedResources;
	}
	
	public void setReferencedResources(Map<String, Url> referencedResources) {
		this.referencedResources = referencedResources;
	}
	
	public void addReferencedResource(String name, Url url) {
		if(!referencedResources.containsKey(name))
			referencedResources.put(name, url);
	}
		
	public String getCSS() {
		return css.toString();
	}
	
	public String getHeaderContributions() {
		return head.toString();
	}
	
	public String getFileName() {
		return fileName;
	}

	public void addHeaderContribution(String header) {
		// TODO: Consider other tags to filter (eg: <meta http-equiv="Content-Type: ...)
		header = header.replaceAll("<title[^>]*>[^<]*</title>", "");
		head.append(header);
	}

	public String getPageURL() {
		return pageURL;
	}
	
	public void setPageURL(String url) {
		pageURL = url;
	}

	@Override
	public void addCookie(Cookie cookie) {
	}

	@Override
	public void clearCookie(Cookie cookie) {
	}

	@Override
	public void setAttachmentHeader(String filename) {
		this.fileName = filename;
	}
	
	@Override
	public void setInlineHeader(String filename) {
		this.fileName = filename;
	}
	
	// TODO: Exctact filename from Content-Disposition header
	
	@Override
	public void setHeader(String name, String value) {
	}

	@Override
	public void addHeader(String name, String value) {
	}

	@Override
	public void setDateHeader(String name, Time date) {
	}

	@Override
	public void setContentLength(long length) {
	}

	@Override
	public void setContentType(String mimeType) {
		this.mimeType = mimeType;
	}

	@Override
	public void setStatus(int sc) {
	}

	@Override
	public void sendError(int sc, String msg) {
	}

	@Override
	public String encodeRedirectURL(CharSequence url) {
		return url.toString();
	}

	@Override
	public void sendRedirect(String url) {
		redirectLocation = url;
	}

	@Override
	public boolean isRedirect() {
		return redirectLocation != null;
	}

	@Override
	public void flush() {
	}

	@Override
	public void write(CharSequence sequence) {
		stringWriter.append(sequence);
	}

	@Override
	public void write(byte[] array) {
		try {
			byteStream.write(array);
		} catch (IOException e) {
			throw new WicketRuntimeException(e);
		}
	}

	@Override
	public String encodeURL(CharSequence url) {
		return url.toString();
	}

	@Override
	public Object getContainerResponse() {
		return null;
	}
}
