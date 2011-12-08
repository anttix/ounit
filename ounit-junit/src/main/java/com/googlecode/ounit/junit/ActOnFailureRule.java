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

package com.googlecode.ounit.junit;

import java.io.IOException;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import static com.googlecode.ounit.junit.TestUtils.*;

/**
 * A JUnit 4.x rule that can be used to act on test failures
 * 
 * <p>
 * Mostly used to save some data to file on test failures so users
 * can have more detailed feedback.
 * </p>
 * <p>
 * Use the following code to activate:
 * </p>
 * <pre>
 * public class TestBase {
 *   
 *   &#64;Rule
 *   public ActOnFailureRule aof = new ActOnFailureRule() {
 *     &#64;Override
 *     protected void onFailure(Description description) {
 *       saveFailureData(description, "txt", "Some Data");
 *     }
 *   }
 *   ...
 * </p>
 * </pre>
 */
public abstract class ActOnFailureRule implements TestRule {
	@Override
	public Statement apply(final Statement base, final Description description) {
		return new Statement() {
			@Override
			public void evaluate() throws Throwable {
				try {
					base.evaluate();
				} catch (Throwable t) {
					try {
						onFailure(description);
					} catch(Throwable e) {
						// Ignore
					}
					throw t;
				}
			}
		};
	}

	/**
	 * Will be called when a test fails
	 * 
	 * Any exception thrown from this method will be silently ignored.
	 * 
	 * @param desc
	 * @throws Throwable
	 */
	protected abstract void onFailure(Description desc) throws Throwable;
	
	/**
	 * Saves test failure data to an "output" file in test report directory
	 * (eg <code>target/surefire-reports</code>)
	 * 
	 * Test output files will be picked up by OUnit report generator
	 * and displayed to the user if possible.
	 * 
	 * @param desc
	 * @param suffix
	 * @param data
	 * @throws IOException
	 */
	public void saveFailureData(Description desc, String suffix, String data)
			throws IOException {
		saveFailureData(desc, suffix, data.getBytes());
	}

	/**
	 * Saves test failure data to an "output" file in test report directory
	 * (eg <code>target/surefire-reports</code>)
	 *  
	 * Test output files will be picked up by OUnit report generator
	 * and displayed to the user if possible.
	 * 
	 * @param desc
	 * @param suffix
	 * @param data
	 * @throws IOException
	 */
	public void saveFailureData(Description desc, String suffix, byte[] data)
			throws IOException {
		String name = desc.getClassName() + "." + desc.getMethodName();
		saveOutputFile(name, suffix, data);
	}
}