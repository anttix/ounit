package com.googlecode.ounit.test.moodle19;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;


public class QuizEditPage {
	private WebDriver driver;
	
	@FindBy(id="id_name")
	private WebElement name;
	@FindBy(xpath="//select[@name='questionsperpage']//option[@value='1']")
	private WebElement oneQuestionPerPage;
	@FindBy(id="id_submitbutton")
	private WebElement saveAndDisplay;
	
	public QuizEditPage(WebDriver driver) {
		this.driver = driver;
	}

	public QuizPage newQuiz(String name) {
		this.name.clear();
		this.name.sendKeys(name);
		oneQuestionPerPage.setSelected();
		saveAndDisplay.click();
		
		return PageFactory.initElements(driver, QuizPage.class);
	}
}
