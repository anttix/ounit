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
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.apache.wicket.Session;
import org.apache.wicket.request.Request;
import org.apache.wicket.session.ISessionStore;

public class OpaqueSessionStore implements ISessionStore {
	//private final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(this.getClass());
	private final Set<UnboundListener> unboundListeners = new CopyOnWriteArraySet<UnboundListener>();
	
	/*
	final Map<String, Serializable> attributes = Collections
	.synchronizedMap(new HashMap<String, Serializable>());
	*/

	@Override
	public Serializable getAttribute(Request request, String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getAttributeNames(Request request) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setAttribute(Request request, String name, Serializable value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeAttribute(Request request, String name) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void invalidate(Request request) {
		// TODO Auto-generated method stub
		
		//for (UnboundListener l : unboundListeners) {
		//	l.sessionUnbound(sessId);
		//}
		
	}

	@Override
	public String getSessionId(Request request, boolean create) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Session lookup(Request request) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void bind(Request request, Session newSession) {
		// TODO
		//if(request instanceof OpaqueRequest) {
		//}		
	}

	@Override
	public void flushSession(Request request, Session session) {
		// TODO Auto-generated method stub
		
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