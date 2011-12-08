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
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

abstract public class MojoData extends AbstractMojo {
	/**
	 * Location where results will be created.
	 * 
	 * @parameter expression="${project.build.directory}"
	 */
	protected File outputDirectory;

	/**
	 * Maven Project
	 * 
	 * @parameter expression="${project}"
	 * @required @readonly
	 */
	protected MavenProject project;

	/**
	 * The Maven Session Object
	 * 
	 * @parameter expression="${session}"
	 * @required
	 * @readonly
	 */
	protected MavenSession session;

	/**
	 * The Maven PluginManager Object
	 * 
	 * @component
	 * @required
	 */
	protected BuildPluginManager pluginManager;

	/**
	 * Directories containing the teacher tests surefire XML Report files
	 * 
	 * @parameter
	 */
	protected File[] teacherTestsReportsDirectories;
	
	/**
	 * Include test output files in reports
	 * 
	 * @parameter expression="${ounit.showTestOutput}" default-value="true"
	 */
	protected boolean showTestOutput;
	
	public File getOutputDirectory() {
		return outputDirectory;
	}

	public MavenProject getProject() {
		return project;
	}
	
	public MavenSession getSession() {
		return session;
	}
	
	public BuildPluginManager getPluginManager() {
		return pluginManager;
	}
	
	public boolean isShowTestOutput() {
		return showTestOutput;
	}

	/**
	 * 
	 * @return
	 */
	public String getOunitDirectory() {
		return outputDirectory + "/ounit-reports";
	}

	// FIXME: Determine surefire, failsafe and teacher test directories from
	// build configuration instead of hardcoded defaults

	public String getSurefireDirectory() {
		return project.getBuild().getDirectory() + "/surefire-reports";
	}

	public String getFailsafeDirectory() {
		return project.getBuild().getDirectory() + "/failsafe-reports";
	}

	private String getTeacherDirectory() {
		return project.getBuild().getDirectory() + "/teacher-reports";
	}
	
	public List<File> getStudentTestDirectories() {
		Log log = getLog();
		List<File> dirs = new ArrayList<File>(2);
		
		String[] repDirs = { getSurefireDirectory(), getFailsafeDirectory() };
		for (String d : repDirs) {
			File f = new File(d);
			if (f.isDirectory()) {
				log.debug("Looking for student test results in " + d);
				dirs.add(f);
			}
		}
		return dirs;
	}
	
	public List<File> getTeacherTestDirectories() {
		Log log = getLog();
		List<File> dirs = new ArrayList<File>(1);
		
		String[] repDirs = { getTeacherDirectory() };
		for (String d : repDirs) {
			File f = new File(d);
			if (f.isDirectory()) {
				log.debug("Looking for teacher test results in " + d);
				dirs.add(f);
			}
		}
		return dirs;
	}

	public ReportParser getReportParser() throws Exception {
		return new ReflectiveSurefireReportParser(
				getSession(), getPluginManager());
	}
}
