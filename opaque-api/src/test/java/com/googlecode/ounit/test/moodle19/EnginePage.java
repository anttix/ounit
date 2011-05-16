package com.googlecode.ounit.test.moodle19;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import static com.googlecode.ounit.test.moodle19.MoodleParams.*;

public class EnginePage {
    private WebDriver driver;
    
    @FindBy(xpath = "//p[contains(text(), '" + engineName + "')]//a[contains(@href, 'editengine.php')]")
    private WebElement engineEditLink;
    @FindBy(xpath = "//a[@href = 'editengine.php']")
    private WebElement newEngineLink;
    
    @FindBy(xpath = "//p[contains(text(), '" + engineName + "')]//a[contains(@href, 'testengine.php')]")
    private WebElement testEngineLink;

	public EnginePage(WebDriver driver) {
        this.driver = driver;
    }
	
	public void setupEngineUrl(String url) {
		try {
			engineEditLink.click();
		} catch(NoSuchElementException e) {
			newEngineLink.click();
		}
		EngineEditPage page = PageFactory.initElements(driver, EngineEditPage.class);
		page.saveEngine(engineName, url);
	}
	
	public void testConnection() {
		testEngineLink.click();
	}
}
