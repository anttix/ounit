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
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Build;
import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.PluginExecution;
import org.apache.maven.model.Profile;
import org.apache.maven.model.Repository;
import org.apache.maven.model.io.ModelWriter;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

/**
 * Generate student POM and other files to destination directory
 *
 * @goal generate-files
 */
public class GenerateFilesMojo
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
	
	/**
	 * The Maven ModelWriter
	 * 
	 * @component
	 * @required
	 */
	protected ModelWriter modelWriter;
	
	/**
	 * What files to prepare for downloading.
	 * Can be src, all or none
	 * 
	 * @parameter expression="${ounit.download}"
	 */
	protected String download;
	
    public void execute()
        throws MojoExecutionException
    {
        /* Copy required stuff from teacher POM to student POM */
        Model model = new Model();
        Object ouparent = project.getProperties().get("ounit.parent");
        if(ouparent != null) {
        	String [] tmp = ouparent.toString().split(":");
        	if(tmp.length != 3)
				throw new MojoExecutionException(
						"Invalid value for ounit.parent property. " +
						"Must be in format groupId:artifactId:version");
        	Parent parent = new Parent();
        	parent.setGroupId(tmp[0]);
        	parent.setArtifactId(tmp[1]);
        	parent.setVersion(tmp[2]);
        	model.setParent(parent);
        }
        model.setModelVersion( project.getModelVersion() );
        model.setGroupId( project.getGroupId() );
        model.setArtifactId( project.getArtifactId() );
        model.setVersion( project.getVersion() );
        Object packaging = project.getProperties().get("ounit.packaging");   
        if(packaging != null)
        	model.setPackaging(packaging.toString());
        model.setProperties( project.getProperties() );
        model.setPrerequisites( project.getPrerequisites() );
        model.setDependencies( project.getDependencies() );
        /* There is no point to copy DependencyManagement section because
         * all management directives are already applied when we call
         * project.getDependencies() and we do not expect students to
         * subclass the generated POM. 
         */
        //model.setDependencyManagement( project.getDependencyManagement() );
        
        Build build = new Build();
        build.setPluginManagement(project.getBuild().getPluginManagement());
        /* TODO: Add groupId */
        List<String> blacklist = Arrays.asList(
        		new String [] { "maven-clean-plugin", "maven-site-plugin" });
        for(Plugin p: project.getBuild().getPlugins()) {
        	if(p.getArtifactId().equals("maven-ounit-plugin")) {
        		Plugin n = new Plugin();
        		n.setGroupId(p.getGroupId());
        		n.setArtifactId(p.getArtifactId());
        		n.setVersion(p.getVersion());
        		n.setExtensions(true);
        		PluginExecution e = new PluginExecution();
        		e.addGoal("setup-student");
        		e.addGoal("teacher-tests");
        		e.addGoal("generate-results");
        		n.addExecution(e);
        		build.addPlugin(n);
        	} else if(!blacklist.contains(p.getArtifactId())) {
        		build.addPlugin(p);
        	}
        }
        if(ouparent == null)
        	build.setDefaultGoal("verify");
        model.setBuild(build);

        /* Sanitize and Add repositories */
        for(Repository r: project.getRepositories()) {
        	if(r.getUrl().startsWith("file://")) continue;
        	if(r.getId().equals("central")) continue;
        	if(r.getId().startsWith("ounit-dependency-")) continue;
        	model.addRepository(r);
        }        
        for(Repository r: project.getPluginRepositories()) {
        	if(r.getUrl().startsWith("file://")) continue;
        	if(r.getId().equals("central")) continue;
        	if(r.getId().startsWith("ounit-dependency-")) continue;
        	model.addPluginRepository(r);
        }

        /* Sanitize and add build profiles */
        {
            MavenProject tmp = project;
            while(tmp != null) {
            	for(Profile p: tmp.getModel().getProfiles() )
            		if(!p.getId().startsWith("ounit-"))
            			model.addProfile(p);
        		tmp = tmp.getParent();
        	}
        }

        //File baseDir = project.getBasedir();
        
        /* Generate student edible file list */
        File ssDir = new File(baseDirectory, "student");
		model.getProperties().put("ounit.editfiles", listFiles(ssDir, ssDir));

		// Write down student POM
    	try {
			modelWriter.write(new File(outputDirectory, "pom.xml"), null, model);
		} catch (IOException e) {
			throw new MojoExecutionException("Failed to generate student pom.xml", e);
		}
		
		// Generate assembly descriptor for downloading files
		if (download != null && !download.equals("none")) {
			File f = new File(outputDirectory, "assembly.xml");
			InputStream is = this.getClass().getResourceAsStream(
					"/assemblies/ounit-" + download + ".xml");
			if (is == null) {
				throw new MojoExecutionException("Invalid value " + download
						+ " for ounit.download");
			}
			try {
				OutputStream os = new FileOutputStream(f);
				copyStream(is, os);
			} catch (Exception e) {
				throw new MojoExecutionException(
						"Error copying assembly descriptor", e);
			}
		}
    }
    
	private String listFiles(File dir, File baseDir) {
		StringBuilder rv = new StringBuilder();

		File [] files = dir.listFiles(new FileFilter() {
			public boolean accept(File pathname) {
				return !pathname.isHidden();
			}
		});
		
		for(File f: files) { 
			if(f.isDirectory()) {
				rv.append(listFiles(f, baseDir));
			} else {
				String relativeName = f.getPath().replace(
						baseDir.getPath() + File.separator, "");
				rv.append(relativeName);
				rv.append("\n");
			}
		}
		
		return rv.toString();
	}

	private void copyStream(InputStream src, OutputStream dst)
			throws IOException {
		try {
			byte[] buf = new byte[1024];
			int len;
			while ((len = src.read(buf)) > 0) {
				dst.write(buf, 0, len);
			}
		} finally {
			if (src != null) {
				src.close();
			}
			if (dst != null) {
				dst.close();
			}
		}
	}
}
