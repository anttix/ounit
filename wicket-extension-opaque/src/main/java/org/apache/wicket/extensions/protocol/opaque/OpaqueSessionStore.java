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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArraySet;

import org.apache.wicket.Session;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.request.Request;
import org.apache.wicket.session.ISessionStore;
import org.apache.wicket.util.lang.Args;

public class OpaqueSessionStore implements ISessionStore {
	//private final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(this.getClass());
	private final Set<UnboundListener> unboundListeners = new CopyOnWriteArraySet<UnboundListener>();

	final Map<String, Map<String, Serializable>> attributes = Collections
			.synchronizedMap(new HashMap<String, Map<String, Serializable>>());

	final Map<String, Session> sessions = Collections
			.synchronizedMap(new HashMap<String, Session>());

	private Map<String, Serializable> getSessionMap(Request request) {
		String id = getSessionId(request, false);
		
		if(attributes.get(id) == null)
			throw new WicketRuntimeException("Stale session!");
		
		return attributes.get(id);
	}
	
	public int getActiveSessionCount() {
		return sessions.size();
	}
	
	@Override
	public Serializable getAttribute(Request request, String name) {
		return getSessionMap(request).get(name);
	}

	@Override
	public List<String> getAttributeNames(Request request) {
		return new ArrayList<String>(getSessionMap(request).keySet());
	}

	@Override
	public void setAttribute(Request request, String name, Serializable value) {
		getSessionMap(request).put(name, value);		
	}

	@Override
	public void removeAttribute(Request request, String name) {
		getSessionMap(request).remove(name);		
	}

	@Override
	public void invalidate(Request request) {
		String id = getSessionId(request, false);
		attributes.remove(id);
		
		for (UnboundListener l : unboundListeners)
		{
			l.sessionUnbound(id);
		}
	}

	@Override
	public String getSessionId(Request request, boolean create) {
		//if(request instanceof OpaqueRequest)
		OpaqueRequest rq = (OpaqueRequest)request;
		String id = ((OpaqueRequest)request).getSessionId();
		
		if(id == null && create) {
			if(rq.callType != OpaqueRequest.CallType.START) {
				throw new WicketRuntimeException("Stale OPAQUE session!");
			}
			id = UUID.randomUUID().toString().replaceAll("-", "");
			rq.setSessionId(id);
		}

		return id;
	}

	@Override
	public Session lookup(Request request) {
		String id = getSessionId(request, false);
		Session rv = sessions.get(id);
		OpaqueRequest rq = (OpaqueRequest)request;
		if(rq.callType == OpaqueRequest.CallType.PROCESS && rv == null)
			/* LMS should now request a new question session and replay all user responses */
			throw new WicketRuntimeException("Stale OPAQUE session!");

		return rv;
	}

	@Override
	public void bind(Request request, Session newSession) {
		String id = getSessionId(request, false);
		Args.notEmpty(id, "Session ID");
		sessions.put(id, newSession);
	}

	@Override
	public void flushSession(Request request, Session session) {
		bind(request, session);
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
	}

	@Override
	public void registerUnboundListener(UnboundListener listener)
	{
		unboundListeners.add(listener);
	}
	
	@Override
	public void unregisterUnboundListener(UnboundListener listener) {
		unboundListeners.remove(listener);
	}
	
	@Override
	public Set<UnboundListener> getUnboundListener() {
		return Collections.unmodifiableSet(unboundListeners);
	}
}