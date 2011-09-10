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

package org.apache.wicket.extensions.protocol.opaque;

import static com.googlecode.ounit.opaque.OpaqueUtils.*;
import static org.apache.wicket.extensions.protocol.opaque.OpaqueSession.DEFAULT_MARKS;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

import com.googlecode.ounit.opaque.EngineStatus;
import com.googlecode.ounit.opaque.OpaqueException;
import com.googlecode.ounit.opaque.OpaqueService;
import com.googlecode.ounit.opaque.ProcessReturn;
import com.googlecode.ounit.opaque.QuestionInfo;
import com.googlecode.ounit.opaque.StartReturn;

/**
 * Wicket based Opaque service abstraction.
 * <p>
 * Used like this:
 * </p>
 * <pre>
 * &#64;WebService(serviceName="MyOpaqueService")
 * &#64;SOAPBinding(style = Style.RPC)
 * public class MyOpaqueService extends WicketOpaqueService {
 *   public MockWicketService() {
 *     super(new MyOpaqueApplication());
 *   }
 *
 *   ...
 *
 * }
 * </pre>
 * 
 * @author anttix
 *
 */
@WebService(serviceName="WicketOpaqueService")
@SOAPBinding(style = Style.RPC)
public abstract class WicketOpaqueService implements OpaqueService {
	private final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(this.getClass());
	
	protected class EngineSession {
		String id;
		OpaqueSession opaqueSession;
		
		EngineSession(OpaqueSession opaqueSession) {
			this(UUID.randomUUID().toString().substring(0, 8), opaqueSession);
		}
		EngineSession(String id, OpaqueSession opaqueSession) {
			this.id = id;
			opaqueSession.setEngineSessionId(getId());
			this.opaqueSession = opaqueSession;
		}
		public String getId() {
			return id;
		}
		public OpaqueSession getOpaqueSession() {
			return opaqueSession;
		}
	}
	
	OpaqueApplication app;
	PageRunner renderer;
	Map<String, EngineSession> sessions =
		Collections.synchronizedMap(new HashMap<String, EngineSession>());

	public WicketOpaqueService(OpaqueApplication app) {
		this.app = app;
		renderer = new PageRunner(app);
	}

	/**
	 * {@inheritDoc}
	 */
	public EngineStatus getEngineStatus() {
		log.debug("getEngineStatus()");
		
		EngineStatus rv = new EngineStatus();
		rv.setName(app.getName());
		// TODO: add Version number
		rv.setUsedmemory(getJvmMem());
		rv.setActivesessions(sessions.size());
		
		return rv;
	}

	/**
	 * {@inheritDoc}
	 */
	public QuestionInfo getQuestionInfo(String questionID,
			String questionVersion, String questionBaseURL) throws OpaqueException {
		
		log.debug("getQuestionInfo({}, {}, {})",
				new Object[] { questionID, questionVersion, questionBaseURL });
		
		if(questionID == null || questionVersion == null)
			throw new OpaqueException("questionID and questionVersion must be present");
		
		QuestionInfo rv = fetchQuestionMetadata(questionID, questionVersion,
				questionBaseURL);
		
		if (rv.getMaxScore() == 0)
			rv.setMaxScore(DEFAULT_MARKS);
		
		return rv;
	}

	/**
	 * {@inheritDoc}
	 */
	public StartReturn start(String questionID, String questionVersion,
			String questionBaseURL, String[] initialParamNames,
			String[] initialParamValues, String[] cachedResources)
			throws OpaqueException {
		
		log.debug("start({}, {}, {}, {}, {}, {})", new Object[] { questionID,
				questionVersion, questionBaseURL, initialParamNames,
				initialParamValues, cachedResources });

		if(questionID == null || questionVersion == null)
			throw new OpaqueException("questionID and questionVersion must be present");
		
		/* Call fetchQuestionMetadata to make sure the question exists */ 
		fetchQuestionMetadata(questionID, questionVersion, questionBaseURL);

		EngineSession session = newEngineSession(initialParamNames,
				initialParamValues);
		OpaqueSession context = session.getOpaqueSession();
		
		log.debug("Successfully set up new engine session: {}", session.getId());

		context.setCachedResources(Arrays.asList(cachedResources));
		StartReturn rv = new StartReturn(session.getId());
		renderer.doPage(context, rv);
		
		return rv;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public ProcessReturn process(String questionSession, String[] names,
			String[] values) throws OpaqueException {
		
		log.debug("process({}, {}, {}, {}, {}, {})", new Object[] {
				questionSession, names, values });
		
		EngineSession session = sessions.get(questionSession);
		
		if(session == null) {
			throw new OpaqueException("Stale questionSession");
			/* LMS should now request a new question session and replay all user responses */
		}

		OpaqueSession context = session.getOpaqueSession();
		context.newPostParameters(names, values);
		ProcessReturn rv = new ProcessReturn();
		renderer.doPage(context, rv);
		if (context.isClosed())
			rv.setResults(context.getResults());
		
		return rv;
	}

	/**
	 * {@inheritDoc}
	 */
	public void stop(String questionSession) throws OpaqueException {
		log.debug("stop({})", questionSession);
		
		sessions.remove(questionSession);
	}
	
	/**
	 * Deprecated compatibility function.
	 * {@inheritDoc}
	 */
	public String getEngineInfo() {
		log.debug("getEngineInfo()");
		return makeEngineXML(getEngineStatus());
	}
	
	/**
	 * Deprecated compatibility function.
	 * {@inheritDoc}
	 */
	public String getQuestionMetadata(String questionID,
			String questionVersion, String questionBaseURL) throws OpaqueException {
		log.debug("getQuestionMetadata({}, {}, {})",
				new Object[] { questionID, questionVersion, questionBaseURL });

		return makeQuestionXML(getQuestionInfo(questionID, questionVersion, questionBaseURL));
	}
	
	/** 
	 * Called to create new engine session.
	 * Inherited classes may override this.
	 * 
	 * @param initialParamNames
	 * @param initialParamValues
	 * @return new engine session object
	 * @throws OpaqueException
	 */
	protected EngineSession newEngineSession(String[] initialParamNames,
			String[] initialParamValues) throws OpaqueException {
		
		EngineSession session = new EngineSession(newOpaqueSession(
				initialParamNames, initialParamValues));
		sessions.put(session.getId(), session);

		return session;
	}
	
	/**
	 * Called to create new Opaque session.
	 * Inherited classes may override this.
	 *  
	 * @param initialParamNames
	 * @param initialParamValues
	 * @return new opaque session
	 * @throws OpaqueException
	 */
	protected OpaqueSession newOpaqueSession(String[] initialParamNames,
			String[] initialParamValues) throws OpaqueException {
		
		return new OpaqueSession(initialParamNames, initialParamValues);
	}

	/**
	 * 
	 * Called from getQuestionMetadata and from start to make sure the question
	 * exists.
	 * 
	 * @param questionID
	 * @param questionVersion
	 * @param questionBaseURL
	 * @return
	 */
	protected abstract QuestionInfo fetchQuestionMetadata(String questionID,
			String questionVersion, String questionBaseURL)
			throws OpaqueException;
}