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

import static org.junit.Assert.*;
import static com.googlecode.ounit.executor.Util.*;

import java.io.File;
import java.util.ArrayList;

import org.junit.*;

public class OunitExecutorTest {
	private static OunitExecutor oe;
	private static ArrayList<String> delDirs = new ArrayList<String>();

	int nConcurrent = 3;

	@BeforeClass
	public static void setupThreadPool() {
		oe = new OunitExecutor();
	}

	@AfterClass
	public static void shutdownThreadPool() {
		oe.shutdown();
	}
	
	@AfterClass
	public static void cleanTargetDirs() {
		for (String dir : delDirs) {
			System.out.println("cleanTargetDirs: removing directory " + dir);
			deleteDirectory(dir);
		}
		delDirs.clear();
	}
	
	public static void addDelDir(OunitResult r) {
		String d = r.getOutputDirectory();

		if(d == null) return;

		if (!delDirs.contains(d))
			delDirs.add(d);
	}

	private OunitExecutionRequest newRequest(String name) {
		File dir = fromResources( name );

		return new OunitExecutionRequest()
		    .setBaseDirectory(dir);
	}

	@Test
	public void runSuccessfulTask() throws Exception {
		OunitTask task = oe.submit( newRequest(TP3) );
		while(!task.isDone()) {
			Thread.sleep(200);
		}
		OunitResult r = task.get();
		addDelDir(r);
		assertFalse("TP3 build failed", r.hasErrors());
	}

	@Test
	public void runCompileFailingTask() throws Exception {
		OunitTask task = oe.submit( newRequest(TP2) );
		while(!task.isDone()) {
			Thread.sleep(200);
		}
		OunitResult r = task.get();
		addDelDir(r);
		assertTrue("TP2 build did not fail", r.hasErrors());
		assertTrue("TP2 has no compile errors", r.hasCompileErrors());
		assertTrue("Error message not available", r.getErrors() != null);

		/*
		 * FIXME: There must be a better way to detect if the correct compiler
		 * (eclipse) is used.
		 */
		assertEquals("Invalid number of error messages. Wrong compiler?", 8, r
				.getErrors().split("\n").length);
	}
	
	@Test
	public void runTestFailingTask() throws Exception {
		OunitTask task = oe.submit( newRequest(TP4) );
		while(!task.isDone()) {
			Thread.sleep(200);
		}
		OunitResult r = task.get();
		addDelDir(r);
		assertTrue("TP4 build did not fail", r.hasErrors());
		assertTrue("TP4 has no test errors", r.hasTestErrors());
	}
	
	@Test
	public void testConcurrentTasks() throws Exception {
		OunitExecutionRequest [] requests = new OunitExecutionRequest[nConcurrent];
		File dir = fromResources( TP3 );
		for(int i = 0; i < requests.length; i++) {
			String outDir = dir.getAbsolutePath() + "/target_" + (i + 1);
			File logFile = new File(outDir + "/build.log");
			logFile.getParentFile().mkdirs();

			requests[i] = new OunitExecutionRequest()
				.setBaseDirectory(dir)
				.setOutputDirectory(outDir)
				.setLogFile( logFile );
		}

		ArrayList<OunitTask> tasks = new ArrayList<OunitTask>();
		
		for(OunitExecutionRequest r: requests) {
			tasks.add( oe.submit( r ) );
		}

		boolean allDone;
		do {
			Thread.sleep(200);

			allDone = true;
			for(OunitTask t: tasks) {
				if(!t.isDone()) {
					allDone = false;
					break;
				}
			}
		} while(!allDone);
		
		for(OunitTask t: tasks) {
			OunitResult r = t.get();
			// TODO: Dump failed build log to standard output on failure
			assertFalse("One of the parallel builds failed", r.hasErrors());
			addDelDir(r);
		}
	}
}
