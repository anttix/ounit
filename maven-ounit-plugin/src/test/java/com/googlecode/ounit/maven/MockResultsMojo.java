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
import java.util.LinkedList;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

public class MockResultsMojo extends MojoData {
	ReportParser mp;

	public MockResultsMojo(ReportParser mp) {
		this.mp = mp;
	}

	@Override
	public ReportParser getReportParser() throws Exception {
		return mp;
	}

	@Override
	public List<File> getStudentTestDirectories() {
		return new LinkedList<File>();
	}

	@Override
	public List<File> getTeacherTestDirectories() {
		return new LinkedList<File>();
	}
	
	public void execute() throws MojoExecutionException, MojoFailureException { }
}
