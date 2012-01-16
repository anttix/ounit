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

import org.junit.runner.Description;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import com.googlecode.ounit.junit.ActOnFailureRule;

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
public class ScreenShotOnFailureRule extends ActOnFailureRule {
	private WebDriver driver;
	private boolean sourceDumpEnabled = false; 
	private boolean screenShotsEnabled = true;

	public ScreenShotOnFailureRule(WebDriver driver) {
		this.driver = driver;
	}

	public WebDriver getDriver() {
		return driver;
	}

	public void setDriver(WebDriver driver) {
		this.driver = driver;
	}

	public boolean isScreenShotsEnabled() {
		return screenShotsEnabled;
	}

	public void setScreenShotsEnabled(boolean screenShotsEnabled) {
		this.screenShotsEnabled = screenShotsEnabled;
	}

	/**
	 * Check if source dumps are performed in case driver does
	 * not support screen shots.
	 * 
	 * @return
	 */
	public boolean isSourceDumpEnabled() {
		return sourceDumpEnabled;
	}

	/**
	 * Control if page source dumps are performed in case driver does
	 * not support screen shots.
	 * 
	 * @param sourceDumpEnabled
	 */
	public void setSourceDumpEnabled(boolean sourceDumpEnabled) {
		this.sourceDumpEnabled = sourceDumpEnabled;
	}
	
	@Override
	protected void onFailure(Description desc) throws Throwable {
		if(!screenShotsEnabled)
			return;

		WebDriver driver = getDriver();
		if (driver instanceof TakesScreenshot) {
			byte[] data = ((TakesScreenshot) getDriver())
					.getScreenshotAs(OutputType.BYTES);
			saveFailureData(desc, "png", data);
		} else {
			// Can't take screen shots. Save page source instead.
			if (sourceDumpEnabled)
				saveFailureData(desc, "html", getDriver()
						.getPageSource());
		}
	}
}
