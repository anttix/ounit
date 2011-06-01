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

import org.junit.*;
//import static org.junit.Assert.*;
//import static com.googlecode.ounit.executor.Util.*;

/**
 * These tests generate considerable load on the system in order
 * to try to crash it or make sure it runs out of memory.
 * These tests take considerable time to complete
 * thus they are not included in default build.
 * 
 * You can run these tests from command line with
 *     mvn -Dtest=StressTests test
 * 
 * @author <a href="mailto:anttix@users.sourceforge.net">Antti Andreimann</a>
 *
 */
public class StressTests {
	/**
	 * This test will execute 500 tasks in batches of 10 in order to test 
	 * the systems reliability. The test can take up to 30 minutes to complete.
	 */
	@Test
	public void reliabilityTest() throws Exception {
		OunitExecutorTest.setupThreadPool();
		OunitExecutorTest t = new OunitExecutorTest();
		t.nConcurrent = 10;

		for(int i = 1; i <= 50; i++) {
			System.out.println("Running set " + i);
			t.testConcurrentTasks();
			OunitExecutorTest.cleanTargetDirs();
		}

		OunitExecutorTest.shutdownThreadPool();
	}
}
