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

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

/**
 * Generate student project and store it to output directory
 *
 * @execute lifecycle="generate-project" phase="generate-sources"
 * @goal generate-project
 */
public class GenerateProjectMojo
    extends AbstractMojo
{
	/**
	 * Location where results will be created.
	 * 
	 * @parameter expression="${project.build.directory}"
	 */
	protected File outputDirectory;
	
    public void execute()
        throws MojoExecutionException
    {
    	// Delete assembly descriptor
   		File f = new File(outputDirectory, "assembly.xml");
   		f.delete();
   		
		// Classes have been compiled thus generated sources are not needed
		deleteDirectory(new File(outputDirectory, "generated-sources"));
		
		// Remove assembly temp files
		deleteDirectory(new File(outputDirectory, "archive-tmp"));
    }

	public static boolean deleteDirectory(File path) {
		if (path.exists()) {
			File[] files = path.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					deleteDirectory(files[i]);
				} else {
					files[i].delete();
				}
			}
		}
		return (path.delete());
	}
}
