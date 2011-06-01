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

package com.googlecode.ounit.executor;

import java.io.File;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class OunitExecutor {
	ExecutorService executor;
	MavenRunner parser = null;

	public OunitExecutor() {
		executor = Executors.newFixedThreadPool(5);
	}
	
	public OunitExecutor(int nThreads) {
		executor = Executors.newFixedThreadPool(nThreads);
	}

	public OunitTask submit(OunitExecutionRequest request) {
		OunitTask task = new OunitTask( request );
		executor.submit( task );

		return task;
	}

	public void shutdown() {
		executor.shutdown();
	}
	
	public Properties getModelProperties(File dir) throws Exception  {
		if(parser == null)
			parser = new MavenRunner();
		
		return parser.getModelProperties(dir);
	}
}
