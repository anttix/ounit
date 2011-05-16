package com.googlecode.ounit.opaque;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

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