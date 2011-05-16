package com.googlecode.ounit.test.moodle19;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import static com.googlecode.ounit.test.moodle19.MoodleParams.*;


public class CoursePage {
    private final WebDriver driver;
    
    @FindBy(linkText=quizName)
    private WebElement testQuizLink;
    
    @FindBy(xpath="//input[@type='hidden' and @name='edit']/../input[@type='submit']")
    private WebElement toggleEditButton;
    
    @FindBy(xpath="//option[contains(@value, 'add=quiz')]")
    private WebElement newQuiz;

	public CoursePage(WebDriver driver) {
		this.driver = driver;
	}
	
	public void toggleEditing() {
		toggleEditButton.click();
	}

	public QuizPage gotoTestQuiz() {
    	try {
    		testQuizLink.click();
        	return PageFactory.initElements(driver, QuizPage.class);
    	} catch(NoSuchElementException e) {
    		return createTestQuiz();
    	}
	}

	private QuizPage createTestQuiz() {
		toggleEditing();
		newQuiz.setSelected();
		
    	QuizEditPage page = PageFactory.initElements(driver, QuizEditPage.class);
    	return page.newQuiz(quizName);
	}
}