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

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

/**
 * Verify question.
 * 
 * Check that boilerplate (student code) compiles and scores zero points.
 * Check that reference solution (teacher code) compiles and scores maximum points.
 * 
 * @execute lifecycle="verify-question" phase="initialize"
 * @goal verify
 */

public class VerifyMojo extends AbstractMojo {
	public void execute() throws MojoExecutionException {
		// Do nothing !
	}
}
