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
import java.io.FileInputStream;
import java.util.Properties;

import org.apache.maven.plugin.MojoExecutionException;

/**
 * Verify that boilerplate (student code) compiles and scores zero points
 * 
 * @goal check-marks
 */
public class CheckMarksMojo extends MojoData {
	/**
	 * Expected marks on the default axis.
	 * 
	 * @parameter
	 * @required
	 */
	protected double marks;

	public void execute() throws MojoExecutionException {
		double default_marks;
		try {
			Properties marks = new Properties();
			marks.load(new FileInputStream(new File(new File(
					getOunitDirectory()), "marks.properties")));
			default_marks = Double.parseDouble(marks.getProperty("default"));
		} catch (Exception e) {
			throw new MojoExecutionException("Failed to parse test results", e);
		}
		if (default_marks != marks)
			throw new MojoExecutionException("Invalid default marks. Expected "
					+ marks + " got " + default_marks);
	}
}
