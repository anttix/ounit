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

package com.googlecode.ounit.selenium;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.openqa.selenium.WebDriverException;

/**
 * A JUnit 4.x rule that can be used to sanitize Selenium exceptions.
 * <p>
 * Use the following code to activate:
 * </p>
 * <pre>
 * public class TestBase {
 *   protected static WebDriver driver;
 *   
 *   &#64;Rule
 *   public SanitizeSeleniumExceptionsRule san = new SanitizeSeleniumExceptionsRule();
 *   ...
 * </p>
 * </pre>
 */
public class SanitizeSeleniumExceptionsRule implements TestRule {
	@Override
	public Statement apply(final Statement base, final Description description) {
		return new Statement() {
			@Override
			public void evaluate() throws Throwable {
				try {
					base.evaluate();
				} catch (WebDriverException e) {
					String msg = e.getMessage();
					int idx = msg.indexOf("For documentation on this error");
					if(idx > 0)
						msg = msg.substring(0, idx);
					
					throw new AssertionError(msg);
				}
			}
		};
	}
}
