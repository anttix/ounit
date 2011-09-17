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

import static com.googlecode.ounit.OunitUtil.*;

import java.io.File;
import java.util.Properties;

import org.apache.wicket.Application;
import org.apache.wicket.Page;
import org.apache.wicket.RuntimeConfigurationType;
import org.apache.wicket.Session;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.extensions.protocol.opaque.OpaqueApplication;
import org.apache.wicket.extensions.protocol.opaque.OpaqueQuestion;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Response;
import org.apache.wicket.session.ISessionStore.UnboundListener;

import com.googlecode.ounit.executor.OunitExecutionRequest;
import com.googlecode.ounit.executor.OunitExecutor;
import com.googlecode.ounit.executor.OunitResult;
import com.googlecode.ounit.executor.OunitTask;
import com.googlecode.ounit.opaque.OpaqueException;

public class OunitApplication extends OpaqueApplication {
	
	protected QuestionFactory qf = new DefaultQuestionFactory();
	
	
	@Override
	public Class<? extends Page> getHomePage() {
		return WelcomePage.class;
	}
	
	@Override
	public RuntimeConfigurationType getConfigurationType() {
		return RuntimeConfigurationType.DEPLOYMENT;
	}
	
	@Override
	protected void init() {
		super.init();
		getRootRequestMapperAsCompound().add(new OunitRequestMapper());
		getSessionStore().registerUnboundListener(new UnboundListener() {
			@Override
			public void sessionUnbound(String sessionId) {
				deleteDirectory(new File(OunitSession.sessDir, sessionId));
			}
		});
	}

	@Override
	public OpaqueQuestion fetchQuestion(String id, String version,
			String baseUrl) throws OpaqueException {
		
		return qf.loadQuestion(id, version, baseUrl);
	}
	
	@Override
	public Session newSession(Request request, Response response) {
		return new OunitSession(request);
	}
	
	public static OunitApplication get() {
		Application app = Application.get();

		if (!(app instanceof OunitApplication)) {
			throw new WicketRuntimeException(
					"The application attached to the current " +
					"thread is not an OunitApplication");
		}

		return (OunitApplication) app;
	}
		
	public static OunitResult waitForTask(OunitTask task)
			throws RuntimeException {
		try {
			while(!task.isDone()) {
				Thread.sleep(200);
			}
			return task.get();
		} catch(Exception e) {
			//slog.warn("Failed task", e);
			throw new RuntimeException((e.getCause() == null) ? e : e.getCause());
		}
	}

	/* Executor access must be synchronized */
	static OunitExecutor oe = null;
	
	public static synchronized OunitTask scheduleTask(OunitExecutionRequest r) {
		return getExecutor().submit(r);
	}
	
	public static synchronized Properties getModelProperties(File outDir) {
		try {
			return getExecutor().getModelProperties(outDir);
		} catch (Exception e) {
			throw new RuntimeException("Unable to parse " + outDir + "/pom.xml", e);
		}
	}
	
	private static synchronized OunitExecutor getExecutor() {
		if(oe == null)
			oe = new OunitExecutor();

		return oe;
	}
}