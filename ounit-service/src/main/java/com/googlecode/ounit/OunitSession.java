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

import static com.googlecode.ounit.OunitConfig.*;
import static com.googlecode.ounit.OunitUtil.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.wicket.extensions.protocol.opaque.OpaqueSession;
import org.apache.wicket.request.Request;

import com.googlecode.ounit.executor.OunitExecutionRequest;
import com.googlecode.ounit.executor.OunitResult;
import com.googlecode.ounit.executor.OunitTask;
import com.googlecode.ounit.opaque.OpaqueException;
import com.googlecode.ounit.opaque.Results;
import com.googlecode.ounit.opaque.Score;

public class OunitSession extends OpaqueSession {
	private static final long serialVersionUID = 1L;
	
	// TODO: Do not allow more than one thread to mess with a single session
	
	/* Temporary directory to hold user files (aka sessions) */
	static final File sessDir = new File(WORKDIR, SESSION_DIR);
	
	private transient org.slf4j.Logger _log;
	private org.slf4j.Logger getLog() {
		if(_log == null)
			_log = org.slf4j.LoggerFactory.getLogger(this.getClass());
		
		return _log;
	}

	private File projDir;
	private List<String> editFiles;
	private boolean prepared;
	private String downloadChecksum;
	private int attempt = 1;
	private int maxAttempts = DEFAULT_ATTEMPTS;

	public OunitSession(Request request) {
		super(request);
		this.projDir = new File(sessDir, getId());
		
		getLog().debug("Successfully set up new engine session: {}", getId());
	}

	public static OunitSession get() {
		return (OunitSession)OpaqueSession.get();
	}
	
	@Override
	public OunitQuestion getQuestion() {
		return (OunitQuestion)question;
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
		return getQuestion().getRevision();
	}

	public void setRevision(String revision) {
		getQuestion().setRevision(revision);
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
		Results rv = super.getResults();
		try {
			Properties p = getMarksProps();
			
			for(Object key: p.keySet()) {
				String k = (String) key;
				if(k.equals(DEFAULT_PROPERTY)) continue;
				int v = (int)Math.round(Double.parseDouble((String)p.get(key)));
				rv.addScore(k, v);
			}			
		} catch (IOException e) {
			// Exception while loading results property file normally
			// means that the file was not generated
			getLog().debug("Error loading results properties {}", e.getMessage());
		}
		
		for(Score s: rv.getScores()) {
			getLog().debug("Score axis '{}' = {}",
					new Object[] { s.getAxis(), s.getMarks() } );
		}

		return rv;
	}

	private Properties getMarksProps() throws IOException {
		File f = new File(projDir, MARKS_FILE);
		Properties p = new Properties();
		p.load(new FileInputStream(f));
		getLog().debug("Loaded marks from {}", f);
		return p;
	}

	/**
	 * Load and decode properties from student POM.
	 * 
	 * @throws OpaqueException
	 */
	public void loadModelProps() throws OpaqueException {
		Properties modelProps = OunitApplication.getModelProperties(projDir);
	
		Object marks = modelProps.get(MARKS_PROPERTY);
		if (marks != null) {
			getLog().debug("Found " + MARKS_PROPERTY + " = {} in POM", marks);
			setMaxMarks(Integer.parseInt((String) marks));
		}
		Object attempts = modelProps.get(ATTEMPTS_PROPERTY);
		if (attempts != null) {
			getLog().debug("Found " + ATTEMPTS_PROPERTY + " = {} in POM", attempts);
			setMaxAttempts(Integer.parseInt((String) attempts));
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
	
	public boolean hasDownload() {
		File f = new File(projDir, DOWNLOAD_FILE);
		
		return f.exists();
	}
	
	public File getDownloadFile() {
		File f = new File(projDir, DOWNLOAD_FILE);
		if(f.exists())
			return f;
		else
			return null;
	}
	
	public String getDownloadFileName() {
		String hash = getDownloadChecksum();
		if(hash == null)
			return null;
		else
			return hash + ".zip";
	}
	
	public boolean isPrepared() {
		return prepared;
	}
	
	public int getAttempt() {
		return attempt;
	}
	
	public void setAttempt(int nattempt) {
		this.attempt = nattempt;
	}
	
	public int getMaxAttempts() {
		return maxAttempts;
	}
	
	public void setMaxAttempts(int maxAttempt) {
		this.maxAttempts = maxAttempt;
	}

	/**
	 * Prepare question (generate student sources and POM)
	 * 
	 * @return
	 */
	public void prepare() {
		if(prepared) {
			getLog().warn("Session already prepared. Buggy client?");
			return;
		}

		OunitTask task = startPrepare();
		OunitResult r = OunitApplication.waitForTask(task);
		
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
		
		waitForNextMtimeSlot();
		prepared = true;
	}
	
	/**
	 * Calculate checksum of the download file.
	 * We want a content based checksum that is not affected by modification
	 * dates. Therefore we will concatenate all filenames along with their
	 * CRC checksums and then calculate an MD5 hash over the resulting string.
	 *  
	 * @return a 32 character hex string
	 */
	private String getDownloadChecksum() {
		if(downloadChecksum != null)
			return downloadChecksum;
		
		File f = getDownloadFile();
		if (f == null)
			return null;

		String chain = "";
		try {
			ZipFile zf = new ZipFile(f);
			Enumeration<? extends ZipEntry> i = zf.entries();
			while(i.hasMoreElements()) {
				ZipEntry e = i.nextElement();
				long crc = e.getCrc();
				assert crc != -1: "Download files without CRC checking are not supported";
				chain += e.getName();
				chain += Long.toHexString(crc);
			}
			MessageDigest m = MessageDigest.getInstance("MD5");
			byte[] digest = m.digest(chain.getBytes());
			BigInteger bi = new BigInteger(1, digest);
			return String.format("%1$032x", bi);
		} catch (Exception e) {
			throw new RuntimeException("Unable to calculate download checksum", e);
		}
	}
	
	/**
	 * Request a Maven build to prepare the question.
	 * 
	 * @return
	 */
	private OunitTask startPrepare() {
		getLog().debug("Preparation phase of session {} started", getId());
		
		File qDir = getQuestion().getSrcDir();
		getLog().debug("Found question in {}", qDir);
		
		if(projDir.isDirectory())
			throw new RuntimeException("Directory " + projDir + " already exists");
		projDir.mkdirs();
		
		getLog().debug("Preparing question from {} to {}",
					new Object[] { qDir, projDir });
			
		OunitTask task = OunitApplication.scheduleTask(new OunitExecutionRequest()
			.setBaseDirectory(qDir)
			.setOutputDirectory(projDir.getAbsolutePath())
			.setLogFile(new File(projDir, PREPARE_LOG)));
			
		return task;
	}

	/**
	 * Execute a build in project directory.
	 * 
	 * @return true if build was successful
	 */
	public boolean build() {
		OunitTask task = startBuild();
		OunitResult r = OunitApplication.waitForTask(task);

		if(r.hasErrors()) {
			// Dump compiler errors into results file
			// TODO: Create a proper Wicket template for this
			File rf = getResultsFile();
			getLog().debug("Build failed with errors: {}", r.getErrors());
			rf.getParentFile().mkdirs();
			try {
				FileWriter wr = new FileWriter(rf);
				wr.append("<pre>");
				wr.append(r.getErrors());
				wr.append("</pre>");
				wr.close();
			} catch(Exception e) {
				throw new RuntimeException("Failed to save result", e);
			}
		}
		
		waitForNextMtimeSlot();
		
		return r.hasErrors();
	}
	
	/**
	 * Request a maven build
	 * @return
	 */
	private OunitTask startBuild() {
		getLog().debug("Build of session {} started", getId());
		
		if(!projDir.isDirectory()) throw new RuntimeException("Attempted to compile a stale session");
		
		OunitTask task = OunitApplication.scheduleTask(new OunitExecutionRequest()
			.setBaseDirectory(projDir)
			.setLogFile(new File(projDir, BUILD_LOG)));
		
		return task;
	}

	/**
	 * There are many file systems out there that store modification
	 * time in seconds. This function tries to detect the situation
	 * and wait for the next full second to elapse.
	 * If we fail to wait, the compiler may not detect that files
	 * have been changed. It may seem strange that projects can be
	 * recompiled multiple times inside a same second, but this is
	 * exactly what happens when LMS replays the session. 
	 */
	private void waitForNextMtimeSlot() {
		long mtime = projDir.lastModified();

		if (mtime % 1000 == 0) {
			// Probability of hitting a timestamp that falls exactly on full
			// second is very low thus we use the fact to decide that
			// the file system stores mtimes in seconds
			long currentTime = System.currentTimeMillis();
			long sleepTime = 1001 - currentTime % 1000;
			getLog().debug("Sleeping for {}ms", sleepTime);
			try {
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
				// Eat it !
			}
		}
	}
}