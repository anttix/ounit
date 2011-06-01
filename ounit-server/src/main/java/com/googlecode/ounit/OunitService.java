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

import static com.googlecode.ounit.opaque.OpaqueUtils.*;
import static com.googlecode.ounit.OunitSession.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

import org.apache.wicket.extensions.protocol.opaque.PageRunner;

import com.googlecode.ounit.executor.OunitExecutionRequest;
import com.googlecode.ounit.executor.OunitExecutor;
import com.googlecode.ounit.executor.OunitResult;
import com.googlecode.ounit.executor.OunitTask;
import com.googlecode.ounit.opaque.EngineStatus;
import com.googlecode.ounit.opaque.OpaqueException;
import com.googlecode.ounit.opaque.OpaqueService;
import com.googlecode.ounit.opaque.ProcessReturn;
import com.googlecode.ounit.opaque.QuestionInfo;
import com.googlecode.ounit.opaque.StartReturn;

@WebService(serviceName="Ounit")
@SOAPBinding(style = Style.RPC)
public class OunitService implements OpaqueService {
	
	protected class EngineSession {
		private String id;
		public OunitSession ounitSession;
		
		EngineSession() {
			id = UUID.randomUUID().toString().substring(0, 8);	
		}
		EngineSession(String id) {
			this.id = id;
		}
		public String getId() {
			return id;
		}
	}
	
	private final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(this.getClass());
	private static final org.slf4j.Logger slog = org.slf4j.LoggerFactory
			.getLogger(OunitService.class);
	
	Map<String, EngineSession> sessions = new HashMap<String, EngineSession>();
	PageRunner renderer;
	
	/* Temporary directory to hold user files (aka sessions) */
	static final File sessDir = new File(WORKDIR);
		
	public OunitService() {
		log.debug("OunitService()");
		renderer = new PageRunner(new OunitApplication());
	}
	
	public String getEngineInfo() {
		log.debug("getEngineInfo()");
		return makeEngineXML(getEngineStatus());
	}

	public EngineStatus getEngineStatus() {
		log.debug("getEngineStatus()");
		
		EngineStatus rv = new EngineStatus();
		rv.setName("OUnit question engine");
		// TODO: add Version number
		rv.setUsedmemory(getJvmMem());
		rv.setActivesessions(sessions.size());
		
		return rv;
	}

	public QuestionInfo getQuestionInfo(String questionID,
			String questionVersion, String questionBaseURL) throws OpaqueException {
		
		log.debug("getQuestionInfo({}, {}, {})",
				new Object[] { questionID, questionVersion, questionBaseURL });
		
		if(questionID == null || questionVersion == null)
			throw new OpaqueException("questionID and questionVersion must be present");
		
		File srcDir = fetchQuestion(questionID, questionVersion, questionBaseURL);
		Properties qprops = getModelProperties(srcDir);
		
		int maxScore;
		try {
			maxScore = Integer.parseInt((String)qprops.get(MARKS_PROPERTY));
		} catch(Exception e) {
			maxScore = DEFAULT_MARKS;
		}
		
		QuestionInfo rv = new QuestionInfo();
		rv.setMaxScore(maxScore);

		/* Moodle currently does not display it, but we handle it anyway */
		rv.setTitle((String)qprops.get(TITLE_PROPERTY));

		return rv;
	}

	public String getQuestionMetadata(String questionID,
			String questionVersion, String questionBaseURL) throws OpaqueException {
		log.debug("getQuestionMetadata({}, {}, {})",
				new Object[] { questionID, questionVersion, questionBaseURL });

		return makeQuestionXML(getQuestionInfo(questionID, questionVersion, questionBaseURL));
	}

	public StartReturn start(String questionID, String questionVersion,
			String questionBaseURL, String[] initialParamNames,
			String[] initialParamValues, String[] cachedResources)
			throws OpaqueException {
		
		log.debug("start({}, {}, {}, {}, {}, {})", new Object[] { questionID,
				questionVersion, questionBaseURL, initialParamNames,
				initialParamValues, cachedResources });

		/* New request. Execute question preparation phase. */
		String errstr = "Failed to prepare question " + questionID + "-" + questionVersion;
		EngineSession session = new EngineSession();
		File outDir = new File(sessDir, session.getId());
		outDir.mkdirs();

		OunitSession context = new OunitSession(outDir, initialParamNames, initialParamValues);
		context.setEngineSessionId(session.getId());		
		OunitResult r;
		try {
			File srcDir = fetchQuestion(questionID, questionVersion, questionBaseURL);
			log.debug("Preparing question from {} to {}",
					new Object[] { srcDir, outDir });
			
			OunitTask task = scheduleTask(new OunitExecutionRequest()
				.setBaseDirectory(srcDir)
				.setOutputDirectory(outDir.getAbsolutePath())
				.setLogFile(new File(outDir, PREPARE_LOG)));
			r = waitForTask(task);
		} catch (Exception e) {
			deleteDirectory(outDir);
			log.warn(errstr, e);
			throw new OpaqueException(errstr, e.getCause());
		}
		
		if(r.hasErrors()) {
			deleteDirectory(outDir);
			throw new OpaqueException(errstr + ": " + r.getErrors());
		}
		
		handleModelProps(context, outDir);

		log.debug("Successfully set up new engine session: {}", session.getId());
		sessions.put(session.getId(), session);
		
		session.ounitSession = context;
		context.setCachedResources(Arrays.asList(cachedResources));
		StartReturn rv = new StartReturn(session.getId());
		renderer.doPage(context, rv);
		
		return rv;
	}

	public ProcessReturn process(String questionSession, String[] names,
			String[] values) throws OpaqueException {
		
		log.debug("process({}, {}, {}, {}, {}, {})", new Object[] {
				questionSession, names, values });
		
		EngineSession session = sessions.get(questionSession);
		
		if(session == null) {
			throw new OpaqueException("Stale questionSession");
			/* LMS should now request a new question session and replay all user responses */
		}
		
		OunitSession context = session.ounitSession;
		context.newPostParameters(names, values);
		ProcessReturn rv = new ProcessReturn();
		renderer.doPage(context, rv);
		if (context.isClosed())
			rv.setResults(context.getResults());

		return rv;
	}

	public void stop(String questionSession) throws OpaqueException {
		log.debug("stop({})", questionSession);
		deleteDirectory(new File(sessDir, questionSession));
		sessions.remove(questionSession);
	}
	
	
	/* End web service methods */
	
	/**
	 * Fetch question from Question Database.
	 */
	private File fetchQuestion(String questionID, String questionVersion, String questionBaseURL) {
		// TODO: Make it configurable etc.
		//return new File("/srv/ounit/questions/" + questionID);
		return new File("/home/anttix/products/ounit/questions/" + questionID);
	}
	
	private static boolean deleteDirectory(File path) {
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
	
	private void handleModelProps(OunitSession context, File outDir)
			throws OpaqueException {
		Properties modelProps = getModelProperties(outDir);
		
		Object marks = modelProps.get(MARKS_PROPERTY);
		if (marks != null) {
			log.debug("Found " + MARKS_PROPERTY + " = {} in POM", marks);
			context.setMaxMarks(Integer.parseInt((String) marks));
		}
		String tmp = (String) modelProps.get(RWFILES_PROPERTY);
		if (tmp == null)
			throw new OpaqueException(
					RWFILES_PROPERTY + " missing from student pom.xml");
		List<String> editFiles = new ArrayList<String>();
		for (String f : tmp.split("\n"))
			editFiles.add(f);
		
		log.debug("Editable files loaded from POM: {}", editFiles);		
		context.setEditFiles(editFiles);
	}

	public static OunitTask requestBuild(String questionSession) {
		final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(OunitService.class);
		log.debug("Build of session {} started", questionSession);
		
		File dir = new File(sessDir, questionSession);
		if(!dir.isDirectory()) throw new RuntimeException("Attempted to compile a stale session");
		
		OunitTask task = scheduleTask(new OunitExecutionRequest()
			.setBaseDirectory(dir)
			.setLogFile(new File(dir, BUILD_LOG)));
		
		return task;
	}
	
	public static OunitResult waitForTask(OunitTask task)
			throws RuntimeException {
		try {
			while(!task.isDone()) {
				Thread.sleep(200);
			}
			return task.get();
		} catch(Exception e) {
			slog.warn("Failed task", e);
			throw new RuntimeException((e.getCause() == null) ? e : e.getCause());
		}
	}

	/* Executor access must be synchronized */
	static OunitExecutor oe = null;
	
	private static synchronized OunitTask scheduleTask(OunitExecutionRequest r) {
		return getExecutor().submit(r);
	}
	
	private static synchronized Properties getModelProperties(File outDir)
			throws OpaqueException {
		
		try {
			return getExecutor().getModelProperties(outDir);
		} catch (Exception e) {
			throw new OpaqueException("Unable to parse " + outDir + "/pom.xml", e);
		}
		
	}
	
	private static synchronized OunitExecutor getExecutor() {
		if(oe == null)
			oe = new OunitExecutor();

		return oe;
	}
}
