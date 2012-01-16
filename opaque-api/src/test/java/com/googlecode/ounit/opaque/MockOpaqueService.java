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

package com.googlecode.ounit.opaque;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

import static com.googlecode.ounit.opaque.OpaqueUtils.*;

/**
 * A minimal implementation of a question engine.
 * Can be used to test engine clients.
 * 
 * @author anttix
 *
 */
@WebService(serviceName="MockOpaque")
@SOAPBinding(style = Style.RPC)
public class MockOpaqueService implements OpaqueService {
	
	class MockSession {
		public String [] cachedResources;
	}
	Map<String, MockSession> sessions;
	
	List <Resource> engineResources;
	
	protected void debug(String s) {
		System.err.println("DEBUG: " + s);
	}
	
	public MockOpaqueService() {
		super();
		debug("MockOpaqueService()");
		
		sessions = Collections.synchronizedMap(new HashMap<String, MockSession>(1024));
		engineResources = new ArrayList<Resource>(5);
		
		/* 1x1 GIF image with black foreground - 35bytes of data */
		byte [] pic = { (byte) 0x47, (byte) 0x49, (byte) 0x46, (byte) 0x38, (byte) 0x37, (byte) 0x61,
						(byte) 0x01, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x80, (byte) 0x01,
				        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0xff, (byte) 0xff,
				        (byte) 0xff, (byte) 0x2c, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
				        (byte) 0x01, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x02,
				        (byte) 0x02, (byte) 0x44, (byte) 0x01, (byte) 0x00, (byte) 0x3b };
		
		engineResources.add(new Resource("mockpic.gif", "image/gif", pic));

		String script = "function mockHideDiv(id) {" +
				" document.getElementById(id).style.display = \"none\"; }";
		engineResources.add(new Resource("mockscript.js", "text/javascript",
				"UTF-8", script.getBytes()));
	}
	
	/**
	 * {@inheritDoc}
	 */
	public EngineStatus getEngineStatus() 
	{
		debug("getEngineStatus()");
		
		EngineStatus rv = new EngineStatus();
		rv.setName("Mock OPAQUE question engine");
		rv.setUsedmemory(getJvmMem());
		rv.setActivesessions(sessions.size());
		
		return rv;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public QuestionInfo getQuestionInfo(
			String questionID, String questionVersion, String questionBaseURL) throws OpaqueException {
		
		debug("getQuestionInfo(" + questionID + ", " + questionVersion + ", " + questionBaseURL + ")");
		
		if(questionID == null || questionVersion == null)
			throw new OpaqueException("questionID and questionVersion must be present");
		
		QuestionInfo rv = new QuestionInfo();
		rv.setMaxScore(3);
		
		/* Moodle currently does not display it */
		rv.setTitle("Mock Question");
		return rv;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public StartReturn start(String questionID, String questionVersion,
			String questionBaseURL, String[] initialParamNames,
			String[] initialParamValues, String[] cachedResources)
			throws OpaqueException {
		
		debug("start(" + questionID + ", " + questionVersion + ","
				+ questionBaseURL + "," + formatArray(initialParamNames)
				+ "," + formatArray(initialParamValues) + ","
				+ formatArray(cachedResources) + ")");

		String sessId = UUID.randomUUID().toString().substring(0, 8);
		debug("sessId = " + sessId);

		MockSession session = new MockSession();
		session.cachedResources = cachedResources;
		sessions.put(sessId, session);
		
		StartReturn rv = new StartReturn(sessId);
		populateReturn(rv, "none yet", sessId);
		
		return rv;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public ProcessReturn process(String questionSession, String[] names,
			String[] values) throws OpaqueException {
		// FIXME: handle null parameters correctly
		debug("process(" + questionSession + ", " + formatArray(names) + ", "
				+ formatArray(values) + ")");
		
		Map <String, String> params = makeMap(names, values);
		
		ProcessReturn rv = new ProcessReturn();
		
		/* This is not shown by Moodle, however it is parsed to find
		 * the number of retries. Words one and last count as 1 and any digit
		 * is interpreted as the number of retries.
		 *  
		 * Bleh! How UGLY is THAT!
		 * 
		 * And in the end, it doesn't seem to work at all.
		 */
		rv.setProgressInfo("You have 3 attempts left");
		
		/* QuestionEnd means that this was the final page and no more
		 * Processing should be done. However, if we want to show a
		 * Results page (we usually do), then we should never set it to true.
		 * Moodle will close the question as soon as any results are set anyway.
		 * If queston end is set, moodle will stop processing and
		 * shall display the HTML it received from the LAST OPERATION,
		 * NOT the one sent along with the end response.
		 * This HTML will later be stored for review purposes and shown to the
		 * student so it better be something meaningful.
		 */
		rv.setQuestionEnd(false);
		
		int moodleEvent = 0;
		try {
			moodleEvent = Integer.parseInt(params.get("event"));
		} catch(Exception e) { }
		
		
		int nr = -1;
		try {
			nr = Integer.parseInt(params.get("nr"));
		} catch(Exception e) { }
		
		
		boolean done = true;
		Results r = new Results();
		
		/* Moodle shows this in the "History of Responses"
		 * It is also used in an "Item analysis" table.
		 * (on a question review page). As well as sends it back to us when it is
		 * replaying the question. */
		r.setAnswerLine("Mock answer line");
		
		/* Used by moodle for the same purposes if answerLine is not available */
		r.setActionSummary("Mock actionsummary");
		
		/* Simply added to history along with all the input data sent to server if answer line
		 * and action summary are unavailable */
		r.setQuestionLine("Mock question line");
		
		/* The attempts constans are not used by Moodle for anything.
		 * However if attempts > 0, some funky logic kicks in.
		 */
		switch (nr) {
		case 0:
			r.addScore(nr, Results.ATTEMPTS_WRONG);
			break;
		case 1:
		case 2:
			r.addScore(nr, Results.ATTEMPTS_PARTIALLYCORRECT);
			break;
		case 3:
			r.addScore(nr, Results.ATTEMPTS_PASS);
			break;
		default:
			done = false;
			break;

		}
		if(done) {
			rv.setResults(r);
		}
		
		switch(moodleEvent) {
		/* Moodle reports that it has already closed and graded the question
		 * We should not show any forms as a result anymore.
		 */
		case MOODLE_EVENTCLOSEANDGRADE:
			done = true;
			break;
		case MOODLE_EVENTSAVE:
			/* Moodle reports that the user saved the question.
			 * This will be sent fo both, page save and navigate away operations.
			 * Also for any requests sent due to button clicks inside Opaque question
			 */
			break;
		case MOODLE_EVENTSUBMIT:
			/* This is only used in "adaptive" mode. The user has asked the engine to
			 * Grade and return the result. */
			break;
		}
		
		if(done) {
			rv.setXHTML("Congratz! You are done! Your answer was: " + params.get("nr"));
			
			/* + "<input type=\"submit\" name=\"%%IDPREFIX%%retry\" value=\"Retry\"/>");
			   + "<input type=\"hidden\" name=\"%%IDPREFIX%%nr\" value=\"5\"/>"
			   + "<input type=\"hidden\" name=\"%%IDPREFIX%%done\" value=\"1\">"); */
			
		} else {
			populateReturn(rv, params.get("nr"), questionSession);
		}

		return rv;
	}
	
	private void populateReturn(ReturnBase rv, String nr, String questionSession) {
		rv.setXHTML("\n\n<!-- MockOpaqueService Question START -->\n"
				+ "<div class=\"quizdiv\">Enter a number of points <br />\n"
				+ "(0-3 score points, others let you retry) \n"
				+ "<input type=\"text\" name=\"%%IDPREFIX%%nr\"/>\n"
				+ "<input type=\"submit\" name=\"%%IDPREFIX%%go\" value=\"Go\" />\n"
				+ "</div>\n<div>Last Reply: <span class=\"lastreply\">" + nr + "</span></div>\n"
				+ "<div class=\"picdiv\">" 
				+ "If you can read this and do NOT see a black box, the resource image failed to load "
				+ "<img id=\"%%IDPREFIX%%pic\" src=\"%%RESOURCES%%/mockpic.gif\" "
				+ "     width=\"50\" height=\"50\" /></div>\n"
				+ "<div class=\"headdiv\">"
				+ "If you can read this, header injection did not work (only Moodle supports it)</div>\n"
				+ "<div class=\"cssdiv\">If You can read this, CSS was not loaded</div>\n"
				+ "<div id=\"%%IDPREFIX%%jsdiv\" class=\"jsdiv\">If You can read this, JS was not loaded</div>\n"
				+ "<script type=\"text/javascript\" src=\"%%RESOURCES%%/mockscript.js\"></script>\n"
				+ "<script type=\"text/javascript\">mockHideDiv('%%IDPREFIX%%jsdiv');</script>\n"
				+ "<script type=\"text/javascript\">"
				+ "<!-- http://snippets.dzone.com/posts/show/89 -->\n"
				+ "function hideImgDiv() {\n"
				+ "  var img = document.getElementById('%%IDPREFIX%%pic');\n"
				+ "  if(img.complete && (typeof img.naturalWidth == 'undefined' "
				+ "|| img.naturalWidth != 0)) { \n"
				+ "      img.parentNode.style.display = 'none';\n"
				+ "      return true; } else { return false; }"
				+ "}\n if(!hideImgDiv()) setTimeout(hideImgDiv, 1000);\n"
				+ "</script>\n"
				+ "<!-- MockOpaqueService Question END -->\n\n");

		/* Resources, CSS and headers are NOT filtered for %%..%% constructs so we MUST use
		 * classes or other stuff that do not depend on these values.
		 * Normally it wouldn't be much of a problem, however relative URL-s pointing to
		 * resource files do not work from the CSS with the default implementation of moodle
		 * module. The alternative? Do things in-line.
		 * Moodle saves this to resources with a filename that
		 * contains $questionsessionid.
		 */
		rv.setCSS(".cssdiv { display: none; }");
		rv.setHead("<style type=\"text/css\">.headdiv { display: none; }</style>");
		rv.setResources(generateNewResources(engineResources,
				sessions.get(questionSession).cachedResources));
		debug("Sending " + rv.getResources().length + " new resources to client");
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void stop(String questionSession) throws OpaqueException
	{
		debug("stop(" + questionSession + ")");
		sessions.remove(questionSession);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public String getEngineInfo() {
		return makeEngineXML(getEngineStatus());
	}
	
	/**
	 * {@inheritDoc}
	 */
	public String getQuestionMetadata(String questionID,
			String questionVersion, String questionBaseURL) throws OpaqueException {
		return makeQuestionXML(getQuestionInfo(questionID, questionVersion, questionBaseURL));
	}
}