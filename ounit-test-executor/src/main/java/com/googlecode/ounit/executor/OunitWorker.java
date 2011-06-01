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
import java.io.FileNotFoundException;
import java.io.PrintStream;

import org.apache.maven.cli.PrintStreamLogger;
import org.apache.maven.cli.PrintStreamLogger2.Provider;

/**
 * Implements a thread safe worker object factory 
 * that can be used to execute the test requests. 
 * 
 * @author <a href="mailto:anttix@users.sourceforge.net">Antti Andreimann</a>
 *
 */
public class OunitWorker implements Provider {
	private static OunitWorker instance = null;
	private MavenRunner mvn;
	private ThreadLocal<PrintStream> log;
	
	private OunitWorker() throws Exception {
		mvn = new MavenRunner( new PrintStreamLogger( this ) );
		log = new ThreadLocal<PrintStream>() {
			@Override
			protected PrintStream initialValue() {
				return System.out;
			}
		};
	}

	public PrintStream getStream() {
		return log.get();
	}

	public static OunitWorker getInstance() throws Exception {
		if(instance == null)
			instance = new OunitWorker();
		
		return instance;
	}
	
	public OunitResult execute(OunitExecutionRequest r) throws FileNotFoundException {
		File logFile = r.getLogFile();
		if(logFile != null)
			log.set( new PrintStream(logFile) );
		
		OunitResult res = new OunitResult(mvn.execute(r.getBaseDirectory(),
				MavenRunner.DEFAULT_GOAL, r.getOutputDirectory()));
		
		if(logFile != null) {
			log.get().close();
			log.set( System.out );
		}

		return res;
	}
}
