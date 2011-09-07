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
import static com.googlecode.ounit.OunitConfig.*;
import static com.googlecode.ounit.OunitUtil.*;

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
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
import com.googlecode.ounit.opaque.Resource;
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
	
	Map<String, EngineSession> sessions = 
		Collections.synchronizedMap(new HashMap<String, EngineSession>());
	PageRunner renderer;
	QuestionFactory qf;
	
	/* Temporary directory to hold user files (aka sessions) */
	static final File sessDir = new File(WORKDIR, SESSION_DIR);
		
	public OunitService() {
		log.debug("OunitService()");
		renderer = new PageRunner(new OunitApplication()); 
		qf = new DefaultQuestionFactory();
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
		
		OunitQuestion question = qf.loadQuestion(questionID, questionVersion, questionBaseURL);
		Properties qprops = getModelProperties(question.getSrcDir());
		
		int maxScore;
		try {
			maxScore = Integer.parseInt((String)qprops.get(MARKS_PROPERTY));
		} catch(Exception e) {
			maxScore = OunitSession.DEFAULT_MARKS;
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

		/* Start new engine session */
		OunitSession context = newSession(questionID, questionVersion,
				questionBaseURL, initialParamNames, initialParamValues,
				cachedResources);
		
		/* Do not allow more than one thread to mess with a single session */
		EngineSession session = sessions.get(context.getEngineSessionId());
		assert session != null : "Engine session was not set up properly";
		
		synchronized(session) {
			StartReturn rv = new StartReturn(context.getEngineSessionId());
			renderer.doPage(context, rv);
			return rv;
		}
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
		
		synchronized(session) {
			OunitSession context = session.ounitSession;
			context.newPostParameters(names, values);
			ProcessReturn rv = new ProcessReturn();
			renderer.doPage(context, rv);
			if (context.isClosed())
				rv.setResults(context.getResults());
			
			// FIXME: Ugly, ugly, ugly hack!
			String fname = context.getDownloadFileName();
			if (fname != null && !context.getCachedResources().contains(fname)) {
				try {
					FileInputStream is = new FileInputStream(context.getDownloadFile());
					int len = (int) is.getChannel().size();
					byte[] buf = new byte[len];
					is.read(buf);
					is.close();
					Resource r = new Resource(fname,
							"application/octet-stream", buf);
					Resource[] rs = rv.getResources();
					int rlen = rs == null ? 0 : rs.length;
					Resource[] newrs = new Resource[rlen + 1];
					if (rlen > 0)
						System.arraycopy(rs, 0, newrs, 0, rlen);
					newrs[rlen] = r;
					rv.setResources(newrs);
				} catch (Exception e) {
					throw new RuntimeException(
							"Error creating download resource", e);
				}
			}

			return rv;
		}
	}

	public void stop(String questionSession) throws OpaqueException {
		log.debug("stop({})", questionSession);
		EngineSession session = sessions.get(questionSession);
		if(session == null) {
			// Stale session. Do nothing!
			return;
		}

		synchronized(session) {
			deleteDirectory(new File(sessDir, questionSession));
			sessions.remove(questionSession);
		}
	}
	
	/* End web service methods */
	

	/**
	 * Set up a new engine session.
	 */
	protected OunitSession newSession(String questionID,
			String questionVersion, String questionBaseURL,
			String[] initialParamNames, String[] initialParamValues,
			String[] cachedResources) throws OpaqueException {

		OunitQuestion question = qf.loadQuestion(questionID, questionVersion, questionBaseURL);
		EngineSession session = new EngineSession();
		File outDir = new File(sessDir, session.getId());
		OunitSession context = new OunitSession(outDir, question,
				initialParamNames, initialParamValues);
		context.setEngineSessionId(session.getId());
		log.debug("Successfully set up new engine session: {}", session.getId());
		sessions.put(session.getId(), session);
		session.ounitSession = context;
		context.setCachedResources(Arrays.asList(cachedResources));
		
		return context;
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
	
	public static synchronized OunitTask scheduleTask(OunitExecutionRequest r) {
		return getExecutor().submit(r);
	}
	
	public static synchronized Properties getModelProperties(File outDir)
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
