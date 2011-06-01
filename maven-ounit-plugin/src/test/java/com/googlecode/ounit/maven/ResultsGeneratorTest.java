/*
 * ounit - an OPAQUE compliant framework for Computer Aided Testing
 *
 * Copyright (C) 2010-2011  Antti Andreimann
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package com.googlecode.ounit.maven;

import org.junit.Test;
import static org.junit.Assert.*;

//import org.apache.maven.plugin.testing.

public class ResultsGeneratorTest {
	ResultsGenerator r;
	TestResults mr;
	
	public ResultsGeneratorTest() throws Exception {
		final MockReportParser mp = new MockReportParser();
		mr = mp.parseReportFiles(null);
		r = new ResultsGenerator(new MockResultsMojo(mp));
	}
	
	private void checkValue(String s, String v) {
		assertTrue("results:\n" + s + "\n did not contain \"" + v + "\"",
				s.contains(v));
	}
	
	private void checkForMockedValuesInOutput(String s) {
		//checkValue(s, " " + mr.getTotalSucceeded());
		checkValue(s, " " + mr.getTotalErrors());
		checkValue(s, " " + mr.getTotalFailures());
		checkValue(s, " " + mr.getTotalSkipped());
		checkValue(s, " " + mr.getTotalTests());
		checkValue(s, " " + String.format("%.2fs", mr.getTotalElapsedTime()));
		checkValue(s, " " + mr.getFailureDetails().get(0).getMessage());
		checkValue(s, " " + mr.getFailureDetails().get(1).getType());
		checkValue(s, mr.getFailureDetails().get(0).getName());
		checkValue(s, mr.getFailureDetails().get(1).getFullClassName());
	}
	
	@Test
	public void testGenerateHtmlReport() {
		String res = r.generateHtmlReport();
		checkForMockedValuesInOutput(res);
	}

	@Test
	public void testGenerateTextReport() {
		String res = r.generateTextReport();
		checkForMockedValuesInOutput(res);
	}
}