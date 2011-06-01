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
import java.util.List;
import java.util.Properties;

import org.apache.wicket.extensions.protocol.opaque.OpaqueSession;
import com.googlecode.ounit.opaque.OpaqueException;
import com.googlecode.ounit.opaque.Results;
import com.googlecode.ounit.opaque.Score;

public class OunitSession extends OpaqueSession {
	public static final String DESCRIPTION_FILE = "description.html";
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
	
	private transient org.slf4j.Logger log;
	private org.slf4j.Logger getLog() {
		if(log == null)
			log = org.slf4j.LoggerFactory.getLogger(this.getClass());
		
		return log;
	}

	private File projDir;	
	private List<String> editFiles;
	
	public OunitSession(File projDir, String[] initialParamNames,
			String[] initialParamValues) throws OpaqueException {
		super(initialParamNames, initialParamValues);
		this.projDir = projDir;
	}
	
	public static OunitSession get() {
		return (OunitSession)OpaqueSession.get();
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

	/* These read-only properties can be easily recreated on-fly thus there is no
	 * point to serialize them to a session store */
	private transient File description;
	private transient File resultsFile;
	private transient ProjectTree tree;

	public ProjectTree getTree() {
		final File srcDir = new File(projDir, SRCDIR);
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
			log.debug("setScore({})", s);
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
				log.debug("Score axis '{}' = {}",
						new Object[] { s.getAxis(), s.getMarks() } );
			}

			return rv;
		} catch (Exception e) {
			throw new RuntimeException("Can not build results", e);
		}
	}

	private Properties getMarksProps() throws Exception {
		File f = new File(projDir, MARKS_FILE);
		log.debug("Loading marks from {}", f);
		Properties p = new Properties();
		p.load(new FileInputStream(f));
		return p;
	}

}