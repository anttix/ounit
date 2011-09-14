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

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import org.apache.maven.plugin.logging.Log;

import com.googlecode.ounit.html.*;

/**
 * Parse output from testing subsystems and generate test results to be passed
 * back to the LMS.
 * 
 * @author <a href="mailto:anttix@users.sourceforge.net">Antti Andreimann</a>
 * @version $Id$
 */

public class ResultsGenerator {
	private List<TestSuite> testResults;
	private Log log = null;
	private Map<String, Double> marks = new HashMap<String, Double>();
	
	// TODO: i18n these
	private final String labelTestResults = "Test Results";
	private final String labelFailedTests = "Failed Tests";
	private final String labelStudent = "Student";
	private final String labelTeacher = "Teacher";
	private final String summaryLine =
		"Tests run: %d, Failures: %d, Errors: %d, Skipped: %d, Time elapsed: %.2fs";
	private final String failureLine = "%s(%s)\n  %s: %s";

	public ResultsGenerator(MojoData mavenInternals) throws Exception {
		log = mavenInternals.getLog();
		testResults = new ArrayList<TestSuite>(2);

		TestSuite suite;
		List<File> dirs;
		ReportParser parser = mavenInternals.getReportParser();
		
		dirs = mavenInternals.getStudentTestDirectories();
		TestResults results = parser.parseReportFiles(dirs);
		if(results.getTotalTests() > 0) {
			suite = new TestSuite(labelStudent, dirs);
			suite.setResults(results);
			testResults.add(suite);
		}

		dirs = mavenInternals.getTeacherTestDirectories();
		suite = new TestSuite(labelTeacher, dirs);
		suite.setResults(parser.parseReportFiles(dirs));
		
		if(suite.getResults().getTotalTests() <= 0) 
			throw new Exception("Teacher tests were not executed");
		
		marks.put("teacher", suite.getResults().getTotalPercentage());
		testResults.add(suite);
	}
	
	public String generateHtmlReport() {
		log.debug("Generating HTML Report");
		
		Tag panel = new Div();
		panel.setClasses("ou-testresults");
		
		for(TestSuite suite: testResults) {
			log.debug("Processing " + suite.getName());
			
			Tag sr = new Div();
			sr.setClasses("ou-test-suite-results");
			panel.add(sr);
			
			/* Caption */
			Tag caption = new H3();
			caption.setClasses("ou-test-suite-results-caption");
			caption.add(suite.getName() + " " + labelTestResults);
			sr.add(caption);
			
			/* Details */
			Tag details = new Div();
			details.setClasses("ou-test-suite-results-details");
			sr.add(details);
			
			/* Summary */
			TestResults results = suite.getResults();
			Tag summary = new Div();
			summary.setClasses("ou-test-summary");
			details.add(summary);
			summary.add(generateSummaryLine(results));
			

			/* Failures */
			List<FailureDetail> failureDetails = results.getFailureDetails();
			if(failureDetails.size() > 0) {
				Tag failureCaption = new H4();
				failureCaption.setClasses("ou-test-failures-caption");
				failureCaption.add(labelFailedTests);
				details.add(failureCaption);
				Tag failures = new Ol();
				failures.setClasses("ou-failed-tests-list");
				details.add(failures);

				for(FailureDetail f: failureDetails) {
					Tag failure = new Li();
					failures.add(failure);
					for(String line: generateFailureString(f).split("\n")) {
						if(failure.size() > 0)
							failure.add(new Br());
						failure.add(line);
					}
				}
			}
		}
		
		return panel.toString();
	}

	public String generateTextReport() { 
		final String dashes =
			"------------------------------------------------------------------------\n";
		
		StringBuilder sb = new StringBuilder();
		
		log.debug("Generating Text Report");
		
		for(TestSuite suite: testResults) {
			log.debug("Processing " + suite.getName());
			TestResults results = suite.getResults();

			sb.append(dashes);
			sb.append(suite.getName());
			sb.append(" " + labelTestResults);
			sb.append("\n");
			sb.append(dashes);
			sb.append(generateSummaryLine(results));
			sb.append("\n");
			sb.append("\n");
			
			for(FailureDetail f: results.getFailureDetails()) {
				sb.append(generateFailureString(f));
				sb.append("\n");
			}
			sb.append(dashes);
			sb.append("\n");
		}
		
		return sb.toString();
	}

	public Properties generateMarks() {
		Properties rv = new Properties();
		
		// TODO: Take whatever TDD stuff into account
		rv.put("default",
				String.format(Locale.US, "%.2f", marks.get("teacher")));
		
		for(String key: marks.keySet())
			rv.put(key, String.format(Locale.US, "%.2f", marks.get(key)));

		return rv;
	}
	
	private String generateSummaryLine(TestResults results) {
		return String.format(summaryLine, results.getTotalTests(),
				results.getTotalFailures(), results.getTotalErrors(),
				results.getTotalSkipped(), results.getTotalElapsedTime());
	}
	
	private String generateFailureString(FailureDetail f) {
		return String.format(failureLine, f.getName(), f.getFullClassName(),
				f.getType(), f.getMessage());
	}
}