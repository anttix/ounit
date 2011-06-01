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
 * 
 * Contains pieces of code from OpenMark online assessment system
 * SVN r437 (2011-04-21)
 * 
 * Copyright (C) 2007 The Open University
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package com.googlecode.ounit.opaque;

import javax.jws.WebService;
import javax.jws.WebParam;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.jws.soap.SOAPBinding.Style;

/*
 * API CLASS: This class is used in SOAP returns and should not be altered   
 */

/**
 * OPAQUE Protocol compliant Question engine.
 * 
 * This JSR-181 annotated interface contains all public methods of
 * the OPAQUE API.
 * 
 * We are not using exactly the same WSDL as OpenMark does, mainly
 * because:
 * <ol>
 * <li>RPC/encoded is deprecated and not supported by modern libraries</li>
 * <li>RPC/encoded is not WS-I compliant</li>
 * <li>We are targeting the Moodle OPAQUE plugin and PHP SOAP bindings NOT
 *    Om-s own TestNavigator.</li>
 * </ol>
 *
 * By default Apache Axis2 and CXF use Document/literal wrapped pattern.
 * Unfortunately this requires manual (un-)wrapping in PHP. However, unwrapped Document/literal
 * is not WS-I compliant if method has multiple arguments. As a result this interface
 * operates in RPC/literal mode. 
 * 
 * @see <a href="http://docs.moodle.org/en/Development:Open_protocol_for_accessing_question_engines">OPAQUE API</a>
 * @see <a href="http://www.ibm.com/developerworks/webservices/library/ws-whichwsdl/">WSDL design patterns</a>
 */

@WebService(name = "Opaque")
@SOAPBinding(style = Style.RPC, parameterStyle = ParameterStyle.BARE)
public interface OpaqueService {
	/**
	 * Called to obtain engine name, memory usage and active session count.
	 */
	public abstract EngineStatus getEngineStatus();
	
	/**
	 * Called to obtain engine name, memory usage and active session count.
	 * This is a compatibility function.
	 * @see #getEngineStatus
	 *
	 * @return An XML string with information about the question engine status.
	 */
	
	/* Must return an XML like this:
	 * <engineinfo>
	 *   <name>My Question Engine</name> <!-- Required -->
	 *   <usedmemory>123 bytes or 45 KB or 67 MB</usedmemory> <!-- Optional -->
	 *   <activesessions>9</activesessions> <!-- Optional -->
	 * </engineinfo>
	 *
	 */
	@Deprecated
	public String getEngineInfo();
	
	/**
	 * Called to obtain question metadata and scoring information.
	 * 
	 * @param questionID Unique ID of question
	 * @param questionVersion Version identifier of question (eg 1.0)
	 *   OpenMark JavaDoc says "May be null, to indicate that the question may not be cached (for
	 *   preview usage only)". However, this behaviour is not implemented in Moodle module!
	 * @param questionBaseURL Base URL for questions
	 * @return Metadata about the question
	 * @throws OpaqueException
	 */
	public abstract QuestionInfo getQuestionInfo(
			@WebParam(name = "questionID") String questionID,
			@WebParam(name = "questionVersion") String questionVersion,
			@WebParam(name = "questionBaseURL") String questionBaseURL)
	        throws OpaqueException;
	
	/**
	 * Called to obtain question metadata and scoring information.
	 * This is a compatibility function.
	 * 
	 * @see #getQuestionInfo
	 * 
	 * @return An XML string with information about the question
	 */

	/* Must return an XML like this:
	 * <questionmetadata>
	 *   <scoring> 
	 *     <marks>3</marks> <!-- Maximum score for this question -->
	 *   </scoring> 
	 *   <plainmode>yes</plainmode> <!-- plain mode supported? -->
	 *   <title>Question title</title> <!-- Optional -->
	 * </questionmetadata>
	 */
	@Deprecated
	public String getQuestionMetadata(
			@WebParam(name = "questionID") String questionID,
			@WebParam(name = "questionVersion") String questionVersion,
			@WebParam(name = "questionBaseURL") String questionBaseURL)
			throws OpaqueException;

	/**
	 * Creates a new question session for the given question.
	 * <p>
	 * API METHOD: This method signature must not be changed in future (after
	 * initial release) unless careful attention is paid to simultaneous changes
	 * of Test Navigator. In general, if additional parameters or return values
	 * are added, a new method should be defined.
	 * <h3>Obtaining question file</h3>
	 * If the question engine already has this question in its cache, it will
	 * use that (questions of the same version are guaranteed not to change).
	 * Otherwise it retrieves questions using the following URL:
	 * <p>
	 * <i>questionBaseURL</i>/<i>questionID</i>.<i>questionversion</i>
	 * <p>
	 * For security reasons, the URL may be https: and the test navigator
	 * (which is likely to be the thing serving the URL) will operate an IP
	 * whitelist, only responding to requests from question engines it has already
	 * initiated connections to.
	 * <h3>Initial parameters</h3>
	 * initialParamNames and initialParamValues (which must be of equal length)
	 * include initial configuration parameters to be passed to the question.
	 * <table border="1">
	 * <tr><th>Name</th><th>Value</th></tr>
	 * <tr><td>randomseed</td><td>Random number seed given to question, which
	 *   should be based on the current user, the test, and the number of times
	 *   they've started this question before. Parsed as decimal integer (up to
	 *   64 bit)<br />
	 *   Moodle passes the current time in milliseconds as random seed.
	 *   Therefore you should use the lower bits of it or entropy shall be very poor.
	 *   </td></tr>
	 * <tr><td>userid</td><td>Curren user ID. Passed only by the Moodle module?</td></tr>
	 * <tr><td>language</td><td>Client language (eg en_utf8). Passed only by Moodle module?</td></tr>
	 * <tr><td>passKey</td><td>Moodle generates it like this: md5($secret . $userid)</td></tr>
	 * </table>
	 * @param questionID Unique ID of question
	 * @param questionVersion Version identifier of question (should include
	 *   only filename-space characters, probably just digits and full stops).
	 *   May be null, to indicate that the question may not be cached (for
	 *   preview usage only)
	 * @param questionBaseURL Base URL for questions (see above)
	 * @param initialParamNames Names of initial parameters
	 * @param initialParamValues Values of initial parameters
	 * @param cachedResources List of resources that the Test Navigator has cached
	 * @return Various data in order to provide the initial page of the question
	 * @throws OpaqueException Whenever something goes wrong
	 */
	public abstract StartReturn start(
			@WebParam(name = "questionID") String questionID,
			@WebParam(name = "questionVersion") String questionVersion,
			@WebParam(name = "questionBaseURL") String questionBaseURL,
			@WebParam(name = "initialParamNames") String[] initialParamNames,
			@WebParam(name = "initialParamValues") String[] initialParamValues,
			@WebParam(name = "cachedResources") String[] cachedResources)
			throws OpaqueException;
	
	/**
	 * Processes a user's action in a question session.
	 * <p>
	 * A user's action consists of a number of name-value pairs. These are generally
	 * the form parameters from the user's submission, but other information may
	 * be included here if needed.
	 * <p>
	 * If the action occurred in plain mode then the following
	 * name/value pair - "plain","yes" - <b>must</b> be included in the parameters.
	 * This is because questions can accept plain-mode actions even when the question
	 * itself was not started in plain mode (e.g. when playing through steps to
	 * restart a question that was initially attempted in plain mode).
	 * <p>
	 * API METHOD: This method signature must not be changed in future (after
	 * initial release) unless careful attention is paid to simultaneous changes
	 * of Test Navigator. In general, if additional parameters or return values
	 * are added, a new method should be defined.
	 * @param questionSession Question session ID
	 * @param names Form names in user's answer
	 * @param values Form parameters (must be an array of equal size to
	 *   names)
	 * @return New XHTML etc.
	 * @throws OpaqueException
	 */
	public abstract ProcessReturn process(
			@WebParam(name = "questionSession") String questionSession,
			@WebParam(name = "names") String[] names,
			@WebParam(name = "values") String[] values) throws OpaqueException;
	
	/**
	 * Stops a given question session, freeing up resources.
	 * <p>
	 * If a question session is not stopped after a given timeout (possibly 24
	 * hours) since last {@link #start(String,String,String,String[],String[],String[])} or
	 * {@link #process(String, String[], String[])}, the question
	 * engine should automatically time-out the session.
	 * <p>
	 * API METHOD: This method signature must not be changed in future (after
	 * initial release) unless careful attention is paid to simultaneous changes
	 * of Test Navigator. In general, if additional parameters or return values
	 * are added, a new method should be defined.
	 * @param questionSession
	 * @throws OpaqueException
	 */
	public abstract void stop(
			@WebParam(name = "questionSession") String questionSession)
			throws OpaqueException;
}
