package com.googlecode.ounit.test.moodle19;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

public class EngineEditPage {
	private WebDriver driver;
	private WebElement enginename;
	private WebElement questionengineurls;
	private WebElement submitbutton;

	public EngineEditPage(WebDriver driver) {
        this.driver = driver;
    }

	public EnginePage saveEngine(String name, String url) {
		enginename.clear();
		enginename.sendKeys(name);
		questionengineurls.clear();
		questionengineurls.sendKeys(url);
		submitbutton.submit();
		
		return PageFactory.initElements(driver, EnginePage.class);
	}
}
