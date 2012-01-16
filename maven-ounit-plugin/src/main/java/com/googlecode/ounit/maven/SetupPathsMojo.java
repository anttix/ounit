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

import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Build;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

/**
 * Set up question project. Configure compiler directories etc.
 *
 * @goal setup-paths
 */
public class SetupPathsMojo
    extends AbstractMojo
{
	/**
	 * Location where results will be created.
	 * 
	 * @parameter expression="${project.build.directory}"
	 */
	protected File outputDirectory;
	
	/**
	 * Location where files are
	 * 
	 * @parameter expression="${basedir}"
	 */
	protected File baseDirectory;

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
		
    public void execute()
        throws MojoExecutionException
    {
		final String FS = File.separator;
    	
    	String [] srcTopDirs = { "shared", "student" };
    	String [] testSrcTopDirs = { "teacher" };
    	
		List <String> srcDirs = project.getCompileSourceRoots();
		List <String> testSrcDirs = project.getTestCompileSourceRoots();
		List <Resource> resources = project.getResources();
		List <Resource> testResources = project.getTestResources();
		
		srcDirs.clear();
		testSrcDirs.clear();
		resources.clear();
		testResources.clear();
		
		for(String topDir: srcTopDirs) {
			String subDir = topDir + FS + "main" + FS + "java";
			String newDir = new File(baseDirectory, subDir).getAbsolutePath();
			srcDirs.add(newDir);
			Resource newResource = new Resource();
			newResource.setDirectory(topDir + FS + "main" + FS + "resources");
			resources.add(newResource);
		}
		
		for(String topDir: testSrcTopDirs) {
			String subDir = topDir + FS + "test" + FS + "java";
			String newDir = new File(baseDirectory, subDir).getAbsolutePath();
			testSrcDirs.add(newDir);
			Resource newResource = new Resource();
			newResource.setDirectory(topDir + FS + "test" + FS + "resources");
			testResources.add(newResource);
		}
		
		Build build = project.getBuild();
		build.setOutputDirectory(new File(outputDirectory, "target" + FS
				+ "classes").getAbsolutePath());
		build.setTestOutputDirectory(new File(outputDirectory, "bin" + FS
				+ "teacher-test-classes").getAbsolutePath());
		
		if (getLog().isDebugEnabled()) {
			getLog().debug("Source directories: " + srcDirs);
			getLog().debug("Test source directories: " + testSrcDirs);
			getLog().debug("Class output: " + build.getOutputDirectory());
			getLog().debug("Test class output: " + build.getTestOutputDirectory());
		}
    }
}
