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

package com.googlecode.ounit.opaque;

import javax.xml.ws.Endpoint;

import org.junit.*;
import static org.junit.Assert.*;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

public class TestBase {
	public final static String serviceAddress = "http://localhost:9099/opaque";
	
	protected static WebDriver driver;
	protected static Endpoint ep = null;
	
	@Rule
	public ScreenShotOnFailureRule ssf = new ScreenShotOnFailureRule(driver);

	public static void startServer() {
		if(ep == null) {
			MockOpaqueService implementor = new MockOpaqueService();
			ep = Endpoint.publish(serviceAddress, implementor);
			assertTrue("Service did not start", ep.isPublished());
			System.out.println("Mock server started");
		}
	}

	public static void openBrowser() {
	    if(driver == null)
	    	driver = new FirefoxDriver();
	}

	@AfterClass
	public static void stopServer() {
		if(ep != null)
			ep.stop();
	}

	@AfterClass
	public static void closeBrowser() {
		if(driver != null)
			driver.close();
	}

	public static void main(String [] args) {
    	startServer();
	}
}