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

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * 
 * We call alternate lifecycles "initialize" phase because we do not want
 * anything to be regenerated or rebuilt.
 * 
 * Teacher tests normally run in integration-test phase in order to have access
 * to services started in pre-integration-test phase.
 * 
 * @execute lifecycle="teacher-tests" phase="initialize"
 * @goal teacher-tests
 * @phase integration-test
 * 
 * @author anttix
 *
 */
public class TeacherTestsMojo extends AbstractMojo {

	public void execute() throws MojoExecutionException, MojoFailureException {
		// I will do nothing!
		// My lifecycle will do the heavy lifting.
	}
}
