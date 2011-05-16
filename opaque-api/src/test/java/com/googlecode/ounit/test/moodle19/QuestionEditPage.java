package com.googlecode.ounit.test.moodle19;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import static com.googlecode.ounit.test.moodle19.MoodleParams.*;


public class QuestionEditPage {
	private WebDriver driver;

	private WebElement name;
	private WebElement remoteid;
	private WebElement remoteversion;
	private WebElement submitbutton;
	
	@FindBy(xpath = "//option[text() = '" + engineName + "']")
	private WebElement testEngineSelect;
	
	public QuestionEditPage(WebDriver driver) {
		this.driver = driver;
	}
	
	public QuizPage createQuestion(String name, String remoteid, String version) {
		this.name.sendKeys(name);
		this.remoteid.sendKeys(remoteid);
		remoteversion.sendKeys(version);
		testEngineSelect.setSelected();
		submitbutton.click();
		if(driver.getCurrentUrl().contains("question.php")) // Still on edit page? Failed!
			throw new IllegalStateException("Question creation FAILED");
		
    	return PageFactory.initElements(driver, QuizPage.class);
	}
}
