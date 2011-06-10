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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

/**
 * A JUnit 4.x rule that can be used with selenium to take
 * screenshots of test failures (if the driver supports it).
 * <p>
 * Screenshots will be saved to <code>target/surefire-reports</code> directory.
 * </p>
 * <p>
 * Use the following code to activate:
 * </p>
 * <pre>
 * public class TestBase {
 *   protected static WebDriver driver;
 *   
 *   &#64;Rule
 *   public ScreenShotOnFailureRule ssf = new ScreenShotOnFailureRule(driver);
 *   ...
 * </p>
 * </pre>
 */
public class ScreenShotOnFailureRule implements MethodRule {
	private WebDriver driver;
	
	public ScreenShotOnFailureRule(WebDriver driver) {
		this.driver = driver;
	}

	public WebDriver getDriver() {
		return driver;
	}

	public void setDriver(WebDriver driver) {
		this.driver = driver;
	}

	@Override
	public Statement apply(final Statement base, final FrameworkMethod method,
			final Object target) {
		return new Statement() {
			@Override
			public void evaluate() throws Throwable {
				try {
					base.evaluate();
				} catch (Throwable t) {
					Class<?> c = method.getMethod().getDeclaringClass();
					takeScreenshot(c.getName() + "." + method.getName());
					throw t;
				}
			}
		};
	}
	
	protected void saveFile(String name, byte [] data) throws IOException {
		/* Find directory to save screenshots to */
		String basedir = System.getProperty("basedir");
		if(basedir != null) {
			basedir += "/target/";
		} else {
			basedir = "target/";
		}
		basedir += "surefire-reports";

		File dir = new File(basedir);
		dir.mkdirs();
		File f = new File(dir, name);
		FileOutputStream fs = new FileOutputStream(f);
		fs.write(data);
		fs.close();
	}

	protected void takeScreenshot(String baseName) {	
		try {
			byte[] data = ((TakesScreenshot)getDriver()).getScreenshotAs(OutputType.BYTES);
			saveFile(baseName + ".png", data);
		} catch(Throwable t) {
			// Ignore
		}
	}
}
