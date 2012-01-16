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

import org.junit.*;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;

public abstract class OunitSeleniumTest {
	protected static WebDriver driver;

	@Rule
	public SanitizeSeleniumExceptionsRule san = new SanitizeSeleniumExceptionsRule();
	
	@Rule
	public ScreenShotOnFailureRule ssf = new ScreenShotOnFailureRule(driver);

	@BeforeClass
	public static void openBrowser() {
		if(driver == null)
			driver = WebDriverFactory.newInstance();
	}

	@AfterClass
	public static void closeBrowser() {
		if(driver != null) {
			driver.quit();
			driver = null;
		}
	}

	/**
	 * Get the base URL used for navigating
	 * 
	 * Tries to find base URL from selenium.baseurl property
	 * if that fails, calls {@link #getDefaultBaseUrl()}
	 * 
	 * @return base URL
	 */
	protected final String getBaseUrl() {
		// Try to find baseURL from properties
		String baseUrl = WebDriverFactory.getBaseUrl();
		if(baseUrl != null) {
			return baseUrl;
		} else {
			// Not found, return default
			char p = File.separatorChar;
			String baseDir = System.getProperty("basedir");
			if (baseDir == null)
				baseDir = "teacher";
			else
				baseDir += p + "src";
			return new File(baseDir).toURI().toString();
		}
	}
	
	/**
	 * Constructs base URL that points to Maven basedir
	 * 
	 * Returns "teacher" if not running under Maven. Feel free to override this
	 * in your own tests.
	 * 
	 * @return a default URL
	 */
	protected String getDefaultBaseUrl() {
		char p = File.separatorChar;
		String baseDir = System.getProperty("basedir");
		if (baseDir == null)
			baseDir = "teacher";
		else
			baseDir += p + "src";
		return new File(baseDir).toURI().toString();
	}

	/**
	 * Navigate to a page using base URL
	 * 
	 * Navigates to a page in application under test. Will take base URL into
	 * account. Feel free to override it in your own tests.
	 * 
	 * @param page
	 */
	protected void gotoPage(String page) {
		driver.get(getBaseUrl() + "/" + page);
	}


	/**
	 * Compatibility function to accommodate Selenium IDE exported tests
	 * 
	 * @param by
	 * @return
	 */
    public boolean isElementPresent(By by) {
        try {
                driver.findElement(by);
                return true;
        } catch (NoSuchElementException e) {
                return false;
        }
    }
}
