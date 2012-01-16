/*
 * ounit - an OPAQUE compliant framework for Computer Aided Testing
 *
 * Copyright (C) 2010  Antti Andreimann
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

import java.io.File;
import java.util.List;

public class MockReportParser implements ReportParser {
	
	public TestResults parseReportFiles(List<File> dirs) throws Exception {
		TestResults rv = new TestResults();
		
		// Mock summary data. 
	    rv.setTotalTests      ( 10    );
	    rv.setTotalFailures   (  3    );
	    rv.setTotalErrors     (  2    );
	    rv.setTotalSkipped    (  1    );
	    rv.setTotalElapsedTime( 56.50 );
	    //rv.setTotalPercentage(  40    );

		// Mock failures
		rv.addFailure(new FailureDetail(
			"thisWillFail",
			"foopackage.junit4.JUnit4ErrorTest",
			"org.junit.ComparisonFailure",
			"Wrong animal in my pocket"));

		rv.addFailure(new FailureDetail(
				"thisWillFail",
				"foopackage.junit4.JUnit4TimeoutTest",
				"java.lang.Exception",
				"test timed out after 1000 milliseconds"));

	    return rv;
	}
}
