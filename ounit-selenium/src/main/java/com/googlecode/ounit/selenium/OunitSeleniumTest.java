package com.googlecode.ounit.selenium;

import java.io.File;

import org.junit.*;
import org.openqa.selenium.WebDriver;

public abstract class OunitSeleniumTest {
	static WebDriver driver;

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
		try {
			// Try to find baseURL from properties
			return WebDriverFactory.getBaseUrl();
		} catch (AssertionError e) {
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
}
