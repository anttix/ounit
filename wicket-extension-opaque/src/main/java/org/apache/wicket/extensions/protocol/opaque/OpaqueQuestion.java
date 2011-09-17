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

import org.apache.wicket.util.lang.Args;

import com.googlecode.ounit.opaque.QuestionInfo;

public class OpaqueQuestion {
	protected String id;
	protected String version;
	protected String baseUrl;
	protected QuestionInfo info;
	
	public OpaqueQuestion(String id, String version, String baseUrl) {
		Args.notEmpty(id, "id");
		
		this.id = id;
		this.version = version;
		this.baseUrl = baseUrl;
		this.info = new QuestionInfo();
	}
	
	public String getId() {
		return id;
	}
	public String getVersion() {
		return version;
	}
	public String getBaseUrl() {
		return baseUrl;
	}
	public QuestionInfo getInfo() {
		return info;
	}
	public void setInfo(QuestionInfo info) {
		this.info = info;
	}
}
