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

import org.junit.*;

import com.googlecode.ounit.opaque.OpaqueService;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.text.StringContains.*;

public class OunitServiceTest {
	private static OpaqueService service;
	
	@BeforeClass
	public static void createService() {
		service = new OunitService();
	}

	@Test
	@SuppressWarnings("deprecation")
	public void testGetEngineInfo() {
		String xml = service.getEngineInfo();
		
		assertThat(xml, is(notNullValue()));
		assertThat(xml, containsString("<engineinfo>"));
		assertThat(xml, containsString("</engineinfo>"));
		assertThat(xml, containsString("<name>"));
		assertThat(xml, containsString("</name>"));
	}

	@Test
	@Ignore
	@SuppressWarnings("deprecation")
	public void testGetQuestionMetadata() throws Exception {
		String xml = service.getQuestionMetadata("ounit.selftest.simple", "1.0", "");
		
		assertThat(xml, is(notNullValue()));
		assertThat(xml, containsString("<questionmetadata>"));
		assertThat(xml, containsString("</questionmetadata>"));
		assertThat(xml, containsString("<marks>"));
		assertThat(xml, containsString("</marks>"));
	}
}
