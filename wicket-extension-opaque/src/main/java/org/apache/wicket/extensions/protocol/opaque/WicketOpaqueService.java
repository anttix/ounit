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
	
	protected OpaqueApplication app;
	protected PageRunner renderer;

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
		rv.setActivesessions(app.getActiveSessions());
		
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
		
		OpaqueQuestion q = app.fetchQuestion(questionID, questionVersion,
				questionBaseURL);
		QuestionInfo rv = q.getInfo();
		
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
		
		OpaqueQuestion question = app.fetchQuestion(questionID,
				questionVersion, questionBaseURL);

		StartReturn rv = new StartReturn();
		OpaqueRequest request = new OpaqueRequest(question, initialParamNames,
				initialParamValues, cachedResources);

		renderer.execute(request, rv);

		return rv;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public ProcessReturn process(String questionSession, String[] names,
			String[] values) throws OpaqueException {
		
		log.debug("process({}, {}, {}, {}, {}, {})", new Object[] {
				questionSession, names, values });
		
		if(questionSession == null)
			throw new OpaqueException("questionSession must be present");

		ProcessReturn rv = new ProcessReturn();
		OpaqueRequest request = new OpaqueRequest(questionSession, names,
				values);
		renderer.execute(request, rv);
		
		return rv;
	}

	/**
	 * {@inheritDoc}
	 */
	public void stop(String questionSession) throws OpaqueException {
		log.debug("stop({})", questionSession);
		
		if(questionSession == null)
			throw new OpaqueException("questionSession must be present");
		
		OpaqueRequest request = new OpaqueRequest(questionSession, null);
		app.getSessionStore().invalidate(request);
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
}