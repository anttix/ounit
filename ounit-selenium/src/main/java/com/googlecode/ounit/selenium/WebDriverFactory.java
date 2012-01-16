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

import java.util.HashMap;

import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import com.gargoylesoftware.htmlunit.IncorrectnessListener;
import com.gargoylesoftware.htmlunit.WebClient;

/**
 * A factory class that instantiates a WebDriver driven browser.
 * <p>
 * Normally used from JUnit 4.x tests like this:
 * </p>
 * <pre>
 * public class TestBase {
 *   protected static WebDriver driver;
 *   
 *   &#64;BeforeClass
 *   public static void openBrowser() {
 *     if(driver == null)
 *       driver = WebDriverFactory.newInstance();
 *   }
 *   
 *   &#64;AfterClass
 *   public static void closeBrowser() {
 *     if(driver != null)
 *       driver.close();
 *   }
 *   
 *   &#64;Test
 *   public void gotoHomePage() {
 *     driver.open(WebDriverFactory.getBaseUrl());
 *   }
 * }
 * </pre>
 *
 * The specific browser to be instantiated is controlled via a system property:
 * <i>selenium.browser</i>.
 * If no browser is specified, the factory will default to HTMLUnit.
 * <p>
 * The list of supported browsers:
 * </p>
 * <table>
 * <tr><th>Property value</th><th>Browser used</th></tr>
 * <tr><td>firefox</td><td>Mozilla Firefox</td></tr>
 * <tr><td>chrome</td><td>Google Chrome</td></tr>
 * <tr><td>ie</td><td>Internet Explorer</td></tr>
 * <tr><td>htmlunit</td><td>HTMLUnit</td></tr>
 * </table>
 * 
 * <p>
 * NOTE: For chrome to work, you need to install an additional
 * <a href="http://code.google.com/p/selenium/wiki/ChromeDriver">ChromeDriver</a>
 * binary.
 * </p>
 * <p>
 * To properly pass test properties from Maven,
 * you must add the following to the build section in pom.xml:
 * </p>
 * <pre>
 *  &lt;pluginManagement&gt;
 *      &lt;plugin&gt;
 *        &lt;artifactId&gt;maven-surefire-plugin&lt;/artifactId&gt;
 *        &lt;configuration&gt;
 *          &lt;systemPropertyVariables&gt;
 *            &lt;selenium.browser&gt;${selenium.browser}&lt;/selenium.browser&gt;
 *            &lt;selenium.baseurl&gt;${selenium.baseurl}&lt;/selenium.baseurl&gt;
 *          &lt;/systemPropertyVariables&gt;
 *        &lt;/configuration&gt;
 *      &lt;/plugin&gt;
 *    &lt;/plugins&gt;
 *  &lt;/pluginManagement&gt;
 * </pre>
 */

public class WebDriverFactory {
	public enum Browser {
		FIREFOX, CHROME, IE, HTMLUNIT
	};

	/**
	 * Creates a new WebDriver instance determined by the <i>selenium.browser</i> property.
	 * 
	 * @return new WebDriver instance.
	 * @see WebDriverFactory
	 */
	public static WebDriver newInstance() {
		HashMap<String, Browser> knownBrowsers = new HashMap<String, Browser>();
		knownBrowsers.put("firefox", Browser.FIREFOX);
		knownBrowsers.put("chrome", Browser.CHROME);
		knownBrowsers.put("ie", Browser.IE);
		knownBrowsers.put("htmlunit", Browser.HTMLUNIT);

		WebDriver rv;
		Exception err = null;
		String preferredBrowser = System.getProperty("selenium.browser",
				"htmlunit");
		Browser b = knownBrowsers.get(preferredBrowser.toLowerCase());

		if (b == null)
			throw new RuntimeException("Invalid selenium.browser property: "
					+ preferredBrowser);

		try {
			rv = newInstance(b);
			assert rv != null;
			return rv;
		} catch (Exception e) {
			throw new RuntimeException("Unable to create WebDriver instance",
					err);
		}
	}

	public static WebDriver newInstance(Browser b) {
		switch (b) {
		case FIREFOX:
			return new FirefoxDriver();
		case CHROME:
			return new ChromeDriver();
		case IE:
			return new InternetExplorerDriver();
		case HTMLUNIT:
			/* 
			 * Make sure we have a fully capable HtmlUnitDriver and
			 * silence the stupid warnings about text/javascript  
			 */
			HtmlUnitDriver rv = new HtmlUnitDriver(new DesiredCapabilities(
					"htmlunit", "firefox", Platform.ANY)) {
				@Override
				protected WebClient modifyWebClient(WebClient client) {
					final IncorrectnessListener delegate = client
							.getIncorrectnessListener();
					client.setIncorrectnessListener(new IncorrectnessListener() {
						@Override
						public void notify(String message, Object origin) {
							if (message.contains("Obsolete")
									&& message.contains("/javascript"))
								return;

							delegate.notify(message, origin);
						}
					});
					return super.modifyWebClient(client);
				}
			};
			return rv;
		default:
			throw new IllegalArgumentException("Invalid browser specified");
		}
	}
	/**
	 * Get the base URL specified in system property <i>selenium.baseurl</i>.
	 * 
	 * @return base URL for Selenium tests
	 * @see WebDriverFactory
	 */
	public static String getBaseUrl() {
		String baseUrl = System.getProperty("selenium.baseurl");		
		return baseUrl;
	}
}
