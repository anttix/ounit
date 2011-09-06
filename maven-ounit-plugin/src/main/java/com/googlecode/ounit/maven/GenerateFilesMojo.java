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
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Build;
import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.PluginExecution;
import org.apache.maven.model.Repository;
import org.apache.maven.model.io.ModelWriter;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

/**
 * Goal which filters sources to output directory
 *
 * @execute lifecycle="prepare-question" phase="package"
 * @goal generate-student
 */
public class GenerateStudentMojo
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
	 * Maven base directory
	 * 
	 * @parameter expression="${project}"
	 * @required @readonly
	 */

	
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
        	model.addRepository(r);
        }        
        for(Repository r: project.getPluginRepositories()) {
        	if(r.getUrl().startsWith("file://")) continue;
        	if(r.getId().equals("central")) continue;
        	model.addPluginRepository(r);
        }
        
        //File baseDir = project.getBasedir();
        
        /* Generate student edible file list */
        File ssDir = new File(baseDirectory, "student");
		model.getProperties().put("ounit.editfiles", listFiles(ssDir, ssDir));

    	try {
			modelWriter.write(new File(outputDirectory, "pom.xml"), null, model);

			/* Create policy file */
			/* TODO: Load student policies from a resource file */
			FileWriter out = new FileWriter(new File(outputDirectory, "tests.policy"), true);
			
			//out.write("grant codeBase \"" +
			//		  new File(outputDirectory, "bin").toURI() +
			//		  "-\" { permission java.security.AllPermission; };\n");
			
			// FIXME: We should be using properties from the current build, but
			//        unfortunately they can not be properly passed to java command line
			//        so the only viable solution would be to create a new mojo
			//        that generates this file and attach it to a pre-test phase.
			
			out.write("grant codeBase \"" +
					  session.getLocalRepository().getUrl() +
					  "-\" { permission java.security.AllPermission; };\n");
			out.write("grant codeBase \"file:${user.home}/.m2/repository/-\"" +
			  " { permission java.security.AllPermission; };\n");
			out.write("grant codeBase \"file:${user.dir}/bin/-\"" +
					  " { permission java.security.AllPermission; };\n");
			
			out.close();		
		} catch (IOException e) {
			throw new MojoExecutionException("Failed to generate student pom.xml", e);
		}
		
		// Classes have been compiled thus generated sources are not needed
		deleteDirectory(new File(outputDirectory, "generated-sources"));
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
