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

package com.googlecode.ounit.maven;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

/**
 * Define test "suites" in OUnit sense.
 * A test suite is a set of tests or test suites (in JUnit sense)
 * which will be aggregated and the success rate will be taken into account
 * as a single value when calculating the grades.
 * 
 * @author anttix
 *
 */
public class TestSuite {
	private String name;
	private List<File> dirs;
	private String fullName;
	private TestResults results = null;
	
	public TestSuite(String name, List<File> dirs) {
		this.name = this.fullName = name;
		this.dirs = dirs;
	}
	
	public String getName() {
		return name;
	}
	public List<File> getDirs() {
		return dirs;
	}
	
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public TestResults getResults() {
		return results;
	}
	public void setResults(TestResults results) {
		this.results = results;
	}

	public List<File> getOutputFiles() {
		List<File> rv = new ArrayList<File>();
		for(File dir: getDirs()) {
			File [] of = dir.listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String fName) {
					return fName.matches("^.*?-output\\.[^.]+$");
				}
			});

			/* Skip empty files */
			for(File f: of) {
				if(f.length() > 1) {
					rv.add(f);
				}
			}
		}

		return rv;
	}
}
