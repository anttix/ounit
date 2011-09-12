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

import org.apache.wicket.Page;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.session.ISessionStore;
import org.apache.wicket.util.IProvider;

public class OpaqueApplication extends WebApplication {
	Class<? extends Page> homePage;
	private ISessionStore sessionStore;

	@Override
	public Class<? extends Page> getHomePage() {
		return homePage;
	}

	public void setHomePage(Class<? extends Page> homePage) {
		this.homePage = homePage;
	}
	
	
	public void setSessionStore(ISessionStore sessionStore) {
		this.sessionStore = sessionStore; 
	}
	
	@Override
	protected void init() {
		super.init();
		if (sessionStore != null) {
			/* Set session store provider */
			setSessionStoreProvider(new IProvider<ISessionStore>() {
				@Override
				public ISessionStore get() {
					return sessionStore;
				}
			});
		}
	}
	
	@Override
	public String getInitParameter(String key) {
		return null;
	}
}
