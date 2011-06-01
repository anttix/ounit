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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;

import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionResult;
import org.junit.*;

import com.googlecode.ounit.executor.MavenRunner;

import static org.junit.Assert.*;
import static com.googlecode.ounit.executor.Util.*;

public class MavenRunnerTest {
	private static MavenRunner mvn;
	private static ArrayList<String> delDirs = new ArrayList<String>();

	private static MavenExecutionResult execute(File dir, String goal) {
		return execute(dir, goal, null);
	}

	private static MavenExecutionResult execute(File dir, String goal,
			String outDir) {

		MavenExecutionResult r = mvn.execute(dir, goal, outDir);
		addDelDir(r);

		return r;
	}
	
	private static void addDelDir(MavenExecutionResult r) {
		try {
			String d = r.getProject().getBuild().getDirectory();

			if (!delDirs.contains(d))
				delDirs.add(d);

		} catch (NullPointerException e) {
		}
	}
	

	@BeforeClass
	public static void createMavenRunner() throws Exception {
		mvn = new MavenRunner();
	}

	@AfterClass
	public static void disposeMavenRunner() {
		mvn = null;
	}
	
	@AfterClass
	public static void cleanTargetDirs() {
		for (String dir : delDirs) {
			System.out.println("cleanTargetDirs: removing directory " + dir);
			deleteDirectory(dir);
		}
	}

	@Test
	public void compileSimpleProject() {
		File dir = fromResources(TP3);
		MavenExecutionResult r = execute(dir, "compile");
		for(Throwable t: r.getExceptions())
			t.printStackTrace();
		assertFalse("TP1 build failed", r.hasExceptions());
	}
	
	@Test
	public void compileProjectThatHasErrors() {
		File dir = fromResources(TP2);
		MavenExecutionResult r = execute(dir, "compile");
		assertTrue("TP2 build did not fail", r.hasExceptions());
	}
	
	@Test
	public void testSimpleProject() {
		File dir = fromResources(TP3);
		MavenExecutionResult r = execute(dir, "test");
		assertFalse("TP3 build failed", r.hasExceptions());		
	}

	@Test
	public void testBuildLogRedirection() {
		AssertionError err = null;
		File dir = fromResources(TP1);
		ByteArrayOutputStream os1 = new ByteArrayOutputStream();
		ByteArrayOutputStream os2 = new ByteArrayOutputStream();
		int oldLogLevel = mvn.getLogLevel();
		mvn.setLogLevel(MavenExecutionRequest.LOGGING_LEVEL_INFO);

		try {
			mvn.setLog(os1);
			execute(dir, "compile");

			int len1 = os1.toString().length();
			assertTrue("No log output produced to first logStream", len1 > 32);

			mvn.setLog(os2);
			execute(dir, "compile");

			int len2 = os2.toString().length();
			assertTrue("No log output produced to second logStream", len2 > 32);

			int newLen1 = os1.toString().length();
			assertTrue("Data appended to old logstream", newLen1 == len1);
		} catch (AssertionError e) {
			err = e;
		}

		// Restore logging settings
		mvn.setLogLevel(oldLogLevel);
		mvn.setLog(System.out);

		if (err != null)
			throw err;
	}

	@Test
	public void buildToDifferentOuputDirectory() {
		File dir = fromResources(TP1);
		String outDir = dir.getAbsolutePath() + "/target/out2";
		MavenExecutionResult r = mvn.execute(dir, "compile", outDir);
		assertEquals("Invalid outputDirectory", outDir, r.getProject().getBuild().getDirectory());
	}

	@Ignore
	@Test
	public void testIfMalformedPomProducesErrors() {
		// TODO
	}
	
	@Ignore
	@Test
	public void testIfProgressListenerWorks() {
		// TODO
	}
}