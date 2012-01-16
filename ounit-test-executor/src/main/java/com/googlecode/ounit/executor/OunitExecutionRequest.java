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

package com.googlecode.ounit.executor;

import java.io.File;

public class OunitExecutionRequest {
	private File baseDirectory = null;
	private String outputDirectory = null;
	private File logFile = null;

	public File getBaseDirectory() {
		return baseDirectory;
	}

	public OunitExecutionRequest setBaseDirectory(File baseDirectory) {
		this.baseDirectory = baseDirectory;

		return this;
	}

	public String getOutputDirectory() {
		return outputDirectory;
	}

	public OunitExecutionRequest setOutputDirectory(String outputDirectory) {
		this.outputDirectory = outputDirectory;
		
		return this;
	}

	public File getLogFile() {
		return logFile;
	}

	public OunitExecutionRequest setLogFile(File logFile) {
		this.logFile = logFile;
		
		return this;
	}
}
