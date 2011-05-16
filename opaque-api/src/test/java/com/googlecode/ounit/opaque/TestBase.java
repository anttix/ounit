package com.googlecode.ounit.opaque;

import javax.xml.ws.Endpoint;

import org.junit.*;
import static org.junit.Assert.*;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

public class TestBase {
	public final static String serviceAddress = "http://localhost:9099/opaque";
	
	protected static WebDriver driver;
	private static Endpoint ep = null;
	
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

}