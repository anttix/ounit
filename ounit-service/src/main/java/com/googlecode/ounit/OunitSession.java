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

package com.googlecode.ounit;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.wicket.extensions.protocol.opaque.OpaqueSession;

import com.googlecode.ounit.executor.OunitExecutionRequest;
import com.googlecode.ounit.executor.OunitResult;
import com.googlecode.ounit.executor.OunitTask;
import com.googlecode.ounit.opaque.OpaqueException;
import com.googlecode.ounit.opaque.Results;
import com.googlecode.ounit.opaque.Score;

public class OunitSession extends OpaqueSession {
	public static final String DESCRIPTION_FILE = "description/index.html";
	public static final String RESULTS_FILE     = "target/ounit-reports/results.html";
	public static final String MARKS_FILE       = "target/ounit-reports/marks.properties";
	public static final String DEFAULT_PROPERTY = "default";
	public static final String MARKS_PROPERTY   = "ounit.marks";
	public static final String TITLE_PROPERTY   = "ounit.title";
	public static final String RWFILES_PROPERTY = "ounit.editfiles";
	public static final String PREPARE_LOG      = "prepare.log";
	public static final String BUILD_LOG        = "build.log";
	public static final String SRCDIR           = "src";
	
	// TODO: Make this configurable
	public static final String WORKDIR = "/tmp/ounit-work";
	
	private transient org.slf4j.Logger _log;
	private org.slf4j.Logger getLog() {
		if(_log == null)
			_log = org.slf4j.LoggerFactory.getLogger(this.getClass());
		
		return _log;
	}

	private File projDir;
	private List<String> editFiles;
	private OunitQuestion question;
	private boolean prepared;

	public OunitSession(File projDir, OunitQuestion question,
			String[] initialParamNames, String[] initialParamValues)
			throws OpaqueException {
		super(initialParamNames, initialParamValues);
		this.projDir = projDir;
		this.question = question;
	}

	public static OunitSession get() {
		return (OunitSession)OpaqueSession.get();
	}
	
	public OunitQuestion getQuestion() {
		return question;
	}
	
	public File getProjDir() {
		return projDir;
	}
	
	public void setProjDir(File projDir) {
		this.projDir = projDir;
	}
	
	public List<String> getEditFiles() {
		return editFiles;
	}
	
	public void setEditFiles(List<String> editFiles) {
		this.editFiles = editFiles;
	}
	
	/* Sent to LMS by WelcomePage so question revision will be 
	 * included in replays */ 
	public String getRevision() {
		return question.getRevision();
	}

	public void setRevision(String revision) {
		question.setRevision(revision);
	}

	/* These read-only properties can be easily recreated on-fly thus there is no
	 * point to serialize them to a session store */
	private transient File description;
	private transient File resultsFile;
	private transient ProjectTree tree;

	public ProjectTree getTree() {
		final File srcDir = new File(projDir, SRCDIR);
		if(!srcDir.isDirectory()) {
			getLog().warn("Project directory {} does not exist. Stale session?", projDir);
			throw new RuntimeException("Project directory does not exist. Stale session?");
		}
		
		if(tree == null) {
			getLog().debug("Loading tree model from {}", srcDir);
		
			tree = new ProjectTree(new File(projDir, "src"),
				new FilenameFilter() {
					public boolean accept(File dir, String name) {
						String t = new File(dir, name).getAbsolutePath()
							.replace(srcDir.getAbsolutePath() + File.separator, "");
						if(editFiles.contains(t))
							return true;
						else
							return false;
					}
			}, null);
		}

		return tree;
	}

	public File getDescription() {
		if(description == null)
			description = new File(projDir, DESCRIPTION_FILE);
		
		return description;
	}

	public File getResultsFile() {
		if(resultsFile == null)
			resultsFile = new File(projDir, RESULTS_FILE);
		
		return resultsFile;
	}
	
	public List<ProjectTreeNode> getEditors() {
		return getTree().getRwNodes();
	}
	
	public List<ProjectTreeNode> getEditorcaptions() {
		return getEditors();
	}
	
	@Override
	public double getScore() {
		try {
			double s = Double.parseDouble((String) getMarksProps().get(DEFAULT_PROPERTY));
			getLog().debug("setScore({})", s);
			setScore(s);
		} catch (Exception e) {
			setScore(0);
		}
		return super.getScore();
	}
		
	@Override
	public Results getResults() throws OpaqueException {
		try {
			Properties p = getMarksProps();
			Results rv = super.getResults();
			
			for(Object key: p.keySet()) {
				String k = (String) key;
				if(k.equals(DEFAULT_PROPERTY)) continue;
				int v = (int)Math.round(Double.parseDouble((String)p.get(key)));
				rv.addScore(k, v);
			}
			
			for(Score s: rv.getScores()) {
				getLog().debug("Score axis '{}' = {}",
						new Object[] { s.getAxis(), s.getMarks() } );
			}

			return rv;
		} catch (Exception e) {
			throw new RuntimeException("Can not build results", e);
		}
	}

	private Properties getMarksProps() throws Exception {
		File f = new File(projDir, MARKS_FILE);
		getLog().debug("Loading marks from {}", f);
		Properties p = new Properties();
		p.load(new FileInputStream(f));
		return p;
	}

	/**
	 * Load and decode properties from student POM.
	 * 
	 * @throws OpaqueException
	 */
	public void loadModelProps() throws OpaqueException {
		Properties modelProps = OunitService.getModelProperties(projDir);
	
		Object marks = modelProps.get(MARKS_PROPERTY);
		if (marks != null) {
			getLog().debug("Found " + MARKS_PROPERTY + " = {} in POM", marks);
			setMaxMarks(Integer.parseInt((String) marks));
		}
		String tmp = (String) modelProps.get(RWFILES_PROPERTY);
		if (tmp == null)
			throw new OpaqueException(
					RWFILES_PROPERTY + " missing from student pom.xml");
		List<String> editFiles = new ArrayList<String>();
		for (String f : tmp.split("\n"))
			editFiles.add(f);
	
		getLog().debug("Editable files loaded from POM: {}", editFiles);		
		setEditFiles(editFiles);
	}

	public boolean isPrepared() {
		return prepared;
	}

	/**
	 * Prepare question (generate student sources and POM)
	 * 
	 * @return
	 */
	public void prepare() {
		if(prepared)
			throw new RuntimeException("Session already prepared");

		OunitTask task = startPrepare();
		OunitResult r = OunitService.waitForTask(task);
		
		String errstr = "Failed to prepare question";
		try {
			if(r.hasErrors()) {
				getLog().warn(errstr, r.getErrors());
				throw new Exception(errstr + ": " + r.getErrors());
			} else {
				loadModelProps();
			}
		} catch(Exception e) {
			deleteDirectory(getProjDir());
			throw new RuntimeException(e);
		}
		
		prepared = true;
	}
	
	/**
	 * Request a Maven build to prepare the question.
	 * 
	 * @return
	 */
	private OunitTask startPrepare() {
		getLog().debug("Preparation phase of session {} started", getEngineSessionId());
		
		File qDir = question.getSrcDir();
		getLog().debug("Found question in {}", qDir);
		
		if(projDir.isDirectory())
			throw new RuntimeException("Directory " + projDir + " already exists");
		projDir.mkdirs();
		
		getLog().debug("Preparing question from {} to {}",
					new Object[] { qDir, projDir });
			
		OunitTask task = OunitService.scheduleTask(new OunitExecutionRequest()
			.setBaseDirectory(qDir)
			.setOutputDirectory(projDir.getAbsolutePath())
			.setLogFile(new File(projDir, PREPARE_LOG)));
			
		return task;
	}

	/**
	 * Execute a build in project directory.
	 * 
	 */
	public void build() {
		// TODO
	}
	
	/**
	 * Request a maven build
	 * @return
	 */
	public OunitTask startBuild() {
		getLog().debug("Build of session {} started", getEngineSessionId());
		
		if(!projDir.isDirectory()) throw new RuntimeException("Attempted to compile a stale session");
		
		OunitTask task = OunitService.scheduleTask(new OunitExecutionRequest()
			.setBaseDirectory(projDir)
			.setLogFile(new File(projDir, BUILD_LOG)));
		
		return task;
	}
	
	/**
	 * Helper function to recursively delete a directory.
	 * 
	 * @param path
	 * @return
	 */
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
